package ru.zulyaev.ifmo.zeather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;
import ru.zulyaev.ifmo.zeather.weather.Forecast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author seidhe
 */
public class WeatherTable {
    public static final String TABLE_NAME = "weather";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_LAST_UPDATED = "updated";

    private static final String CREATE_TABLE_QUERY = String.format(
            "create table %s (" +
                    "_id integer not null primary key autoincrement," +
                    "%s text not null," +
                    "%s real not null," +
                    "%s real not null," +
                    "%s text default null," +
                    "%s integer default 0" +
            ")",
            TABLE_NAME,
            FIELD_LOCATION,
            FIELD_LATITUDE,
            FIELD_LONGITUDE,
            FIELD_DATA,
            FIELD_LAST_UPDATED
    );

    private static final String DROP_TABLE_QUERY = "drop table if exists " + TABLE_NAME;
    private static final String SELECT_ALL_QUERY = "select * from " + TABLE_NAME;

    public static void init(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_QUERY);
    }

    private static final Gson GSON = new Gson();
    private final SQLiteDatabase db;

    public WeatherTable(SQLiteDatabase db) {
        this.db = db;
    }

    public List<WeatherForecast> getAll() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_ALL_QUERY, null);
            List<WeatherForecast> result = new ArrayList<WeatherForecast>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String location = cursor.getString(1);
                double latitude = cursor.getDouble(2);
                double longitude = cursor.getDouble(3);
                String json = cursor.getString(4);
                long updated = cursor.getLong(5);

                result.add(new WeatherForecast(
                        id,
                        location,
                        latitude,
                        longitude,
                        json == null ? null : GSON.fromJson(json, Forecast.class),
                        updated
                ));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateForecast(long id, Forecast forecast, long updateTime) {
        ContentValues values = new ContentValues(2);
        values.put(FIELD_DATA, GSON.toJson(forecast));
        values.put(FIELD_LAST_UPDATED, updateTime);
        db.update(TABLE_NAME, values, "_id = ?", new String[]{ Long.toString(id) });
    }

    public void updateForecast(long id, Forecast forecast) {
        updateForecast(id, forecast, System.currentTimeMillis());
    }

    public void addLocation(String location, double latitude, double longitude) {
        ContentValues values = new ContentValues(3);
        values.put(FIELD_LOCATION, location);
        values.put(FIELD_LATITUDE, latitude);
        values.put(FIELD_LONGITUDE, longitude);
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public void removeLocation(long id) {
        db.delete(TABLE_NAME, "_id = ?", new String[] {Long.toString(id)});
    }
}
