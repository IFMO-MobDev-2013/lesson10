package com.example.MeWeather;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ViruZ on 07.01.14.
 */
public class DBWeather {
    public static final String TABLE_NAME = "weather";
    public static final String CITY_NAME = "name";
    public static final String DATA = "data";
    public static final String LAST_UPDATE = "last_update";
    public static final String IS_SELECTED = "is_selected";
    private static final Gson GSON = new Gson();

    private static final String CREATE_TABLE = String.format(
            "create table %s (" +
                    "_id integer not null primary key autoincrement," +
                    "%s text not null," +
                    "%s text default null," +
                    "%s integer default 0," +
                    "%s integer default 0" +
                    ")",
            TABLE_NAME,
            CITY_NAME,
            DATA,
            LAST_UPDATE,
            IS_SELECTED
    );

    private static final String DROP_TABLE_QUERY = "drop table if exists " + TABLE_NAME;
    private static final String SELECT_ALL_QUERY = "select * from " + TABLE_NAME;
    private static final String SELECT_CITY_QUERY = "select * from " + TABLE_NAME + " todo where _id = ?";
    private static final String SELECT_CITY = "select * from " + TABLE_NAME + " todo where " + IS_SELECTED + " = ?";

    public static void init(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues(6);
        DBCityInform dbCityInform = new DBCityInform(1, "Saint Petersburg", GSON.fromJson("{\"forecast\":[{\"windDirection\":\"NW\",\"date\":\"Jan 11, 2014 12:00:00 AM\",\"maxTemp\":-4,\"minTemp\":-7,\"code\":326,\"windSpeed\":26},{\"windDirection\":\"NNW\",\"date\":\"Jan 12, 2014 12:00:00 AM\",\"maxTemp\":-4,\"minTemp\":-10,\"code\":122,\"windSpeed\":21},{\"windDirection\":\"NNW\",\"date\":\"Jan 13, 2014 12:00:00 AM\",\"maxTemp\":-6,\"minTemp\":-10,\"code\":122,\"windSpeed\":22},{\"windDirection\":\"NNW\",\"date\":\"Jan 14, 2014 12:00:00 AM\",\"maxTemp\":-4,\"minTemp\":-10,\"code\":122,\"windSpeed\":17}],\"today\":{\"windDirection\":\"SSW\",\"date\":\"Jan 10, 2014 12:53:23 AM\",\"maxTemp\":4,\"minTemp\":4,\"code\":122,\"windSpeed\":11}}", Forecast.class), 0, 1);
        values.put(CITY_NAME, dbCityInform.getCityName());
        values.put(DATA, GSON.toJson(dbCityInform.getForecast()));
        values.put(LAST_UPDATE, dbCityInform.getLastUpdate());
        values.put(IS_SELECTED, dbCityInform.getSelected());
        db.insert(TABLE_NAME, null, values);
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_QUERY);
    }


    private final SQLiteDatabase db;

    public DBWeather(SQLiteDatabase db) {
        this.db = db;
    }

    public void insert(DBCityInform dbCityInform) {
        ContentValues values = new ContentValues(6);
        values.put(CITY_NAME, dbCityInform.getCityName());
        values.put(DATA, GSON.toJson(dbCityInform.getForecast()));
        values.put(LAST_UPDATE, dbCityInform.getLastUpdate());
        values.put(IS_SELECTED, dbCityInform.getSelected());
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public void updateForecast(long id, Forecast forecast, long updateTime) {
        ContentValues values = new ContentValues(2);
        values.put(DATA, GSON.toJson(forecast));
        values.put(LAST_UPDATE, updateTime);
        db.update(TABLE_NAME, values, "_id = ?", new String[]{Long.toString(id)});
    }

    public void updateSelection(long id, int select) {
        ContentValues value = new ContentValues(1);
        value.put(IS_SELECTED, select);
        db.update(TABLE_NAME, value, "_id = ?", new String[]{Long.toString(id)});
    }

    public void deleteCity(long id) {
        if (getAll().size() > 1) {
            if (getSelected().getId() == id && !(id == getAll().get(0).getId())) {
                updateSelection(getAll().get(0).getId(), 1);
            }
            if (getSelected().getId() == id && (id == getAll().get(0).getId())) {
                updateSelection(getAll().get(1).getId(), 1);
            }

            db.delete(TABLE_NAME, "_id = ?", new String[]{Long.toString(id)});
        }
    }


    public DBCityInform getSelected() {
        Cursor cursor = null;
        DBCityInform result = null;
        try {
            cursor = db.rawQuery(SELECT_CITY, new String[]{"1"});
            if (cursor != null) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                String cName = cursor.getString(1);
                String json = cursor.getString(2);
                long updated = cursor.getLong(3);
                int is_selected = cursor.getInt(4);

                result = new DBCityInform(
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
                return result;
            }
        }
    }

    public List<DBCityInform> getAll() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_ALL_QUERY, null);
            List<DBCityInform> result = new ArrayList<DBCityInform>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String cName = cursor.getString(1);
                String json = cursor.getString(2);
                long updated = cursor.getLong(3);
                int is_selected = cursor.getInt(4);

                result.add(new DBCityInform(
                        id,
                        cName,
                        json == null ? null : GSON.fromJson(json, Forecast.class),
                        updated,
                        is_selected
                ));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateLocation(DBCityInform dbCityInform) {
        ContentValues values = new ContentValues(3);
        values.put(CITY_NAME, dbCityInform.getCityName());
        values.put(DATA, GSON.toJson(dbCityInform.getForecast()));
        values.put(LAST_UPDATE, dbCityInform.getLastUpdate());
        db.update(TABLE_NAME, values, "_id = ?", new String[]{"1"});
    }

}
