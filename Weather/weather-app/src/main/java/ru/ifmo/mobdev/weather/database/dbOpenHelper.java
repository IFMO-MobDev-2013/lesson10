package ru.ifmo.mobdev.weather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class dbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "weather.db";
    private static final int VERSION = 5;

    public dbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataBaseTable.init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataBaseTable.drop(db);

        onCreate(db);
    }
}
