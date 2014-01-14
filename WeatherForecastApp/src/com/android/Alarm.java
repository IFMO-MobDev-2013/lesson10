package com.android;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Ruslan
 * Date: 12.01.14
 * Time: 19:36
 */
public class Alarm extends IntentService {
    public Alarm() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SQLRequest sqlRequest = new SQLRequest(this);
        sqlRequest.openDB();
        ArrayList<City> cities = sqlRequest.getAllCities();
        WeatherRenew weatherRenew = new WeatherRenew();
        City[] city = new City[cities.size()];
        for (int i = 0; i < cities.size(); ++i) {
            city[i] = cities.get(i);
        }
        weatherRenew.execute(city);
        try {
            cities = weatherRenew.get();
        } catch (Exception e) {
        }
        for (City c : cities) {
            if (!c.date.isEmpty()) {
                sqlRequest.addCity(c);

            }
        }
        sqlRequest.closeDB();
    }
}
