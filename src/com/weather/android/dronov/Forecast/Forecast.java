package com.weather.android.dronov.Forecast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 21.11.13
 * Time: 1:35
 * To change this template use File | Settings | File Templates.
 */
public class Forecast {
    public static final int DAYS_COUNT = 3;
    private String city, country;
    private String weather;
    private String temperature;
    private ArrayList<ForecastWeather> forecastWeathers = new ArrayList<ForecastWeather>();

    public Forecast(String city, String country, String temperature, String weather) {
        this.city = city;
        this.country = country;
        this.weather = weather;
        this.temperature = temperature;

        for (int i = 0; i < DAYS_COUNT; i++)
            forecastWeathers.add(new ForecastWeather("", "", ""));
    }

    public Forecast() {
        city = "";
        country = "";
        weather = "";
        temperature = "";

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void addForecastWeather(ForecastWeather forecastWeather) {
        forecastWeathers.add(forecastWeather);
    }

    public ArrayList<ForecastWeather> getForecastWeathers() {
        return forecastWeathers;
    }

    public void setForecastWeathers(ArrayList<ForecastWeather> forecastWeathers) {
        this.forecastWeathers = forecastWeathers;
    }
}
