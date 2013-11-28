package com.example.Nice_Weather_App;

import android.app.IntentService;
import android.content.Intent;

public class Updater extends IntentService {
    WeatherDataBase weatherDataBase;

    public Updater() {
        super("some_name");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        weatherDataBase = new WeatherDataBase(this);
        weatherDataBase.open();
        weatherDataBase.deleteWeatherTable();
        for (int i = 0; i < MyActivity.addedCities.size(); i++) {
            String urlAdress = MyActivity.URL_ADRESS + MyActivity.addedCitiesID.get(i).toString() + ".xml";

            Intent intentForWeather = new Intent(this, MainIntentWorkForWeather.class);
            intentForWeather.putExtra(MyActivity.KEY_FOR_WEATHER_INTENT, urlAdress);
            intentForWeather.putExtra(MyActivity.KEY_CITY, MyActivity.addedCities.get(i).get(MyActivity.KEY_CITY));
            intentForWeather.putExtra(MyActivity.KEY_COUNTRY, MyActivity.addedCities.get(i).get(MyActivity.KEY_COUNTRY));
            intentForWeather.putExtra(MyActivity.KEY_YANDEX_ID, MyActivity.addedCitiesID.get(i).toString());

            startService(intentForWeather);
        }
    }
}
