package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.ifmo.ctddev.skripnikov.Weather2.SQLiteHelper.*;

import java.util.ArrayList;

public class DBStorage {
    private SQLiteDatabase database;

    public DBStorage(Context context) {
        SQLiteHelper helper = new SQLiteHelper(context);
        database = helper.getWritableDatabase();
    }

    public void destroy() {
        database.close();
    }

    public City[] getCities() {
        Cursor cursor = null;
        try {
            cursor = database.query(SQLiteHelper.TABLE_CITIES,
                    new String[]{CityCols.ID, CityCols.NAME, CityCols.REGION, CityCols.COUNTRY,
                            CityCols.LATITUDE, CityCols.LONGITUDE, CityCols.WEATHER},
                    null, null, null, null, null);
            ArrayList<City> answer = new ArrayList<City>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                answer.add(new City(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getFloat(4), cursor.getFloat(5), cursor.getString(6)));
                cursor.moveToNext();
            }
            return answer.toArray(new City[answer.size()]);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public long addCity(City city) {
        ContentValues values = new ContentValues();
        values.put(CityCols.CODE, city.name + city.region + city.country);
        values.put(CityCols.NAME, city.name);
        values.put(CityCols.REGION, city.region);
        values.put(CityCols.COUNTRY, city.country);
        values.put(CityCols.LATITUDE, city.latitude);
        values.put(CityCols.LONGITUDE, city.longitude);
        return database.insert(SQLiteHelper.TABLE_CITIES, null, values);
    }

    public boolean updateWeather(City city, String weather) {
        ContentValues values = new ContentValues();
        String where = CityCols.ID + "=?";
        values.put(CityCols.WEATHER, weather);
        return database.update(SQLiteHelper.TABLE_CITIES, values, where, new String[]{Long.toString(city.id)}) > 0;
    }
}
