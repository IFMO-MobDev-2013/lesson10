package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CurrentWeatherView extends RelativeLayout {
    private ImageView image;
    private TextView time;
    private TextView description;
    private TextView temp;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;

    public CurrentWeatherView(Context context) {
        super(context);
        initialization(context);
    }

    public CurrentWeatherView(Context context, AttributeSet attr) {
        super(context, attr);
        initialization(context);
    }

    private void initialization(Context context) {
        ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.current_weather_view, this);
        time = (TextView) findViewById(R.id.text_time);
        time.setText(getResources().getString(R.string.weather_now));
        description = (TextView) findViewById(R.id.text_description);
        image = (ImageView) findViewById(R.id.image_current_weather);
        temp = (TextView) findViewById(R.id.text_temp);
        humidity = (TextView) findViewById(R.id.text_humidity);
        pressure = (TextView) findViewById(R.id.text_pressure);
        wind = (TextView) findViewById(R.id.text_wind);
    }

    public void update(City city) {
        CurrentWeather weather = city.getCurrentWeather(getContext());
        description.setText(weather.weatherDesc);
        if (weather.temp_C > 0) {
            temp.setText("+" + Integer.toString(weather.temp_C) + "°C");
        } else {
            temp.setText(Integer.toString(weather.temp_C) + "°C");
        }
        humidity.setText(getResources().getString(R.string.humidity) + " " + Float.toString(weather.humidity) + "%");
        pressure.setText(Integer.toString(weather.pressure) + " " + getResources().getString(R.string.hpa));
        wind.setText(Float.toString(weather.windspeedKmph) + " " + getResources().getString(R.string.kmph));
        image.setImageBitmap(weather.icon);
        setBackgroundColor(weather.icon.getPixel(1,1));
    }
}