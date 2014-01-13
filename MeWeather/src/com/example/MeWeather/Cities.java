package com.example.MeWeather;

import java.util.ArrayList;

/**
 * Created by ViruZ on 09.01.14.
 */
public class Cities {
    private final ArrayList<City> cities;

    public Cities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public City getCity() {
        return cities.get(0);
    }
}
