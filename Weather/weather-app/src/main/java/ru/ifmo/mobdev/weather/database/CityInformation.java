package ru.ifmo.mobdev.weather.database;

import ru.ifmo.mobdev.weather.weather.Forecast;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class CityInformation {
    private long id;
    private String cityName;
    private String country;
    private Forecast forecast;
    private long lastUpdate;
    private int is_selected;
    private double latitude = 0.0;
    private double longitude = 0.0;

    public CityInformation(long id, String cityName, Forecast forecast, long lastUpdate, int is_selected) {
        this.id = id;
        this.cityName = cityName;
        this.forecast = forecast;
        this.lastUpdate = lastUpdate;
        this.is_selected = is_selected;
    }

    public CityInformation(long id, String cityName, String country, Forecast forecast, long lastUpdate, int is_selected, double latitude, double longitude) {
        this.id = id;
        this.cityName = cityName;
        this.country = country;
        this.forecast = forecast;
        this.lastUpdate = lastUpdate;
        this.is_selected = is_selected;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
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

    public double getLatitude()
    {
        return latitude;
    }

    public  double getLongitude() {
        return longitude;
    }
}
