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
import ru.ifmo.mobdev.weather.translation.TranslateProvider;
import ru.ifmo.mobdev.weather.weather.Forecast;
import ru.ifmo.mobdev.weather.weather.WeatherProvider;

/**
 * Created by Nick Smelik.
 */
public class UpdateCityService extends IntentService {

    public static final String TAG = UpdateCityService.class.toString();
    public static final String UPDATE_DONE = TAG.concat(":CITY_ADDED");
    public static final String LOCATION_UPDATE_DONE = TAG.concat(":LOCATION_UPDATED");
    public static final String LOCATION_UPDATE_FAIL = TAG.concat(":LOCATION_UPDATE_FAILED");

    public UpdateCityService() {
        super("Citylist updater");
    }

    private final LocationProvider locationApi = new LocationProvider();
    private final WeatherProvider weatherApi = new WeatherProvider();
    private final TranslateProvider translateApi = new TranslateProvider();
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
                new Intent(context, UpdateCityService.class).putExtra("cityName", cityName).putExtra("locate", "no")
        );
    }

    public static void locationUpdate(Context context, String cityName) {
        context.startService(
                new Intent(context, UpdateCityService.class).putExtra("cityName", cityName).putExtra("locate",  "ok")
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityName = intent.getStringExtra("cityName");
        String locate =  intent.getStringExtra("locate");
        if (locate.equals("ok")) {
            updateLocate(cityName);
        } else {
            try {
                cityName = translateApi.getTranslate(cityName, "en");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("myLog", "UpdateCityService start");
            if (cityName.isEmpty()) {
                Log.d("myLog", "empty string");
            } else {
                update(cityName);
            }
        }
    }

    private void update(String cityName) {
        boolean check = false;
        try {
            Cities cities = locationApi.getLocate(cityName);
            Forecast forecast = weatherApi.getForecast(cities.getCity(0).getName(), 5);
            String name = translateApi.getTranslate(cities.getCity(0).getName(), "ru");
            String country = translateApi.getTranslate(cities.getCity(0).getCountry(), "ru");
            CityInformation cityInformation = new CityInformation(0, name, country,
                    forecast, System.currentTimeMillis(), 0, cities.getCity(0).getLatitude(), cities.getCity(0).getLongitude());
            table.insert(cityInformation);
            if (cityInformation != null) {
                check = true;
            }
        } catch (IOException | NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Cannot add this city", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (check)
            sendBroadcast(new Intent(UPDATE_DONE));
        else
            sendBroadcast(new Intent(LOCATION_UPDATE_FAIL));
    }

    private void updateLocate(String cityName) {
        try {
            Cities cities = locationApi.getLocate(cityName);
            Forecast forecast = weatherApi.getForecast(cities.getCity(0).getName(), 5);
            String name = translateApi.getTranslate(cities.getCity(0).getName(), "ru");
            String country = translateApi.getTranslate(cities.getCity(0).getCountry(), "ru");
            CityInformation cityInformation = new CityInformation(0, name, country,
                    forecast, System.currentTimeMillis(), 0, cities.getCity(0).getLatitude(), cities.getCity(0).getLongitude());
            table.updateLocation(cityInformation);
        } catch (IOException | NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Cannot add this city", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
