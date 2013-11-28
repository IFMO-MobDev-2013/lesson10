package ru.ifmo.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 26.11.13
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */

public class TownDatabase {

    public static final String KEY_TOWN = "town";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUTE = "longitude";


    private static final String TAG = "TownDatabase";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    private static final String DATABASE_NAME = "TownData";
    private static final String DATABASE_TABLE = "TownDatabase";

    private static final String DATABASE_CREATE = "CREATE TABLE "
            + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TOWN + ", " + KEY_COUNTRY + ", " + KEY_LATITUDE + ", " + KEY_LONGITUTE + ");";

    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

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

    public TownDatabase(Context ctx) {
        this.mCtx = ctx;
    }

    public TownDatabase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long addTown(Location town) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TOWN, town.param[Location.TOWN]);
        initialValues.put(KEY_COUNTRY, town.param[Location.COUNTRY]);
        initialValues.put(KEY_LATITUDE, town.param[Location.LATITUDE]);
        initialValues.put(KEY_LONGITUTE, town.param[Location.LONGITUDE]);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    public boolean deleteTown(Location town) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + town.param[Location.ID], null) > 0;
    }


    public ArrayList<Location> getAllTowns() {
        Cursor cursor = mDb.query(DATABASE_TABLE, null, null, null, null, null, null);
        ArrayList<Location> locs = new ArrayList<Location>();
        Location curLoc = new Location();
        while (cursor.moveToNext()){
            curLoc.clear();
            curLoc.param[Location.ID] = cursor.getString(cursor.getColumnIndex(KEY_ROWID));
            curLoc.param[Location.TOWN] = cursor.getString(cursor.getColumnIndex(KEY_TOWN));
            curLoc.param[Location.COUNTRY] = cursor.getString(cursor.getColumnIndex(KEY_COUNTRY));
            curLoc.param[Location.LATITUDE] = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
            curLoc.param[Location.LONGITUDE] = cursor.getString(cursor.getColumnIndex(KEY_LONGITUTE));
            locs.add(curLoc.makeCopy());
        }
        cursor.close();
        return locs;
    }

    public Location getTown(int id) throws SQLException {

        Cursor cursor =

                mDb.query(true, DATABASE_TABLE, null, KEY_ROWID + "=" + id, null,
                        null, null, null, null);

        if (cursor == null) {
            Log.e(TAG, "Error getting town");
            return null;
        }

        cursor.moveToFirst();
        Location loc = new Location();
        loc.param[Location.ID] = cursor.getString(cursor.getColumnIndex(KEY_ROWID));
        loc.param[Location.TOWN] = cursor.getString(cursor.getColumnIndex(KEY_TOWN));
        loc.param[Location.COUNTRY] = cursor.getString(cursor.getColumnIndex(KEY_COUNTRY));
        loc.param[Location.LATITUDE] = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
        loc.param[Location.LONGITUDE] = cursor.getString(cursor.getColumnIndex(KEY_LONGITUTE));
        cursor.close();

        return loc;
    }

    /*public boolean updateSubj(String oldName, String name, int score) {
        ContentValues args = new ContentValues();
        args.put(KEY_TOWN, name);
        args.put(KEY_COUNTRY, score);

        return mDb.update(DATABASE_TABLE, args, KEY_TOWN + "='" + oldName + "'", null) > 0;
    } */

}
