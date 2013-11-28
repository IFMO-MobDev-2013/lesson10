package com.weather.android.dronov.City;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.11.13
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class City {
    private String city = null;
    private String country = null;

    public City() {}

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
