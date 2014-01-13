package com.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.WeatherForecastApp.R;



public class ForecastActivity extends Activity {
    TextView cityNameText;
    TextView dateText;
    TextView degreesText;
    TextView weatherText;
    ImageView imageView;
    TextView[] degreesForecast;
    TextView[] dates;
    TextView[] weatherForecast;
    ImageView[] images;

    private static int setPicture(int id) {
        if (id <= 4 || id == 45) {
            return R.drawable.a9613;
        }
        if (id <= 8 || id == 10) {
            return R.drawable.a969;
        }
        if (id == 9 || id == 18) {
            return R.drawable.a967;
        }
        if (id <= 12) {
            return R.drawable.a968;
        }
        if (id == 13 || id == 15 || (id >= 41 && id <= 43)) {
            return R.drawable.a9611;
        }
        if (id == 14 || id == 16 || id == 46) {
            return R.drawable.a9610;
        }
        if (id == 17 || id == 35) {
            return R.drawable.a9612;
        }
        if (id <= 22) {
            return R.drawable.a966;
        }
        if (id <= 26 || id == 30 || id == 44) {
            return R.drawable.a964;
        }
        if (id == 27) {
            return R.drawable.a9619;
        }
        if (id == 28) {
            return R.drawable.a965;
        }
        if (id == 29) {
            return R.drawable.a9618;
        }
        if (id == 31 || id == 33) {
            return R.drawable.a9617;
        }
        if (id == 32 || id == 34) {
            return R.drawable.a962;
        }
        if (id == 36) {
            return R.drawable.a961;
        }
        if (id <= 39 || id == 47) {
            return R.drawable.a9615;
        }
        if (id == 40) {
            return R.drawable.a9614;
        }
        return R.drawable.a964;
    }


    private void renewFields(City city) {
        cityNameText.setText(city.name);
        dateText.setText(city.date);
        degreesText.setText(Double.toString(city.tempNow) + "°C");
        weatherText.setText("   " + city.weatherNow);
        imageView.setImageResource(setPicture(city.weatherNowCode));
        for (int i = 0; i < 3; i++) {
            degreesForecast[i].setText("from " + Double.toString(city.tempHigh[i]) + "°C\n"
                    + "to " + Double.toString(city.tempLow[i]) + "°C");
            dates[i].setText(city.dates[i]);
            weatherForecast[i].setText("   " + city.weather[i]);
            images[i].setImageResource(setPicture(city.weatherCode[i]));
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherforecast);
        cityNameText = (TextView) findViewById(R.id.citytext);
        dateText = (TextView) findViewById(R.id.daytext);
        degreesText = (TextView) findViewById(R.id.degreestext);
        imageView = (ImageView) findViewById(R.id.imageView);
        weatherText = (TextView) findViewById(R.id.weathertext);
        degreesForecast = new TextView[3];
        degreesForecast[0] = (TextView) findViewById(R.id.degreesday1);
        degreesForecast[1] = (TextView) findViewById(R.id.degreesday2);
        degreesForecast[2] = (TextView) findViewById(R.id.degreesday3);
        dates = new TextView[3];
        dates[0] = (TextView) findViewById(R.id.date1);
        dates[1] = (TextView) findViewById(R.id.date2);
        dates[2] = (TextView) findViewById(R.id.date3);
        weatherForecast = new TextView[3];
        weatherForecast[0] = (TextView) findViewById(R.id.weatherday1);
        weatherForecast[1] = (TextView) findViewById(R.id.weatherday2);
        weatherForecast[2] = (TextView) findViewById(R.id.weatherday3);
        images = new ImageView[3];
        images[0] = (ImageView) findViewById(R.id.imageday1);
        images[1] = (ImageView) findViewById(R.id.imageday2);
        images[2] = (ImageView) findViewById(R.id.imageday3);
        Intent intent = getIntent();
        final int cityId = intent.getIntExtra("cityId", 0);
        SQLRequest helper = new SQLRequest(this);
        helper.openDB();
        City city = helper.getCityForecast(cityId);
        renewFields(city);
        helper.closeDB();
    }
}