package ru.zulyaev.ifmo.zeather;

import ru.zulyaev.ifmo.zeather.weather.Forecast;

/**
 * @author seidhe
 */
public class WeatherForecast {
    private final long id;
    private final String location;
    private final double latitude;
    private final double longitude;
    private final Forecast forecast;
    private final long lastUpdated;

    public WeatherForecast(long id, String location, double latitude, double longitude, Forecast forecast, long lastUpdated) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.forecast = forecast;
        this.lastUpdated = lastUpdated;
    }


    long getId() {
        return id;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public String getLocation() {
        return location;
    }

    long getLastUpdated() {
        return lastUpdated;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
