package ru.ifmo.ctddev.skripnikov.Weather2;

import android.graphics.Bitmap;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CurrentWeather {
    public final String weatherDesc;
    public final long observation_time;
    public final int weatherCode;
    public final int temp_C;
    public final int windspeedKmph;
    public final int winddirDegree;
    public final int precipMM;
    public final int pressure;
    public final int cloudcover;
    public final float humidity;
    public Bitmap icon;

    public CurrentWeather(JSONObject jsonObject) throws JSONException {
        weatherDesc = jsonObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
        long time;
        try {
            SimpleDateFormat format = new SimpleDateFormat("KK:mm a");
            time = format.parse(jsonObject.getString("observation_time")).getTime();
        } catch (ParseException e) {
            time = System.currentTimeMillis();
        }
        observation_time = time;
        weatherCode = jsonObject.getInt("weatherCode");
        temp_C = jsonObject.getInt("temp_C");
        windspeedKmph = jsonObject.getInt("windspeedKmph");
        winddirDegree = jsonObject.getInt("winddirDegree");
        precipMM = jsonObject.getInt("precipMM");
        pressure = jsonObject.getInt("pressure");
        cloudcover = jsonObject.getInt("cloudcover");
        humidity = (float) jsonObject.getDouble("humidity");
    }
}
