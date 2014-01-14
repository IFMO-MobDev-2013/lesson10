package ru.ifmo.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by asus on 14.01.14.
 */
public class WeatherDatabase {

    private static final String TAG = "WeatherDatabase";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final String KEY_PIC = "PIC";


    private String DATABASE_NAME;
    private String DATABASE_TABLE;

    private String DATABASE_CREATE;

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public WeatherDatabase(Context ctx) {
        this.mCtx = ctx;
        DATABASE_NAME = "Data";
        DATABASE_TABLE = "WeatherDatabase";

        DATABASE_CREATE = "CREATE TABLE "+ DATABASE_TABLE + " (";
        for (int i = 0; i < WeatherCond.SqlTags.length; i++){
            DATABASE_CREATE += WeatherCond.SqlTags[i];
            if (i == 0) DATABASE_CREATE += " INTEGER PRIMARY KEY AUTOINCREMENT";
            if (i < WeatherCond.SqlTags.length - 1) DATABASE_CREATE += ", ";
        }
        DATABASE_CREATE += ", " + KEY_PIC + ");";

    }

    public WeatherDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long addItem(WeatherCond item) {
        ContentValues initialValues = new ContentValues();
        for (int i = 1; i < WeatherCond.SqlTags.length; i++){
            initialValues.put(WeatherCond.SqlTags[i], item.param[i]);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        item.pic.compress(Bitmap.CompressFormat.PNG, 0, bos);
        initialValues.put(KEY_PIC, bos.toByteArray());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteItem(WeatherCond item) {
        return mDb.delete(DATABASE_TABLE, WeatherCond.SqlTags[0] + "=" + item.param[WeatherCond.ID], null) > 0;
    }

    public boolean deleteAllItems(Location item) {
        return mDb.delete(DATABASE_TABLE, WeatherCond.SqlTags[WeatherCond.TOWN_ID] + "='" + item.param[Location.ID] + "'", null) > 0;
    }

    public ArrayList<WeatherCond> getAllItems(Location item) {
        Cursor cursor = mDb.query(DATABASE_TABLE, null, WeatherCond.SqlTags[WeatherCond.TOWN_ID] + "='" + item.param[Location.ID] + "'", null, null, null, WeatherCond.SqlTags[WeatherCond.ID] + " ASC");
        ArrayList<WeatherCond> items = new ArrayList<WeatherCond>();
        WeatherCond curItem = new WeatherCond();
        while (cursor.moveToNext()){
            curItem.clear();
            for (int i = 0; i < WeatherCond.SqlTags.length; i++) curItem.param[i] = cursor.getString(cursor.getColumnIndex(WeatherCond.SqlTags[i]));
            byte[] data = cursor.getBlob(cursor.getColumnIndex(KEY_PIC));
            curItem.pic = BitmapFactory.decodeByteArray(data, 0, data.length);
            items.add(curItem.makeCopy());
        }
        cursor.close();
        return items;
    }

    public WeatherCond getItem(int id) throws SQLException {

        Cursor cursor =
                mDb.query(true, DATABASE_TABLE, null, WeatherCond.SqlTags[0] + "=" + id, null,
                        null, null, null, null);

        if (cursor == null) {
            Log.e(TAG, "Error getting item");
            return null;
        }

        cursor.moveToFirst();
        WeatherCond item = new WeatherCond();
        for (int i = 0; i < WeatherCond.SqlTags.length; i++) item.param[i] = cursor.getString(cursor.getColumnIndex(WeatherCond.SqlTags[i]));
        byte[] data = cursor.getBlob(cursor.getColumnIndex(KEY_PIC));
        item.pic = BitmapFactory.decodeByteArray(data, 0, data.length);
        cursor.close();

        return item;
    }


}

