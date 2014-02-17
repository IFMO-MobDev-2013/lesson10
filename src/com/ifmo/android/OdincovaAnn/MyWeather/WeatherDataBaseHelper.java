package com.ifmo.android.OdincovaAnn.MyWeather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class WeatherDataBaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATA_BASE_NAME = "HWAndroidWeather.db";

    public static final String TABLE_NAME = "CitiesTable";
    public static final String CITY = "city";
    public static final String SELECTED = "selected";
    public static final int DATA_BASE_VERSION = 2;

    public static final String TEMP = "temp_C";
    public static final String WEATHER_CODE = "weatherCode";
    public static final String WIND_SPEED = "windspeedKmph";
    public static final String WIND_DIR = "winddir16Point";
    public static final String PRECIP_MM = "precipMM";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String CLOUD_COVER = "cloudcover";
    public static final String DATE = "date";
    public static final String TEMP_MAX = "tempMaxC";
    public static final String TEMP_MIN = "tempMinC";
    public static final String NIGHT = "night";

    public static final String CREATE_MAIN_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + WeatherDataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             CITY + " TEXT, " + SELECTED + " INTEGER);";

    public static final String DELETE_MAIN_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    WeatherDataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MAIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_MAIN_TABLE);
        onCreate(db);
    }
}
