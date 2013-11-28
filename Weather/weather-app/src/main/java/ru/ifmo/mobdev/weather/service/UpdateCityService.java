package ru.ifmo.mobdev.weather.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import ru.ifmo.mobdev.weather.database.CityInformation;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;
import ru.ifmo.mobdev.weather.location.Cities;
import ru.ifmo.mobdev.weather.location.LocationProvider;
import ru.ifmo.mobdev.weather.weather.Forecast;
import ru.ifmo.mobdev.weather.weather.WeatherProvider;

/**
 * Created by Nick Smelik on 28.11.13.
 */
public class UpdateCityService extends IntentService {

    public static final String TAG = UpdateCityService.class.toString();
    public static final String UPDATE_DONE = TAG.concat(":CITY_ADDED");

    public UpdateCityService() {
        super("Citylist updater");
    }

    private final LocationProvider locationApi = new LocationProvider();
    private final WeatherProvider weatherApi = new WeatherProvider();
    private dbOpenHelper dbHelper;
    private DataBaseTable table;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new dbOpenHelper(this);
        table = new DataBaseTable(dbHelper.getWritableDatabase());
    }


    public static void requestUpdate(Context context, String cityName) {
        context.startService(
                new Intent(context, UpdateCityService.class).putExtra("cityName", cityName)
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityName = intent.getStringExtra("cityName");
        Log.d("myLog", "UpdateCityService start");
        if (cityName.isEmpty()) {
            Log.d("myLog", "empty string");
        } else {
            update(cityName);
        }

    }

    private void update(String cityName) {
        try {
            Cities cities = locationApi.getLocate(cityName);
            Forecast forecast = weatherApi.getForecast(cities.getCity(0).getName(), 5);
            CityInformation cityInformation = new CityInformation(0, cities.getCity(0).getName(), cities.getCity(0).getCountry(),
                    forecast, System.currentTimeMillis(), 0, cities.getCity(0).getLatitude(), cities.getCity(0).getLongitude());
            table.insert(cityInformation);
        } catch (IOException | NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Cannot add this city", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        sendBroadcast(new Intent(UPDATE_DONE));
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
