package ru.ifmo.Weather;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 19.11.13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public class WeatherUpdater extends IntentService {
    final static String LOADING_ERROR = "Error loading (weather)";
    final static private String TAG = "Reloader service (weather)";
    static ArrayList<WeatherCond> weather;

    public WeatherUpdater() {
        super("WeatherUpdater");
    }

    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TownDatabase townDb = new TownDatabase(getApplicationContext());
        townDb.open();
        WeatherDatabase weatherDb = new WeatherDatabase(getApplicationContext());
        weatherDb.open();

        Intent intentResponse = new Intent();
        intentResponse.setAction(MainActivity.ACTION_response);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        ArrayList<Location> towns = townDb.getAllTowns();
        int updatedCount = 0;

        for (int j = 0; j < towns.size(); j++) {
            try {
                Location town = towns.get(j);
                weather = WeatherParser.parse(MainActivity.createWeatherRequest(town, 3));
                weatherDb.deleteAllItems(town);
                for (int i = 0; i < weather.size(); i++) {
                    weather.get(i).pic = grabImageFromUrl(weather.get(i).param[WeatherCond.ICON_URL]);
                    weather.get(i).param[WeatherCond.TOWN_ID] = town.param[Location.ID];
                    weatherDb.addItem(weather.get(i));
                }
                updatedCount++;
            } catch (Exception ex) {
                Log.w(TAG, "Parser failed");
                intentResponse.putExtra(LOADING_ERROR, true);
            }

        }

        intentResponse.putExtra("type", MainActivity.WEATHER_COND).putExtra("updatedCount", updatedCount);
        sendBroadcast(intentResponse);

        townDb.close();
        weatherDb.close();
    }

    private Bitmap grabImageFromUrl(String url) throws Exception {
        boolean error = false;
        InputStream in = null;
        Bitmap bt = null;
        try {
            in = (InputStream) new URL(url).getContent();
            bt = BitmapFactory.decodeStream(in);
        } catch (Exception ex) {
            error = true;
        } finally {
            try {
                in.close();
            } catch (Throwable ex) {
            }
        }

        if (error) {
            throw new Exception("Icon loading failed");
        }
        return bt;
    }
}

