package com.example.MeWeather;

/**
 * Created by ViruZ on 09.01.14.
 */
public class City {
    private String name;

    public City(String name, String country, float latitude, float longitude) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
