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

    private int imageSize;
    public TextView cityName;
    public TextView cityTemperature;
    public String name;
    public int id;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int size;

        public DownloadImageTask(ImageView bmImage, int size) {
            this.bmImage = bmImage;
            this.size = size;
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

        protected void onPostExecute(Bitmap result) {
            if (result.getWidth() < size) {
                result = Bitmap.createScaledBitmap(result, size, (result.getHeight() * size) / result.getWidth(), false);
            }
            bmImage.setImageBitmap(result);
        }
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            default:
                return "Dec";
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
            if (d_time - d_hours >= 1.5) {
                d_hours += 3;
            }
            JSONObject result = jsonArray.getJSONObject(0);
            JSONObject mmainInfo = result.getJSONObject("main");
            int curtemp = (int) mmainInfo.getDouble("temp");
            int curtemp_min = (int) mmainInfo.getDouble("temp_min");
            int curtemp_max = (int) mmainInfo.getDouble("temp_max");
            String curtemp_info = curtemp + "°C:\n" + curtemp_min + "..." + curtemp_max;
            ((TextView) findViewById(R.id.currentTemperature)).setText(curtemp_info);
            int curpressure = (int) (mmainInfo.getDouble("pressure") * 0.75);
            ((TextView) findViewById(R.id.currentPressure)).setText("Pressure: " + curpressure + " Hg-mm");
            int curhumidity = (int) mmainInfo.getDouble("humidity");
            ((TextView) findViewById(R.id.currentHumidity)).setText("Humidity: " + curhumidity + "%");
            double curwind_speed = result.getJSONObject("wind").getDouble("speed");
            ((TextView) findViewById(R.id.currentWind)).setText("Wind speed: " + curwind_speed + " m/s");
            String curicon_path = result.getJSONArray("weather").getJSONObject(0).getString("icon");
            new DownloadImageTask((ImageView) findViewById(R.id.curWeatherPicture), 200).execute("http://openweathermap.org/img/w/" + curicon_path);
            String rraw_dt_txt = result.getString("dt_txt");
            String ddt_txt = rraw_dt_txt.split("\\s")[1];
            ddt_txt = ddt_txt.split("\\:")[0];
            ((TextView) findViewById(R.id.lastUpdated)).setText("Upd: " + (Integer.parseInt(ddt_txt) + d_hours) % 24 + ":00");
            int mmonth = Integer.parseInt(rraw_dt_txt.split("-")[1]);
            int dday = Integer.parseInt(rraw_dt_txt.split("-")[2].split(" ")[0]);
            ((TextView) findViewById(R.id.currentTime)).setText(
                    ((TextView) findViewById(R.id.currentTime)).getText() + " - " + dday + " " + getMonthName(mmonth));
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONObject mainInfo = object.getJSONObject("main");
                int temp_min = (int) mainInfo.getDouble("temp_min");
                int temp_max = (int) mainInfo.getDouble("temp_max");
                String temp_info = temp_min + "°C\n" + temp_max + "°C";
                String iconPath = object.getJSONArray("weather").getJSONObject(0).getString("icon");
                String raw_dt_txt = object.getString("dt_txt");
                String dt_txt = raw_dt_txt.split("\\s")[1];
                dt_txt = dt_txt.split("\\:")[0];
                int month = Integer.parseInt(raw_dt_txt.split("-")[1]);
                int day = Integer.parseInt(raw_dt_txt.split("-")[2].split(" ")[0]);
                int hours = (Integer.parseInt(dt_txt) + d_hours) % 24;
                if (hours == 0) {
                    d_day++;
                }
                switch (d_day) {
                    case 1:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.tDate)).setText(
                                        ((TextView) findViewById(R.id.tDate)).getText() + " - " + day + " " + getMonthName(month));
                                ((TextView) findViewById(R.id.tomMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomMorI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                ((TextView) findViewById(R.id.tomDayT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomDayI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 21:
                                ((TextView) findViewById(R.id.tomEveT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomEveI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                        }
                        break;
                    case 2:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.ttDate)).setText(day + " " + getMonthName(month));
                                ((TextView) findViewById(R.id.ttMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.ttMorI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttDayI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttEveI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttEveT)).setText(temp_info);
                                break;
                        }
                        break;
                    case 3:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.tttDate)).setText(day + " " + getMonthName(month));
                                new DownloadImageTask((ImageView) findViewById(R.id.tttMorI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttMorT)).setText(temp_info);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttDayI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttEveI), 100).execute("http://openweathermap.org/img/w/" + iconPath);
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