package com.example.lesson10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lesson10.databases.WeatherDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: Genyaz
 * Date: 21.11.13
 * Time: 19:25
 * To change this template use File | Settings | File Templates.
 */

public class CityWeatherActivity extends Activity {

    public TextView cityName;
    public TextView cityTemperature;
    public String name;
    public int id;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
            }
            return mIcon11;
        }

        private static final int minWidth = 100;

        protected void onPostExecute(Bitmap result) {
            if (result.getWidth() < minWidth) {
                result = Bitmap.createScaledBitmap(result, minWidth, (result.getHeight() * minWidth) / result.getWidth(), false);
            }
            bmImage.setImageBitmap(result);
        }
    }

    public void parseWeatherInfo(String s) {
        try {
            JSONObject json = new JSONObject(s);
            JSONArray jsonArray = json.getJSONArray("list");
            int d_day = 0;
            double lon = json.getJSONObject("city").getJSONObject("coord").getDouble("lon");
            double d_time = (360 + lon) * 24 / 360;
            int d_hours = 3 * ((int)(d_time / 3));
            JSONObject result = jsonArray.getJSONObject(0);
            JSONObject mmainInfo = result.getJSONObject("main");
            int curtemp = (int) mmainInfo.getDouble("temp");
            int curtemp_min = (int) mmainInfo.getDouble("temp_min");
            int curtemp_max = (int) mmainInfo.getDouble("temp_max");
            String curtemp_info = curtemp + "°C:\n" + curtemp_min + "..." + curtemp_max;
            ((TextView) findViewById(R.id.currentTemperature)).setText(curtemp_info);
            int curpressure = (int) (mmainInfo.getDouble("pressure") * 0.75);
            ((TextView) findViewById(R.id.currentPressure)).setText(curpressure + "");
            int curhumidity = (int) mmainInfo.getDouble("humidity");
            ((TextView) findViewById(R.id.currentHumidity)).setText(curhumidity + "");
            double curwind_speed = result.getJSONObject("wind").getDouble("speed");
            ((TextView) findViewById(R.id.currentWind)).setText(curwind_speed + "");
            String curicon_path = result.getJSONArray("weather").getJSONObject(0).getString("icon");
            new DownloadImageTask((ImageView) findViewById(R.id.curWeatherPicture)).execute("http://openweathermap.org/img/w/" + curicon_path);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONObject mainInfo = object.getJSONObject("main");
                int temp = (int) mainInfo.getDouble("temp");
                int temp_min = (int) mainInfo.getDouble("temp_min");
                int temp_max = (int) mainInfo.getDouble("temp_max");
                String temp_info = temp + "°C:\n" + temp_min + "..." + temp_max;
                String iconPath = object.getJSONArray("weather").getJSONObject(0).getString("icon");
                String raw_dt_txt = object.getString("dt_txt");
                String dt_txt = raw_dt_txt.split("\\s")[1];
                dt_txt = dt_txt.split("\\:")[0];
                int hours = (Integer.parseInt(dt_txt) + d_hours) % 24;
                if (hours == 0) {
                    d_day++;
                }
                switch (d_day) {
                    case 1:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.tomMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomMorI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                ((TextView) findViewById(R.id.tomDayT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomDayI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 21:
                                ((TextView) findViewById(R.id.tomEveT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomEveI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                        }
                        break;
                    case 2:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.ttMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.ttMorI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttDayI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttEveI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttEveT)).setText(temp_info);
                                break;
                        }
                        break;
                    case 3:
                        switch (hours) {
                            case 9:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttMorI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttMorT)).setText(temp_info);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttDayI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttEveI)).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttEveT)).setText(temp_info);
                                break;
                        }
                        break;
                }

            }
        } catch (Exception e) {
            weatherParsingError();
        }
    }

    public void weatherParsingError() {
        ((TextView) findViewById(R.id.currentTime)).setText("No data has been found.");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_weather);
        Intent intent = getIntent();
        name = intent.getStringExtra(getString(R.string.cityname));
        id = intent.getIntExtra(getString(R.string.cityid), -1);
        cityName = (TextView) findViewById(R.id.city_name);
        cityName.setText(name);
        WeatherDatabase weatherDatabase = new WeatherDatabase(this);
        SQLiteDatabase rdb = weatherDatabase.getReadableDatabase();
        Cursor cursor = rdb.query(WeatherDatabase.DATABASE_NAME, null, WeatherDatabase.CITY_ID + " = " + id,
                null, null, null, null);
        if (cursor.moveToNext()) {
            parseWeatherInfo(cursor.getString(cursor.getColumnIndex(WeatherDatabase.CITY_WEATHER)));
        } else {
            weatherParsingError();
        }
        cursor.close();
        rdb.close();
        weatherDatabase.close();
    }
}