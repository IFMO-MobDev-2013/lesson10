package ru.zulyaev.ifmo.zeather.weather;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author seidhe
 */
public class WeatherApi {
    private static final String WEATHER_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx";
    private static final String SEARCH_URL = "http://api.worldweatheronline.com/free/v1/search.ashx";

    private static final String LOCATION_PARAM = "q";

    private static final String NUM_OF_DAYS_PARAM = "num_of_days";
    private static final String FORMAT_PARAM = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String KEY_PARAM = "key";
    private static final String KEY_VALUE = "2z5ncguba636pj2x2azhy2qc";

    private final HttpClient client = new DefaultHttpClient();
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    public Forecast getForecast(double latitude, double longitude, int days) throws IOException {
        HttpGet request = new HttpGet(WEATHER_URL + "?" + URLEncodedUtils.format(Arrays.asList(
                new BasicNameValuePair(LOCATION_PARAM, latitude + "," + longitude),
                new BasicNameValuePair(NUM_OF_DAYS_PARAM, days + ""),
                new BasicNameValuePair(FORMAT_PARAM, FORMAT_VALUE),
                new BasicNameValuePair(KEY_PARAM, KEY_VALUE)
        ), "utf-8"));

        HttpResponse response = client.execute(request);

        try {
            return new Forecast(
                    gson.fromJson(new InputStreamReader(response.getEntity().getContent()), WeatherResponse.class)
            );
        } catch (RuntimeException e) {
            Log.w(WeatherApi.class.toString(), "Couldn't retrieve forecast", e);
        }

        return null;
    }

    public SearchResult search(String location) throws IOException {
        HttpGet request = new HttpGet(SEARCH_URL + "?" + URLEncodedUtils.format(Arrays.asList(
                new BasicNameValuePair(LOCATION_PARAM, location),
                new BasicNameValuePair(FORMAT_PARAM, FORMAT_VALUE),
                new BasicNameValuePair(KEY_PARAM, KEY_VALUE)
        ), "utf-8"));

        HttpResponse response = client.execute(request);

        try {
            return new SearchResult(
                    gson.fromJson(new InputStreamReader(response.getEntity().getContent()), SearchResponse.class)
            );
        } catch (RuntimeException e) {
            Log.w(WeatherApi.class.toString(), "Couldn't retrieve search results", e);
        }

        return null;
    }
}
