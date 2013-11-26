package ru.zulyaev.ifmo.zeather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author seidhe
 */
public class WeatherDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "weather";
    private static final int VERSION = 3;

    public WeatherDbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        WeatherTable.init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        WeatherTable.drop(db);

        onCreate(db);
    }
}
