package com.example.GoodWeather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    public static final String TABLE_ID = "_id";
    private static final String DATABASE_NAME = "cities";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "city_table";
    public static final String CITY = "city";
    public static final String LONGTITUDE = "longtitude";
    public static final String LATITUDE = "latitude";
    public static final String COUNTRY = "country";

    private static final String SQL_CREATE_ENTRIES = "create table "
            + TABLE_NAME + " ("
            + TABLE_ID + " integer primary key autoincrement, "
            + CITY + " text not null, "
            + LONGTITUDE + " text not null, "
            + COUNTRY + " text not null, "
            + LATITUDE + " text not null ); ";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    private final Context mcontext;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context context) {
        this.mcontext = context;
        DBHelper = new DatabaseHelper(mcontext);
        db = DBHelper.getWritableDatabase();
    }


    public void insert(String city, String longtitude, String latitude, String country) {
        ContentValues cv =  new ContentValues();
        Cursor cursor = db.query(TABLE_NAME, null, CITY + " = ?", new String[]{city}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cv.put(CITY, city);
            cv.put(LONGTITUDE, longtitude);
            cv.put(LATITUDE, latitude);
            cv.put(COUNTRY, country);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public Cursor getAllData() {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public String[] getAllCities() {
        Cursor cursor = getAllData();
        int size = -1;
        if (cursor.getCount() <= 0) return null;
        String[] result = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex(CITY));
            size++;
            result[size] = city;
        }
        return result;
    }

    public String[] getAllCountries() {
        Cursor cursor = getAllData();
        int size = -1;
        if (cursor.getCount() <= 0) return null;
        String[] result = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex(COUNTRY));
            size++;
            result[size] = city;
        }
        return result;
    }

    public String getCoor(String city) {
        Cursor cursor = db.query(TABLE_NAME, null, CITY + " = ?", new String[]{city}, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(LATITUDE)) + "," + cursor.getString(cursor.getColumnIndex(LONGTITUDE));
    }

    public void deleteCity(String city) {
        city = city.substring(0, city.indexOf(","));
        db.delete(TABLE_NAME,CITY + " = ?", new String[]{city});
    }
    public boolean check(String city) {
        Cursor cursor = db.query(TABLE_NAME, null, CITY + " = ?", new String[]{city}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) return false;
        else return true;
    }
    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
