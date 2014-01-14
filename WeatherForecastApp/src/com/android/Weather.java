package com.android;

import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ruslan
 * Date: 20.11.13
 * Time: 18:09
 */
public class Weather extends AsyncTask<String, Void, ArrayList<Map<String, Object>>> {
    private static final String requestLink = "http://query.yahooapis.com/v1/public/yql?q=select*from%20geo.places%20where%20text=";

    @Override
    public ArrayList<Map<String, Object>> doInBackground(String... city) {
        HttpClient httpClient = new DefaultHttpClient();
        String cityenc = Uri.encode(city[0]);
        String link = requestLink + "%22" + cityenc + "%22" + "&format=xml";
        HttpGet httpGet = new HttpGet(link);
        HttpResponse response;
        InputStream inputStream;
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            String[] keys = {"name", "country", "admin1", "admin2", "woeid"};
            data = CityListParser.Parse(inputStream, "place", keys);
        } catch (Exception e) {
        }
        return data;
    }

    public ArrayList<Map<String, Object>> onPostExecute(ArrayList<Map<String, Object>>... res) {
        super.onPostExecute(res[0]);
        return res[0];
    }
}
