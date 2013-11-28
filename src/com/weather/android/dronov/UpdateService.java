package com.weather.android.dronov;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import com.weather.android.dronov.Forecast.Forecast;
import com.weather.android.dronov.database.WeatherDB;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 28.11.13
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class UpdateService extends IntentService {

    public static final String key = "com.weather.android.dronov.UpdateService";
    public static final int updateTime = 10000;

    private WeatherDownload weatherDownload;
    public UpdateService() {
        super("UpdateWeather");
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        weatherDownload = new WeatherDownload();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherDB weatherDB = new WeatherDB(this);
        weatherDB.open();
        ArrayList<Forecast> arrayList = weatherDB.getAllData();
        for (int i = 0; i < arrayList.size(); i++) {
            Forecast currentForecast = arrayList.get(i);
            weatherDownload.updateWeather(currentForecast.getCity(), currentForecast.getCountry());
        }
        weatherDB.close();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + updateTime, updateTime, pi);
    }
}
