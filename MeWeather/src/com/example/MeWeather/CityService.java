package com.example.MeWeather;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Created by ViruZ on 09.01.14.
 */
public class CityService extends IntentService {
    public static final String TAG = CityService.class.toString();
    public static final String UPDATE_DONE = TAG.concat(":CITY_ADDED");
    public static final String LOCATION_UPDATE_DONE = TAG.concat(":LOCATION_UPDATED");
    public static final String LOCATION_UPDATE_FAIL = TAG.concat(":LOCATION_UPDATE_FAILED");

    public CityService() {
        super("Citylist updater");
    }

    private final URLPars weatherApi = new URLPars();
    private DBHelper dbHelper;
    private DBWeather dbWeather;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this);
        dbWeather = new DBWeather(dbHelper.getWritableDatabase());
    }


    public static void requestUpdate(Context context, String cityName) {
        context.startService(
                new Intent(context, CityService.class).putExtra("cityName", cityName).putExtra("locate", "no")
        );
    }

    public static void locationUpdate(Context context, String cityName) {
        context.startService(
                new Intent(context, CityService.class).putExtra("cityName", cityName).putExtra("locate", "ok")
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityName = intent.getStringExtra("cityName");
        String locate = intent.getStringExtra("locate");
        if (locate.equals("ok")) {
            updateLocate(cityName);
        } else {
            if (!cityName.isEmpty())
                update(cityName);
        }
    }

    private void update(String cityName) {
        boolean check = false;
        try {
            Cities cities = new Cities(GeoCoder.getLocate(cityName));
            String coordinate = cities.getCity().getName();
            Forecast forecast = weatherApi.getForecast(coordinate, 5);
            String name = cities.getCity().getName();
            DBCityInform dbCityInform = new DBCityInform(0, name, forecast,
                    System.currentTimeMillis(), 0);
            dbWeather.insert(dbCityInform);
            if (dbCityInform != null) {
                check = true;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        if (check)
            sendBroadcast(new Intent(UPDATE_DONE));
        else
            sendBroadcast(new Intent(LOCATION_UPDATE_FAIL));
    }

    private void updateLocate(String cityName) {
        try {
            Cities cities = new Cities(GeoCoder.getLocate(cityName));
            String coordinate = cities.getCity().getName();
            Forecast forecast = weatherApi.getForecast(coordinate, 5);
            String name = cities.getCity().getName();
            DBCityInform dbCityInform = new DBCityInform(0, name, forecast,
                    System.currentTimeMillis(), 0);
            dbWeather.updateLocation(dbCityInform);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
