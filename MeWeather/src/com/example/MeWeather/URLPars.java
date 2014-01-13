package com.example.MeWeather;

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
 * Created by ViruZ on 08.01.14.
 */
public class URLPars {private static final String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx";
    private static final String LOCATION_PARAM = "q";
    private static final String NUMBER_OF_DAYS = "num_of_days";
    private static final String FORMAT_PARAM = "format";
    private static final String FORMAT_VALUE = "json";
    private static final String KEY_PARAM = "key";
    private static final String KEY_VALUE = "vzteyw87pmfy69jwu6qg958n";

    private final HttpClient client = new DefaultHttpClient();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();


    public Forecast getForecast(String location, int days) throws IOException {

        HttpGet request = new HttpGet(URL + '?' + URLEncodedUtils.format(Arrays.asList(
                new BasicNameValuePair(LOCATION_PARAM, location),
                new BasicNameValuePair(NUMBER_OF_DAYS, days + ""),
                new BasicNameValuePair(FORMAT_PARAM, FORMAT_VALUE),
                new BasicNameValuePair(KEY_PARAM, KEY_VALUE)
        ), "utf-8"));

        HttpResponse response = client.execute(request);
        try {
            return new Forecast(
                    gson.fromJson(new InputStreamReader(response.getEntity().getContent()), WeatherPars.class)
            );
        } catch (RuntimeException e) {
            Log.e(URLPars.class.toString(), "something with connection");
        }

        return null;
    }
}
