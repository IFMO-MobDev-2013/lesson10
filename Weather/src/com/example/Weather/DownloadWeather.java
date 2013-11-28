package com.example.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Alexei
 * Date: 28.11.13
 * Time: 3:36
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWeather {

    private static final String API_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?";
    private static final String API_KEY = "c52mv9u4u49cvxc8se4nqk2r";
    Context context;
    City city;

    DownloadWeather(City city, Context context) {
        this.city = city;
        this.context = context;
    }

    String download() throws IOException {
        String requestUrl = API_URL + "key=" + API_KEY + "&q=" + URLEncoder.encode(city.name, "UTF-8") + "&num_of_days=3&format=json";
        URL url = new URL(requestUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        int rc = httpConnection.getResponseCode();
        if (rc == HttpURLConnection.HTTP_OK) {
            String line = null;
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                strBuilder.append(line + '\n');
            }
            return strBuilder.toString();
        } else {
            return null;
        }
    }

    void parseFromJSON(String source) throws JSONException {
        JSONObject object = (JSONObject) new JSONTokener(source).nextValue();
        object = object.getJSONObject("data");
        JSONArray jsonArray = object.getJSONArray("weather");
        WeatherDataBaseHelper weatherDataBaseHelper = new WeatherDataBaseHelper(context, city.name);
        SQLiteDatabase sqLiteDatabase = weatherDataBaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(weatherDataBaseHelper.dropDatabase());
        sqLiteDatabase.execSQL(weatherDataBaseHelper.createDatabase());

        for (int i = 0; i < jsonArray.length(); i++) {
            object = jsonArray.getJSONObject(i);
            String date = object.getString("date");
            int lowTemp = object.getInt("tempMinC");
            int maxTemp = object.getInt("tempMaxC");
            int windSpeed = object.getInt("windspeedKmph");
            String windDirect = object.getString("winddirection");
            int weatherCode = object.getInt("weatherCode");
            String weatherDesc = object.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherDataBaseHelper.DATE, date);
            contentValues.put(WeatherDataBaseHelper.LOW_TEMPERATURE, lowTemp);
            contentValues.put(WeatherDataBaseHelper.HIGH_TEMPERATURE, maxTemp);
            contentValues.put(WeatherDataBaseHelper.LOW_TEMPERATURE, lowTemp);
            contentValues.put(WeatherDataBaseHelper.WIND_SPEED, windSpeed);
            contentValues.put(WeatherDataBaseHelper.WIND_DIRECTION, windDirect);
            contentValues.put(WeatherDataBaseHelper.WEATHER_CODE, weatherCode);
            contentValues.put(WeatherDataBaseHelper.WEATHER_DESCRIPTION, weatherDesc);

            sqLiteDatabase.insert(weatherDataBaseHelper.dataBaseName(), null, contentValues);

        }
        sqLiteDatabase.close();
        weatherDataBaseHelper.close();
    }

    public boolean execute() {
        String result = null;
        try {
            result = download();
            parseFromJSON(result);
        } catch (Exception e) {
            result = null;
        }
        return (result != null);
    }
}
