package com.example.weathr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import be.hcpl.android.forecast.model.ForecastResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sergey on 11/28/13.
 */
public class WeatherDBAdapter {
    private static final String LOG_TAG = "WeatherDBAdapter";

    public static final String TABLE_NAME_CITIES = "cities";

    //Cities table
    public static final  String KEY_ID              = "_id";
    public static final  String KEY_NAME            = "name";
    public static final  String KEY_LATITUDE        = "latitude";
    public static final  String KEY_LONGITUDE       = "longitude";
    public static final  String KEY_WEATHER         = "weather";
    public static final  String KEY_LAST_UPDATE     = "last_update";
    private static final String CREATE_TABLE_CITIES = "CREATE TABLE " + TABLE_NAME_CITIES + " (" +
                                                      KEY_ID + " INTEGER PRIMARY KEY, " +
                                                      KEY_NAME + " TEXT, " +
                                                      KEY_LATITUDE + " REAL, " +
                                                      KEY_LONGITUDE + " REAL, " +
                                                      KEY_WEATHER + " TEXT, " +
                                                      KEY_LAST_UPDATE + " INTEGER, " +
                                                      "UNIQUE (" + KEY_NAME + ") ON CONFLICT REPLACE)";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME    = "weather.db";
    private static final int    DATABASE_VERSION = 4;

    private final Context mCtx;

    public int batchDeleteCities(ArrayList<Long> idsToDelete) {
        int result = 0;
        mDb.beginTransaction();
        for (Long anIdsToDelete : idsToDelete)
            if (deleteCityById(anIdsToDelete)) result++;
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        return result;
    }

    public boolean deleteCityById(long id)
    {
        return mDb.delete(TABLE_NAME_CITIES, KEY_ID+"="+id, null)>0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE_CITIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                           + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CITIES);
            onCreate(db);
        }
    }

    public WeatherDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public WeatherDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor fetchAllCities() {
        return mDb.query(TABLE_NAME_CITIES,
                         new String[]{
                                 KEY_ID, KEY_NAME, KEY_LAST_UPDATE
                         }, null, null, null, null
                , KEY_NAME);
    }

    public long putCity(City c) {
        Gson gson = new Gson();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, c.name);
        cv.put(KEY_LATITUDE, c.latitude);
        cv.put(KEY_LONGITUDE, c.longitude);
        cv.put(KEY_WEATHER, gson.toJson(c.weather));
        if (c.id == -1) {
            cv.put(KEY_LAST_UPDATE, c.lastUpdate.getTime());
            return c.id = mDb.insert(TABLE_NAME_CITIES, null, cv);
        } else {
            cv.put(KEY_LAST_UPDATE, System.currentTimeMillis());
            return
                    mDb.update(TABLE_NAME_CITIES, cv, KEY_ID + "=" + c.id, null) > 0 ? c.id : -1;
        }
    }

    public City getCityById(long id) {
        Cursor c = mDb.query(TABLE_NAME_CITIES
                , new String[]{
                KEY_ID, KEY_NAME, KEY_LAST_UPDATE, KEY_LATITUDE, KEY_LONGITUDE, KEY_WEATHER}
                , KEY_ID + "=" + id
                , null, null, null, null, "1");
        if (!c.moveToFirst())
            return null;
        City result = new City();
        result.lastUpdate = new Date(c.getLong(c.getColumnIndex(KEY_LAST_UPDATE)));
        result.name = c.getString(c.getColumnIndex(KEY_NAME));
        result.latitude = c.getDouble(c.getColumnIndex(KEY_LATITUDE));
        result.longitude = c.getDouble(c.getColumnIndex(KEY_LONGITUDE));
        Gson gson = new Gson();
        result.weather = gson.fromJson(c.getString(c.getColumnIndex(KEY_WEATHER)), ForecastResponse.class);
        c.close();
        result.id = id;
        return result;
    }
}
