package ru.ifmo.Weather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 19.11.13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public class WeatherUpdater extends IntentService {
    final static private String TAG = "Reloader service (weather)";
    final static String LOADING_ERROR = "Error loading (weather)";

    public WeatherUpdater(){
        super("WeatherUpdater");
    }

    public void onCreate(){
        super.onCreate();

    }
    static ArrayList<WeatherCond> weather;

    @Override
    protected void onHandleIntent(Intent intent) {

        String url = intent.getStringExtra("url");

        Intent intentResponse = new Intent();
        intentResponse.setAction(MainActivity.ACTION_response);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            weather = WeatherParser.parse(url);
            for (int i = 0; i < weather.size(); i++){
                intentResponse.putExtra("data"+i, weather.get(i).param);
                intentResponse.putExtra("dataType"+i, weather.get(i).now);
           }
        } catch (Exception ex){
            Log.w(TAG, "Parser failed");
            intentResponse.putExtra(LOADING_ERROR, true);
        }
        intentResponse.putExtra("type", MainActivity.WEATHER_COND);
        sendBroadcast(intentResponse);

        if(intent.getExtras().getBoolean("isLast", false)){
            //Toast.makeText(getApplicationContext(), "Погода обновлена", Toast.LENGTH_LONG).show();
        }

    }
}

