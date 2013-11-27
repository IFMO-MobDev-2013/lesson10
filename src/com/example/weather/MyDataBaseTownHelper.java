package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseTownHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME_NO_ID = "weatherdb";
    public static final String DATE = "date";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String WIND_DIRECTION = "wind_direction";
    public static final String WIND_SPEED = "wind_speed";
    public static final String LOW_TEMPERATURE = "low_temperature";
    public static final String HIGH_TEMPERATURE = "high_temperature";
    public static final String CLOUDS = "clouds";
    public static final String IMAGE = "image";

    public String town_woeid = "";

    public static final String CREATE_DATABASE_BEFORE_ID = "CREATE TABLE " + DATABASE_NAME_NO_ID;

    public static final String CREATE_DATABASE_AFTER_ID = " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DATE + " TEXT," + HUMIDITY + " TEXT," + PRESSURE + " TEXT," + WIND_DIRECTION + " TEXT," +
            WIND_SPEED + " TEXT," + LOW_TEMPERATURE + " TEXT," + HIGH_TEMPERATURE + " TEXT," + CLOUDS + " TEXT,"
            + IMAGE + " INTEGER);";


    public static final String DROP_DATABASE_NO_ID = "DROP TABLE IF EXISTS " + DATABASE_NAME_NO_ID;

    public MyDataBaseTownHelper(Context context, String woeid) {
        super(context, DATABASE_NAME_NO_ID + woeid, null, DATABASE_VERSION);
        town_woeid = woeid;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("sq create =" + town_woeid);
        db.execSQL(CREATE_DATABASE_BEFORE_ID + town_woeid + CREATE_DATABASE_AFTER_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME_NO_ID + town_woeid);
            onCreate(db);
        }
    }
}