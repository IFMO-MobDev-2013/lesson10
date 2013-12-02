package com.example.weather;

import android.database.Cursor;

public class WeatherAll {
    String date;
    String part_type;
    int temperature_from;
    int temperature_to;
    String image;
    String weather_type;
    double wind;
    int humidity;
    int pressure;

    public WeatherAll() {
        date = "";
        part_type = "";
        image = "";
        weather_type = "";
        temperature_from = 0;
        temperature_to = 0;
        wind = 0;
        humidity = 0;
        pressure = 0;
    }

    public WeatherAll(String _date, String _part_day, int _temperature_from, int _temperature_to, String _image, String _weather_type, double _wind, int _humidity, int _pressure) {
        date = _date;
        part_type = _part_day;
        temperature_from = _temperature_from;
        temperature_to = _temperature_to;
        image = _image;
        weather_type = _weather_type;
        wind = _wind;
        humidity = _humidity;
        pressure = _pressure;
    }

    public WeatherAll(Cursor cursor) {
        int day_column = cursor.getColumnIndex(DBHelper.DAY);
        int temperature_from_column = cursor.getColumnIndex(DBHelper.TEMPERATURE_FROM);
        int image_column = cursor.getColumnIndex(DBHelper.IMAGE);
        int part_column = cursor.getColumnIndex(DBHelper.PART);
        int temperature_to_column = cursor.getColumnIndex(DBHelper.TEMPERATURE_TO);
        int weather_type_column = cursor.getColumnIndex(DBHelper.WEATHER_TYPE);
        int wind_column = cursor.getColumnIndex(DBHelper.WIND);
        int humidity_column = cursor.getColumnIndex(DBHelper.HUMIDITY);
        int pressure_column = cursor.getColumnIndex(DBHelper.PRESSURE);
        try {
            date = cursor.getString(day_column);
            part_type = cursor.getString(part_column);
            temperature_from = Integer.parseInt(cursor.getString(temperature_from_column));
            temperature_to = Integer.parseInt(cursor.getString(temperature_to_column));
            image = cursor.getString(image_column);
            weather_type = cursor.getString(weather_type_column);
            wind = Double.parseDouble(cursor.getString(wind_column));
            humidity = Integer.parseInt(cursor.getString(humidity));
            pressure = Integer.parseInt(cursor.getString(pressure_column));
        } catch (Exception e) {

        }
    }
}
