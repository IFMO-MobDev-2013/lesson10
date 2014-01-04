package com.example.Weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Alexei
 * Date: 28.11.13
 * Time: 1:11
 * To change this template use File | Settings | File Templates.
 */
public class WeatherDataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME = "weatherdb";
    public static final String DATE = "date";
    public static final String WIND_SPEED = "wind_speed";
    public static final String LOW_TEMPERATURE = "low_temperature";
    public static final String HIGH_TEMPERATURE = "high_temperature";
    public static final String WIND_DIRECTION = "wind_direct";
    public static final String WEATHER_CODE = "weather_code";
    public static final String WEATHER_DESCRIPTION = "weather_description";

    String name;

    WeatherDataBaseHelper(Context context, String new_name) {
        super(context, DATABASE_NAME + new_name, null, DATABASE_VERSION);
        name = prepair(new_name);
    }

    String prepair(String source) {
        String result = "";
        source = source.toLowerCase();
        for (int i = 0; i < source.length(); i++)
            if (Character.isLetter(source.charAt(i)))
                result += source.charAt(i);
        return result;
    }

    public String createDatabase() {
        return "CREATE TABLE " + DATABASE_NAME + name + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DATE + " TEXT," + WIND_SPEED + " TEXT," + WIND_DIRECTION + " TEXT," + WEATHER_CODE + " TEXT," + WEATHER_DESCRIPTION + " TEXT," + LOW_TEMPERATURE + " TEXT," + HIGH_TEMPERATURE + " TEXT);";
    }

    public String dropDatabase() {
        return "DROP TABLE IF EXISTS " + DATABASE_NAME + name;
    }

    public String dataBaseName() {
        return DATABASE_NAME + name;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDatabase());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL(dropDatabase());
            onCreate(db);
        }
    }
}
