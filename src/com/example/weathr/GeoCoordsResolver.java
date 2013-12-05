package com.example.weathr;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sergey on 11/29/13.
 */

public class GeoCoordsResolver extends AsyncTask<String, Void, City> {

    //if this != paragon, we do not need this

    private static final String COMPLETION_API_KEY = "AIzaSyBKDroEXGuXd8nK7m32RQ5ppB3ReQv6bQ0";

    @Override
    protected City doInBackground(String... params) {
        if (params.length > 0) {
            HttpParams p = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(p, 1000);
            HttpConnectionParams.setSoTimeout(p, 5000);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(String.format(
                    "http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false"
                    , URLEncoder.encode(params[0])));
            try {
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String s = "";
                do {
                    sb.append(s);
                    s = reader.readLine();
                } while (s != null);
                JSONObject json = new JSONObject(sb.toString());
                if (json.has("status") && "OK".equals(json.getString("status"))) {
                    ArrayList<String> result = new ArrayList<String>();
                    if (json.has("results")) {
                        JSONObject r = json.getJSONArray("results").getJSONObject(0);
                        JSONArray addrs = r.getJSONArray("address_components");
                        for (int i = 0; i < addrs.length(); i++) {
                            JSONArray types = addrs.getJSONObject(i).getJSONArray("types");
                            for (int j = 0; j < types.length(); j++)
                                if ("locality".equals(types.getString(j))) {
                                    City c = new City();
                                    c.name = addrs.getJSONObject(i).getString("long_name");
                                    JSONObject coords = json.getJSONArray("results")
                                                            .getJSONObject(0)
                                                            .getJSONObject("geometry")
                                                            .getJSONObject("location");
                                    c.latitude = coords.getDouble("lat");
                                    c.longitude = coords.getDouble("lng");
                                    c.lastUpdate = new Date(System.currentTimeMillis());
                                    return c;
                                }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //TODO handle errors
            } catch (JSONException e) {
                e.printStackTrace();
                //TODO handle errors
            }
            return null;
        }
        return null;
    }
}
