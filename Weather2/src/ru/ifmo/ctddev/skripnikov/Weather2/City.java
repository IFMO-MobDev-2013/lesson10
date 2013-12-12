package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import org.json.JSONException;

public class City {
    public final long id;
    public final String name;
    public final String region;
    public final String country;
    public final float latitude;
    public final float longitude;
    public final String jsonWeather;
    private double distance;
    private Weather weather;

    City(long id,
         String name,
         String region,
         String country,
         float latitude,
         float longitude,
         String jsonWeather) {
        distance = 0;
        this.id = id;
        this.region = region;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.jsonWeather = jsonWeather;
    }

    public void setDistance(float lat, float lon) {
        double R = 6371;
        // перевести координаты в радианы
        double lat1 = lat * Math.PI / 180;
        double lat2 = latitude * Math.PI / 180;
        double long1 = lon * Math.PI / 180;
        double long2 = longitude * Math.PI / 180;
        // косинусы и синусы широт и разницы долгот
        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);
        // вычисления длины большого круга
        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        distance = Math.atan2(y, x) * R;
    }

    public String getDistance() {
        if (distance == 0)
            return "";
        return ", " + Long.toString(Math.round(distance)) + "km";
    }

    public CurrentWeather getCurrentWeather(Context context) {
        if (weather != null)
            return weather.cWeather;
        else if (jsonWeather != null)
            try {
                weather = new Weather(context, this, jsonWeather);
                return weather.cWeather;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public ForecastWeather[] getForecastWeather(Context context) {
        if (weather != null)
            return weather.fWeather;
        else if (jsonWeather != null)
            try {
                weather = new Weather(context, this, jsonWeather);
                return weather.fWeather;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }
}
