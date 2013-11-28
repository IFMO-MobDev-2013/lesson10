package ru.ifmo.mobdev.weather.location;

import ru.ifmo.mobdev.weather.weather.Weather;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class Cities {
    private final City[] cities;

    public Cities(City[] cities) {
        this.cities = cities;
    }

    Cities(CityResponse response) {
        int days = response.search_api.result.length;
        this.cities = new City[days];
        for (int i = 0; i < days; ++i) {
            this.cities[i] = new City(response.search_api.result[i]);
        }
    }

    public int getCount() {
        return cities.length;
    }

    public City getCity(int num) {
        return cities[num];
    }
}
