package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME = "weather_db";
    public static final String TOWN_NAME = "town_name";
    public static final String DAY = "day";
    public static final String PART = "part";
    public static final String TEMPERATURE_FROM = "temperature_from";
    public static final String TEMPERATURE_TO = "temperature_to";
    public static final String WEATHER_TYPE = "weather_type";
    public static final String WIND = "wind";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String IMAGE = "image";

    public static final String CREATE_DATABASE = "CREATE TABLE " + DATABASE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOWN_NAME + " TEXT, " + DAY + " TEXT, " + PART + " TEXT, " + TEMPERATURE_FROM + " TEXT, " + TEMPERATURE_TO + " TEXT, " + WEATHER_TYPE + " TEXT, " + WIND + " TEXT, " + HUMIDITY + " TEXT, " + PRESSURE + " TEXT, " + IMAGE + " TEXT" + ");";

    public static final String DROP_DATABASE = "DROP TABLE IF EXISTS " + DATABASE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) {
        if (old_v != new_v) {
            db.execSQL(DROP_DATABASE);
            onCreate(db);
        }
    }
}

