package com.mikhov.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weathers";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table towns (_id integer primary key autoincrement, code text, town text, " +
            "temperature1 text, weather1 text, sunrise1 text, sunset1 text, pressure1 text, image1 integer, img1 blob, " +
            "temperature2 text, weather2 text, sunrise2 text, sunset2 text, pressure2 text, image2 integer, img2 blob, " +
            "temperature3 text, weather3 text, sunrise3 text, sunset3 text, pressure3 text, image3 integer, img3 blob);";
    private static final String DATABASE_CREATE_YA = "create table yandex (_id integer primary key autoincrement, code text, town text);";
    private static final String DATABASE_CREATE_C = "create table c (_id integer primary key autoincrement, c text);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_YA);
        database.execSQL(DATABASE_CREATE_C);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS towns");
        database.execSQL("DROP TABLE IF EXISTS yandex");
        database.execSQL("DROP TABLE IF EXISTS c");
        onCreate(database);
    }
}
