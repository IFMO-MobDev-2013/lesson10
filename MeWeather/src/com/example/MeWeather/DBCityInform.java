package com.example.MeWeather;

/**
 * Created by ViruZ on 07.01.14.
 */
public class DBCityInform {
    private long id;
    private String cityName;
    private Forecast forecast;
    private long lastUpdate;
    private int is_selected;

    public DBCityInform(long id, String cityName, Forecast forecast, long lastUpdate, int is_selected) {
        this.id = id;
        this.cityName = cityName;
        this.forecast = forecast;
        this.lastUpdate = lastUpdate;
        this.is_selected = is_selected;
    }

    public long getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public int getSelected() {
        return is_selected;
    }


}
