package ru.ifmo.mobdev.weather.location;

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
 * Created by Nick Smelik on 21.11.13.
 */
public class LocationProvider {
    private static final String URL = "http://api.worldweatheronline.com/free/v1/search.ashx";
    private static final String LOCATION_PARAM = "q";
    private static final String POPULAR_PARAM = "popular";
    private static final String FORMAT_PARAM = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String KEY_PARAM = "key";
    private static final String KEY_VALUE = "fdrru7s7fvmjdvp3yzfskz4s";

    private final HttpClient client = new DefaultHttpClient();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public Cities getLocate(String location) throws IOException {

        HttpGet request = new HttpGet(URL + '?' + URLEncodedUtils.format(Arrays.asList(
                new BasicNameValuePair(LOCATION_PARAM, location),
                new BasicNameValuePair(FORMAT_PARAM, FORMAT_VALUE),
                new BasicNameValuePair(POPULAR_PARAM, "yes"),
                new BasicNameValuePair(KEY_PARAM, KEY_VALUE)
        ), "utf-8"));


        HttpResponse response = client.execute(request);
        try {
            Cities ct = new Cities(
                    gson.fromJson(new InputStreamReader(response.getEntity().getContent()), CityResponse.class)
            );
            ct.getCount();
            return ct;
        } catch (RuntimeException e) {
            Log.e(LocationProvider.class.toString(), "something with connection");
        }

        return null;
    }
}
