package com.weather.android.dronov;

import com.weather.android.dronov.Forecast.Forecast;
import com.weather.android.dronov.Forecast.ForecastWeather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 21.11.13
 * Time: 3:31
 * To change this template use File | Settings | File Templates.
 */

public class WeatherDownload {

    private Forecast result = new Forecast();
    private static final String KEY = "tjdqdcj7hbk9s8g6urxtpv4b";
    private static final String BASE_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx";

    public void updateWeather(String requestCity, String requestCountry)  {
        String newRequestCity = requestCity.replace(" ", "%20");
        String newRequestCountry = requestCountry.replace(" ", "%20");
        String link = BASE_URL + "?key=" + KEY + "&q=" + newRequestCity + "," + newRequestCountry  + "&num_of_days=3&format=json";
        JSONObject json = null;
        String currentBufferString;
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            StringBuilder builder = new StringBuilder();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((currentBufferString = buffer.readLine()) != null) {
                builder.append(currentBufferString);
            }
            json = new JSONObject(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            JSONObject root = json.getJSONObject("data");
            JSONArray jsonArray = root.getJSONArray("current_condition");
            JSONObject conditions = jsonArray.getJSONObject(0);
            String currentTemprature = conditions.getString("temp_C");
            JSONArray weatherArray = conditions.getJSONArray("weatherDesc");
            JSONObject currentWeather = weatherArray.getJSONObject(0);
            String weather = currentWeather.getString("value");

            JSONArray futureWeather = root.getJSONArray("weather");
            for (int i = 0; i < futureWeather.length(); i++) {
                JSONObject current = futureWeather.getJSONObject(i);
                ForecastWeather nextDay = new ForecastWeather();
                String nextDayDate = current.getString("date");
//                String [] p = nextDayDate.split("-");
//                nextDay.setDate(p[1] + "/" + p[2]);
                Date date = new SimpleDateFormat("yyyy-M-d").parse(nextDayDate);
                String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
                nextDay.setDate(dayOfWeek);
                nextDay.setTemp(current.getString("tempMinC") + "/" + current.getString("tempMaxC"));
                JSONArray curArray = current.getJSONArray("weatherDesc");
                nextDay.setWeather(curArray.getJSONObject(0).getString("value"));
                result.addForecastWeather(nextDay);
            }

            result.setCity(requestCity);
            result.setCountry(requestCountry);
            result.setWeather(weather);
            result.setTemperature(currentTemprature);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Forecast getResult() {
        return result;
    }
}
