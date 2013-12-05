package ru.ifmo.ctddev.skripnikov.Weather2;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WeatherFetcherService extends IntentService {

    public WeatherFetcherService(String name) {
        super(name);
    }

    public WeatherFetcherService() {
        super("WeatherFetcherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBStorage dbs = new DBStorage(this);
        City[] cities = dbs.getCities();
        for (City city : cities) WorldWeatherOnlineAPI.updateWeather(this, dbs, city);
        dbs.destroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.BROADCAST_ACTION);
        sendBroadcast(broadcastIntent);
    }
}
