package com.weather.android.dronov.Forecast;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 28.11.13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class ForecastWeather {
    private String temp;
    private String date;
    private String weather;

    public ForecastWeather(String temp, String date, String weather) {
        this.temp = temp;
        this.date = date;
        this.weather = weather;
    }

    public ForecastWeather() {}

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }



}
