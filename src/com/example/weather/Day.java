package com.example.weather;

public class Day {
    String date;
    String humidity;
    String pressure;
    String wind_direction;
    String wind_speed;
    String low_temperature;
    String high_temperature;
    String clouds;
    int image;

    public static final String DATE = "date";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String WIND_DIRECTION = "wind_direction";
    public static final String WIND_SPEED = "wind_speed";
    public static final String LOW_TEMPERATURE = "low_temperature";
    public static final String HIGH_TEMPERATURE = "high_temperature";
    public static final String CLOUDS = "clouds";
    public static final String IMAGE = "image";

    Day() {
        date = "";
        humidity = "";
        pressure = "";
        wind_direction = "";
        wind_speed = "";
        low_temperature = "";
        high_temperature = "";
        clouds = "";
        image = 3200;
    }

    Day(String _date, String _humidity, String _pressure, String _wind_direction, String _wind_speed,
        String _low_temperature, String _high_temperature, String _clouds, int _image) {
        date = _date;
        humidity = _humidity;
        pressure = _pressure;
        wind_direction = _wind_direction;
        wind_speed = _wind_speed;
        low_temperature = _low_temperature;
        high_temperature = _high_temperature;
        clouds = _clouds;
        image = _image;
    }

    public void println() {
        System.out.println();
        System.out.println("date = " + date +
                " humidity = " + humidity +
                " pressure = " + pressure +
                " wind_direction = " + wind_direction +
                " wind_speed = " + wind_speed);
        System.out.println("low_temperature = " + low_temperature +
                " high_temperature = " + high_temperature +
                " clouds = " + clouds +
                " image = " + image);
    }
}