package com.android;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Руслан
 * Date: 21.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class WeatherRenew extends AsyncTask<City, Void, ArrayList<City>> {
    private static final String requestLink = "http://weather.yahooapis.com/forecastrss?w=";

    @Override
    public ArrayList<City> doInBackground(City... city) {
        HttpClient httpClient = new DefaultHttpClient();
        ArrayList<City> cities = new ArrayList<City>();
        HttpResponse response;
        InputStream inputStream;
        for (City c : city) {
            String link = requestLink + c.id + "&u=c";
            HttpGet httpGet = new HttpGet(link);
            try {
                response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                cities.add(WeatherParser.Parse(inputStream, c));
            } catch (Exception e) {
            }

        }
        return cities;
    }

    @Override
    public void onPostExecute(ArrayList<City> res) {
        super.onPostExecute(res);
    }
}
