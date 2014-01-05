package com.example.weather;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WeatherParser {

    private Weather cur;
    City city;
    private String pictureURL;
    private List<Weather> forecast;


    public void parse(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            JSONObject current = data.getJSONArray("current_condition").getJSONObject(0);
            cur = new Weather();
            cur.setCloudCover(current.getInt("cloudcover"));
            cur.setHumidity((float) current.getDouble("humidity"));
            cur.setWindDir(current.getString("winddir16Point"));
            cur.setWindSpeed(current.getInt("windspeedKmph"));
            cur.setPressure(current.getInt("pressure"));
            cur.setTempC(current.getInt("temp_C"));
            cur.setTempF(current.getInt("temp_F"));
            cur.setCurrent(true);
            cur.setDate("0");

            String weatherDesc = current.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
            cur.setWeatherDesc(weatherDesc);

            pictureURL = current.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
            cur.setIconURL(pictureURL);

            city = new City();
            JSONObject request = data.getJSONArray("request").getJSONObject(0);
            String query = request.getString("query");
            city.setCity(query);

            JSONArray weatherForecast = data.getJSONArray("weather");
            forecast = new ArrayList<>();
            for (int i = 0; i < weatherForecast.length(); i++) {
                Weather w = new Weather();
                JSONObject f = weatherForecast.getJSONObject(i);
                w.setTempMaxC(f.getInt("tempMaxC"));
                w.setTempMinC(f.getInt("tempMinC"));
                w.setDate(f.getString("date"));
                w.setWeatherDesc(f.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
                w.setIconURL(f.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value"));
                forecast.add(w);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Weather getCurrentWeather() {
        return cur;
    }

    public City getCity() {
        return city;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public List<Weather> getForecast() {
        forecast.add(0, cur);
        return forecast;
    }
}
