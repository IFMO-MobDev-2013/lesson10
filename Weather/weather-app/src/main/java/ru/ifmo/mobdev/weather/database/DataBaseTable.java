package ru.ifmo.mobdev.weather.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.mobdev.weather.weather.Forecast;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class DataBaseTable {
    public static final String TABLE_NAME = "weather";
    public static final String CITY_NAME = "name";
    public static final String COUNTRY = "country";
    public static final String DATA = "data";
    public static final String LAST_UPDATE = "last_update";
    public static final String IS_SELECTED = "is_selected";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private static final String CREATE_TABLE = String.format(
            "create table %s (" +
                    "_id integer not null primary key autoincrement," +
                    "%s text not null," +
                    "%s text default null," +
                    "%s text default null," +
                    "%s integer default 0," +
                    "%s integer default 0," +
                    "%s real default 0.0," +
                    "%s real default 0.0" +
                    ")",
            TABLE_NAME,
            CITY_NAME,
            COUNTRY,
            DATA,
            LAST_UPDATE,
            IS_SELECTED,
            LATITUDE,
            LONGITUDE
    );

    private static final String[] INITIAL_DATA = {
            "Saint Petersburg",
            "Moscow"
    };

    private static final String DROP_TABLE_QUERY = "drop table if exists " + TABLE_NAME;
    private static final String SELECT_ALL_QUERY = "select * from " + TABLE_NAME;
    private static final String SELECT_CITY_QUERY = "select * from " + TABLE_NAME + " todo where _id = ?";
    private static final String SELECT_CITY = "select * from " + TABLE_NAME + " todo where " + IS_SELECTED + " = ?";

    public static void init(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues(6);
        CityInformation cityInformation = new CityInformation(1, "Saint Petersburg", "Russia", null, 0, 1, 59.894, 30.264);
        values.put(CITY_NAME, cityInformation.getCityName());
        values.put(COUNTRY, cityInformation.getCountry());
        values.put(DATA, GSON.toJson(cityInformation.getForecast()));
        values.put(LAST_UPDATE, cityInformation.getLastUpdate());
        values.put(IS_SELECTED, cityInformation.getSelected());
        values.put(LATITUDE, cityInformation.getLatitude());
        values.put(LONGITUDE, cityInformation.getLongitude());
        db.insert(TABLE_NAME, null, values);
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_QUERY);
    }

    private static final Gson GSON = new Gson();
    private final SQLiteDatabase db;

    public DataBaseTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void insert(CityInformation cityInformation) {
        ContentValues values = new ContentValues(6);
        values.put(CITY_NAME, cityInformation.getCityName());
        values.put(COUNTRY, cityInformation.getCountry());
        values.put(DATA, GSON.toJson(cityInformation.getForecast()));
        values.put(LAST_UPDATE, cityInformation.getLastUpdate());
        values.put(IS_SELECTED, cityInformation.getSelected());
        values.put(LATITUDE, cityInformation.getLatitude());
        values.put(LONGITUDE, cityInformation.getLongitude());
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public void updateForecast(long id, Forecast forecast, long updateTime) {
        ContentValues values = new ContentValues(2);
        values.put(DATA, GSON.toJson(forecast));
        values.put(LAST_UPDATE, updateTime);
        db.update(TABLE_NAME, values, "_id = ?", new String[]{ Long.toString(id) });
    }

    public void updateSelection(long id, int select) {
        ContentValues value = new ContentValues(1);
        value.put(IS_SELECTED, select);
        db.update(TABLE_NAME, value, "_id = ?", new String[]{ Long.toString(id) });
    }

    public void deleteCity(long id) {
        if (getSelected().getId() == id) {
            updateSelection(1, 1);
        }
        db.delete(TABLE_NAME, "_id = ?", new String[]{ Long.toString(id)});
    }

    public void updateForecast(long id, Forecast forecast) {
        updateForecast(id, forecast, System.currentTimeMillis());
    }

    public CityInformation getSelected() {
        Cursor cursor = null;
        CityInformation result = null;
        try {
            cursor = db.rawQuery(SELECT_CITY, new String[] {"1"});
            if (cursor != null) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                String cName = cursor.getString(1);
                String country = cursor.getString(2);
                String json = cursor.getString(3);
                long updated = cursor.getLong(4);
                int is_selected = cursor.getInt(5);
                double latitude = cursor.getDouble(6);
                double longitude = cursor.getDouble(7);

                result = new CityInformation(
                        id,
                        cName,
                        country,
                        json == null ? null : GSON.fromJson(json, Forecast.class),
                        updated,
                        is_selected,
                        latitude,
                        longitude
                );
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
                return result;
            }
        }
    }

    public List<CityInformation> getAll() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_ALL_QUERY, null);
            List<CityInformation> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String cName = cursor.getString(1);
                String country = cursor.getString(2);
                String json = cursor.getString(3);
                long updated = cursor.getLong(4);
                int is_selected = cursor.getInt(5);
                double latitude = cursor.getDouble(6);
                double longitude = cursor.getDouble(7);

                result.add( new CityInformation(
                        id,
                        cName,
                        country,
                        json == null ? null : GSON.fromJson(json, Forecast.class),
                        updated,
                        is_selected,
                        latitude,
                        longitude
                ));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public CityInformation getForecast(long rowId) throws SQLException {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_CITY_QUERY, new String[] {Long.toString(rowId)});
            CityInformation result = null;
            if (cursor != null) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                String cName = cursor.getString(1);
                String json = cursor.getString(3);
                long updated = cursor.getLong(4);
                int is_selected = cursor.getInt(5);

                result = new CityInformation(
                        id,
                        cName,
                        json == null ? null : GSON.fromJson(json, Forecast.class),
                        updated,
                        is_selected
                );
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void addLocation(String location) {
        ContentValues values = new ContentValues(1);
        values.put(CITY_NAME, location);
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    
}
