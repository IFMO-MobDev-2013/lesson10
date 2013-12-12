package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Weather {
    public final CurrentWeather cWeather;
    public final ForecastWeather[] fWeather;

    public Weather(Context context, City city, String json) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        jObject = jObject.getJSONObject("data");
        JSONArray jsonArray = jObject.getJSONArray("weather");
        ForecastWeather[] forecastWeathers = new ForecastWeather[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            forecastWeathers[i] = new ForecastWeather(jsonArray.getJSONObject(i));
            forecastWeathers[i].icon = getIcon(context, null, forecastWeathers[i].weatherCode);
        }
        cWeather = new CurrentWeather(jObject.getJSONArray("current_condition").getJSONObject(0));
        cWeather.icon = getIcon(context, city, 0);
        fWeather = forecastWeathers;
    }

    private static Bitmap getIcon(Context context, City city, int code) {
        FileInputStream in = null;
        try {
            if (city == null)
                in = new FileInputStream(new File(context.getExternalFilesDir(null), "f" + code + ".png"));
            else
                in = new FileInputStream(new File(context.getExternalFilesDir(null), "c" + city.name +
                        city.region + city.country + ".png"));
            return BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
