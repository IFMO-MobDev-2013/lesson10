package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForecastWeatherView extends RelativeLayout {

    private ImageView image;
    private TextView time;
    private TextView temp;
    private TextView description;


    public ForecastWeatherView(Context context) {
        super(context);
        initialization(context);
    }

    private void initialization(Context context) {
        ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.forecast_weather_view, this);
        time = (TextView) findViewById(R.id.text_time);
        image = (ImageView) findViewById(R.id.image_current_weather);
        temp = (TextView) findViewById(R.id.text_temp);
        description = (TextView) findViewById(R.id.text_description);
    }

    public void update(ForecastWeather weather) {
        time.setText(new SimpleDateFormat("dd.MM").format(new Date(weather.date)));
        if (weather.tempMaxC > 0) {
            temp.setText("+" + Integer.toString(weather.tempMaxC) + "°C");
        } else {
            temp.setText(Integer.toString(weather.tempMaxC) + "°C");
        }
        description.setText(weather.weatherDesc);
        image.setImageBitmap(weather.icon);
    }
}