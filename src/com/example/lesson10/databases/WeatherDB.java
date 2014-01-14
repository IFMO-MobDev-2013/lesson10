package com.example.lesson10.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.lesson10.Weather.DayForecast;
import com.example.lesson10.Weather.Forecast;

import java.util.ArrayList;

public class WeatherDB {

    public static final String DB_NAME = "weather";
    public static final int DB_VERSION = 1;
    public static final String DB_TABLE = "cities";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TEMPRATURE = "temprature";
    public static final String COLUMN_WEATHER = "weather";
    public static final String COLUMN_DATE = "date";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_CITY + " text not null, " +
                    COLUMN_TEMPRATURE + " text not null, " +
                    COLUMN_WEATHER + " text not null, " +

                    COLUMN_DATE + "0 text not null," +
                    COLUMN_TEMPRATURE + "0 text not null," +
                    COLUMN_WEATHER + "0 text not null," +

                    COLUMN_DATE + "1 text not null," +
                    COLUMN_TEMPRATURE + "1 text not null," +
                    COLUMN_WEATHER + "1 text not null," +

                    COLUMN_DATE + "2 text not null," +
                    COLUMN_TEMPRATURE + "2 text not null," +
                    COLUMN_WEATHER + "2 text not null" +
                    ");";

    private final Context context;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public WeatherDB(Context context) {
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    public ArrayList<Forecast> getAllData() {
        Cursor cursor = mDB.query(DB_TABLE,
                new String[]{
                        COLUMN_ID,
                        COLUMN_CITY,
                        COLUMN_TEMPRATURE,
                        COLUMN_WEATHER,

                        COLUMN_DATE + "0",
                        COLUMN_TEMPRATURE + "0",
                        COLUMN_WEATHER + "0",

                        COLUMN_DATE + "1",
                        COLUMN_TEMPRATURE + "1",
                        COLUMN_WEATHER + "1",

                        COLUMN_DATE + "2",
                        COLUMN_TEMPRATURE + "2",
                        COLUMN_WEATHER + "2",

                }, null, null, null, null, null);
        ArrayList<Forecast> result = new ArrayList<Forecast>();
        while (cursor.moveToNext()) {
            Forecast forecast = new Forecast();
            forecast.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
            forecast.setTemp(cursor.getString(cursor.getColumnIndex(COLUMN_TEMPRATURE)));
            forecast.setWeather(cursor.getString(cursor.getColumnIndex(COLUMN_WEATHER)));

            for (int i = 0; i < 3; i++) {
                DayForecast forecastWeather = new DayForecast();
                forecastWeather.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE + String.valueOf(i))));
                forecastWeather.setTemp(cursor.getString(cursor.getColumnIndex(COLUMN_TEMPRATURE + String.valueOf(i))));
                forecastWeather.setWeather(cursor.getString(cursor.getColumnIndex(COLUMN_WEATHER + String.valueOf(i))));
                forecast.addForecast(forecastWeather);
            }
            result.add(forecast);
        }
        return result;
    }

    public Cursor getAllDataCursor() {
        return mDB.query(DB_TABLE,
                new String[]{
                        COLUMN_ID,
                        COLUMN_CITY,
                        COLUMN_TEMPRATURE,
                        COLUMN_WEATHER,

                        COLUMN_DATE + "0",
                        COLUMN_TEMPRATURE + "0",
                        COLUMN_WEATHER + "0",

                        COLUMN_DATE + "1",
                        COLUMN_TEMPRATURE + "1",
                        COLUMN_WEATHER + "1",

                        COLUMN_DATE + "2",
                        COLUMN_TEMPRATURE + "2",
                        COLUMN_WEATHER + "2",
                }, null, null, null, null, null);
    }

    ;

    public long insertData(Forecast forecast) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CITY, forecast.getCity());
        contentValues.put(COLUMN_TEMPRATURE, forecast.getTemp());
        contentValues.put(COLUMN_WEATHER, forecast.getWeather());

        ArrayList<DayForecast> arrayList = forecast.getCities();
        if (arrayList.size() != 3) {
            for (int i = 0; i < 3; i++)
                arrayList.add(new DayForecast("", "", ""));
        }
        for (int i = 0; i < arrayList.size(); i++) {
            DayForecast forecastWeather = arrayList.get(i);
            contentValues.put(COLUMN_DATE + String.valueOf(i), forecastWeather.getDate());
            contentValues.put(COLUMN_TEMPRATURE + String.valueOf(i), forecastWeather.getTemp());
            contentValues.put(COLUMN_WEATHER + String.valueOf(i), forecastWeather.getWeather());
        }

        return mDB.insert(DB_TABLE, null, contentValues);
    }

    public Forecast selectForecast(String name) {
        ArrayList<Forecast> forecasts = getAllData();
        for (int i = 0; i < forecasts.size(); i++)
            if (forecasts.get(i).getCity().equals(name))
                return forecasts.get(i);
        return null;
    }

    public void deleteCity(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + "=" + id, null);
    }

    public void updateCity(String city, Forecast forecast) {
        Cursor cursor = getAllDataCursor();
        long id = 0;
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_CITY)).equals(city))
                id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        updateCity(id, forecast);
    }

    private void updateCity(long id, Forecast forecast) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CITY, forecast.getCity());
        contentValues.put(COLUMN_WEATHER, forecast.getWeather());
        contentValues.put(COLUMN_TEMPRATURE, forecast.getTemp());

        ArrayList<DayForecast> arrayList = forecast.getCities();
        for (int i = 0; i < arrayList.size(); i++) {
            DayForecast forecastWeather = arrayList.get(i);
            contentValues.put(COLUMN_DATE + String.valueOf(i), forecastWeather.getDate());
            contentValues.put(COLUMN_TEMPRATURE + String.valueOf(i), forecastWeather.getTemp());
            contentValues.put(COLUMN_WEATHER + String.valueOf(i), forecastWeather.getWeather());
        }

        mDB.update(DB_TABLE, contentValues, COLUMN_ID + "=" + id, null);
    }
}