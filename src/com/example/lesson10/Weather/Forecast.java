package com.example.lesson10.Weather;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: javlon
 * Date: 13.01.14
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
public class Forecast implements Serializable {
    private String city;
    private String weather;
    private String temp;
    private ArrayList<DayForecast> cities = new ArrayList<DayForecast>();

    public Forecast(String city, String weather, String temp) {
        this.city = city;
        this.weather = weather;
        this.temp = temp;
    }

    public Forecast() {
        city = "";
        weather = "";
        temp = "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public ArrayList<DayForecast> getCities() {
        return cities;
    }

    public void setCities(ArrayList<DayForecast> cities) {
        this.cities = cities;
    }

    public void addForecast(DayForecast forecastWeather) {
        cities.add(forecastWeather);
    }
}
