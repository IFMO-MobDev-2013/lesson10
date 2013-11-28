package com.example.MaWeather;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 29.11.13
 * Time: 2:33
 */
public class Refresher extends IntentService {
    public Refresher() {
        super("Refresher");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherDataBase weatherDataBase = new WeatherDataBase(this);
        int count = weatherDataBase.getTableCount();

        for (int i = 0; i < count; i++) {
            Intent intentRSS = new Intent(this, WeatherGetter.class);
            String cityName = weatherDataBase.getName(i);
            startService(intentRSS.putExtra("cityId",  weatherDataBase.getCityId(cityName)).putExtra("task", "refresh").putExtra("cityName", cityName));
        }
    }
}
