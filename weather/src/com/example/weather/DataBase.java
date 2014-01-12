package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Charm on 02.01.14.
 */
public class DataBase extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "weather_database17.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "weather_table";

    public static final String CITYID = "cityid";
    public static final String CITYNAME = "cityname";
    public static final String TEMPERATURE = "temperature";
    public static final String DATE = "date";
    public static final String SKY = "sky";
    public static final String SUNRISE = "sunrise";
    public static final String SUNSET = "sunset";
    public static final String SPEED = "speed";
    public static final String DIRECTION = "direction";
    public static final String HUMIDITY = "humidity";
    public static final String DAY1 = "day1";
    public static final String DAY2 = "day2";
    public static final String DAY3 = "day3";
    public static final String IMAGE = "image";





    private static final String SQL_CREATE_ENTRIES = "create table "
            + TABLE_NAME + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " +
            CITYID + " text, " +
            CITYNAME + " text, " +
            TEMPERATURE + " text, " +
            DATE + " text, " +
            SKY + " text, " +
            SUNRISE + " text, " +
            SUNSET + " text, " +
            SPEED + " text, " +
            DIRECTION + " text, " +
            HUMIDITY + " text, " +
            DAY1 + " text, " +
            DAY2 + " text, " +
            DAY3 + " text, " +
            IMAGE + " text);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;


    DataBase(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

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
