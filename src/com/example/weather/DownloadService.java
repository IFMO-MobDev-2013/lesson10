package com.example.weather;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;


public class DownloadService extends IntentService {


    private static final String URL = "url";
    private static final String RECEIVER = "receiver";


    public DownloadService() {
        super("downloadService");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        DataBase db = new DataBase(getApplicationContext());

        String url = intent.getStringExtra(URL);
        boolean  updating  = intent.getBooleanExtra("status", false);
        int city_id = intent.getIntExtra("city_id", 0);
        JSONObject json;
        WeatherParser parser = new WeatherParser();

        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            parser.parse(json);
            is.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        try {

            List<Weather> weatherList = parser.getForecast();

            for (int i = 0; i < weatherList.size(); i++) {
                String u = weatherList.get(i).getIconURL();
                Log.d("URL", u);
                weatherList.get(i).setImage(getImage(u));
            }
            if (updating) {
                db.updateWeather(city_id, weatherList);
            } else {
                city_id = (int)db.createCity(parser.getCity());
                db.createWeather(weatherList, city_id);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("city_id", city_id);
            bundle.putBoolean("status", updating);
            receiver.send(0, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private byte[] getImage(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}