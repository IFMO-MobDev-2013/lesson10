package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WorldWeatherOnlineAPI {
    private static final String BASE = "http://api.worldweatheronline.com/free/v1/";
    private static final String APP_ID = "&format=json&key=s2pufaxvwh4fwzfx6rrnpthg";
    public static final City[] NULL_CITIES = {};

    public static City[] getCitiesByCoordinates(float lat, float lon) {
        return getCitiesFromJSONString(getJSONStringByURL(BASE + "search.ashx?q=" +
                Float.toString(lat) + "%2C" +
                Float.toString(lon) + APP_ID));
    }

    public static City[] getCitiesByName(String name) {
        return getCitiesFromJSONString(getJSONStringByURL(BASE + "search.ashx?q=" + name + APP_ID));
    }

    public static boolean updateWeather(Context context, DBStorage dbs, City city) {
        String jString = getJSONStringByURL(BASE + "weather.ashx?q=" + city.latitude + "%2C" + city.longitude + "&num_of_days=5" + APP_ID);
        if (jString != null)
            try {
                JSONObject jObject = new JSONObject(jString);
                jObject = jObject.getJSONObject("data");
                JSONArray jsonArray = jObject.getJSONArray("weather");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.getJSONObject(i)
                            .getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
                    Bitmap icon = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    int code = jsonArray.getJSONObject(i).getInt("weatherCode");
                    saveIcon(context, null, code, icon);
                }
                String url = jObject.getJSONArray("current_condition").getJSONObject(0)
                        .getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
                Bitmap icon = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                saveIcon(context, city, 0, icon);
                dbs.updateWeather(city, jString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return false;
    }

    private static boolean saveIcon(Context context, City city, int code, Bitmap icon) {
        FileOutputStream out = null;
        try {
            if (city == null)
                out = new FileOutputStream(new File(context.getExternalFilesDir(null), "f" + code + ".png"));
            else
                out = new FileOutputStream(new File(context.getExternalFilesDir(null), "c" + city.name +
                        city.region + city.country + ".png"));
            icon.compress(Bitmap.CompressFormat.PNG, 90, out);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    private static City[] getCitiesFromJSONString(String jString) {
        if (jString != null)
            try {
                JSONObject jObject = new JSONObject(jString);
                jObject = jObject.getJSONObject("search_api");
                JSONArray jArray = jObject.getJSONArray("result");
                City[] cities = new City[jArray.length()];
                for (int i = 0; i < jArray.length(); i++) {
                    String name = jArray.getJSONObject(i).getJSONArray("areaName").getJSONObject(0).getString("value");
                    String region = jArray.getJSONObject(i).getJSONArray("region").getJSONObject(0).getString("value");
                    String country = jArray.getJSONObject(i).getJSONArray("country").getJSONObject(0).getString("value");
                    float latitude = (float) jArray.getJSONObject(i).getDouble("latitude");
                    float longitude = (float) jArray.getJSONObject(i).getDouble("longitude");
                    cities[i] = new City(0, name, region, country, latitude, longitude, null);
                }
                return cities;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return NULL_CITIES;
    }

    private static String getJSONStringByURL(String stringUrl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(stringUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            if (is != null) {
                StringBuilder builder = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null)
                    builder.append(line);
                return builder.toString();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}