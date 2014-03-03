package com.example.lesson10;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 03.03.14
 * Time: 4:14
 * To change this template use File | Settings | File Templates.
 */
public class DbCity extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ItmoWeather";

    public static final String TABLE_NAME = "city";
    public static final String NAME = "name";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " ( _id integer primary key autoincrement, "
            + NAME + " TEXT, " + LAT + " TEXT, " + LON + " TEXT)";

    public DbCity(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL(dropDataBase());
            onCreate(db);
        }
    }

    String dropDataBase() {
        return "DROP TABLE IF EXISTS " + DB_NAME;
    }
}
