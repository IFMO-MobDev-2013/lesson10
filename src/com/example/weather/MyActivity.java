package com.example.weather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;

public class MyActivity extends Activity implements AppResultsReceiver.Receiver {

    private final String URL_TAG = "url";
    private final String RECEIVER = "receiver";

    private final int HOUR = 3600 * 1000;

    AppResultsReceiver mReceiver;
    Calendar calendar;
    AlarmManager alarm;
    private final String PREFS_NAME = "MyPrefsFile";


    private static String URL;
    private int curCityId;

    TextView nameOfCity;
    ImageView iw;
    TextView temp;
    TextView sum;
    ImageView f_today_image;
    TextView today_text;
    ImageView f_tomorrow_image;
    TextView tomorrow_text;
    ImageView f_after_image;
    TextView after_text;

    DataBase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new DataBase(getApplicationContext());
        calendar = Calendar.getInstance();
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        URL = getIntent().getStringExtra("url");
        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);



        nameOfCity = (TextView) findViewById(R.id.city);
        iw = (ImageView) findViewById(R.id.cur_image);
        temp = (TextView) findViewById(R.id.temperature);
        sum = (TextView) findViewById(R.id.sum);
        f_today_image = (ImageView) findViewById(R.id.today);
        today_text = (TextView) findViewById(R.id.f_today);
        f_tomorrow_image = (ImageView) findViewById(R.id.tomorrow);
        tomorrow_text = (TextView) findViewById(R.id.f_tomorrow);
        f_after_image = (ImageView) findViewById(R.id.after);
        after_text = (TextView) findViewById(R.id.f_after);




        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("firstTime", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            startCitiesActivity(null);
        } else {
            curCityId = settings.getInt("city_id", 1);
            City c = db.getCity(curCityId);
            nameOfCity.setText(c.getCity());

            changeWeather();

            startUpdating();
        }
    }

    private void startUpdating() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(RECEIVER, mReceiver);
        intent.putExtra(URL_TAG, URL);
        boolean updating = true;
        intent.putExtra("status", updating);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), HOUR, pintent);
    }

    private void changeWeather() {
        List<Weather> weatherList = db.getWeather(curCityId);
        Weather current = weatherList.get(0);
        iw.setImageBitmap(BitmapFactory.decodeByteArray(current.getImage(), 0, current.getImage().length));
        temp.setText(" " + current.getTempC() + " °C");
        sum.setText(current.getWeatherDesc() + "\n"
                + "Wind: " + current.getWindSpeed() + " km/h " + current.getWindDir() + "\n"
                + "Cloud cover: " + current.getCloudCover() + "%\n"
                + "Pressure: " + current.getPressure() + " mb \n"
                + "Humidity: " + current.getHumidity() + " %\n");

        Weather today = weatherList.get(1);
        f_today_image.setImageBitmap(BitmapFactory.decodeByteArray(today.getImage(), 0, today.getImage().length));
        today_text.setText(today.getWeatherDesc() + "\n"
                + today.getTempMinC() + " - " + today.getTempMaxC() + " °C");
        Weather tomorrow = weatherList.get(2);
        f_tomorrow_image.setImageBitmap(BitmapFactory.decodeByteArray(tomorrow.getImage(), 0, tomorrow.getImage().length));
        tomorrow_text.setText(tomorrow.getWeatherDesc() + "\n"
                + tomorrow.getTempMinC() + " - " + tomorrow.getTempMaxC() + " °C");
        Weather after = weatherList.get(3);
        f_after_image.setImageBitmap(BitmapFactory.decodeByteArray(after.getImage(), 0, after.getImage().length));
        after_text.setText(after.getWeatherDesc() + "\n"
                + after.getTempMinC() + " - " + after.getTempMaxC() + " °C");
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        int city_id = resultData.getInt("city_id");
        if (city_id == curCityId) {
            changeWeather();
        }
    }

    public void startCitiesActivity(View v) {
        Intent intent = new Intent(this, CitiesActivity.class);
        startActivity(intent);
        finish();
    }

}
