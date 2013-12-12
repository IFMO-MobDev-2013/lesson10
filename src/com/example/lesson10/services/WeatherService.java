package com.example.lesson10.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.lesson10.CityInfo;
import com.example.lesson10.databases.CitiesDatabase;
import com.example.lesson10.databases.WeatherDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WeatherService extends IntentService {

    public WeatherService(String name) {
        super(name);
    }

    public WeatherService() {
        super("default_name_");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CitiesDatabase citiesdb = new CitiesDatabase(this);
        SQLiteDatabase rdb = citiesdb.getReadableDatabase();
        Cursor cursor = rdb.query(CitiesDatabase.DATABASE_NAME,
                null, null, null, null, null, null);
        int city_name_column = cursor.getColumnIndex(CitiesDatabase.CITY_NAME);
        int id_column = cursor.getColumnIndex(CitiesDatabase._ID);
        String city_name;
        int id;
        List<CityInfo> cityInfos = new ArrayList<CityInfo>();
        while (cursor.moveToNext()) {
            city_name = cursor.getString(city_name_column);
            id = cursor.getInt(id_column);
            cityInfos.add(new CityInfo(city_name, id));
        }
        cursor.close();
        rdb.close();
        citiesdb.close();
        WeatherDatabase weatherdb = new WeatherDatabase(this);
        SQLiteDatabase wdb = weatherdb.getWritableDatabase();
        wdb.execSQL(WeatherDatabase.DROP_DATABASE);
        wdb.execSQL(WeatherDatabase.CREATE_DATABASE);
        CityInfo cityInfo;
        for (int i = 0; i < cityInfos.size(); i++) {
            cityInfo = cityInfos.get(i);
            URLConnection urlConnection = null;
            try {
                String name = URLEncoder.encode(cityInfo.name);
                String weatherQuery = "http://api.openweathermap.org/data/2.5/forecast?q=" + name + "&mode=json&units=metric";
                URL url = new URL(weatherQuery);
                urlConnection = url.openConnection();
                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                ContentValues cv = new ContentValues();
                cv.put(WeatherDatabase.CITY_ID, cityInfo.id);
                cv.put(WeatherDatabase.CITY_WEATHER, builder.toString());
                wdb.insert(WeatherDatabase.DATABASE_NAME, null, cv);
            } catch (Exception e) {
            } finally {
                try {
                    urlConnection.getInputStream().close();
                } catch (IOException e) {
                }
            }
        }
        wdb.close();
        weatherdb.close();
    }
}
