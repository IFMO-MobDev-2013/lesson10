package com.example.weather;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;


public class DayActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dayfull);


        TextView textViewDate = (TextView) findViewById(R.id.textViewDateFull);
        TextView textViewHumidity = (TextView) findViewById(R.id.textViewHumidity);
        TextView textViewPressure = (TextView) findViewById(R.id.textViewPressure);
        TextView textViewWindDirection = (TextView) findViewById(R.id.textViewWindDirection);
        TextView textViewWindFull = (TextView) findViewById(R.id.textViewWindFull);
        TextView textViewLowFull = (TextView) findViewById(R.id.textViewLowFull);
        TextView textViewHighFull = (TextView) findViewById(R.id.textViewHighFull);
        TextView textViewCloudsFull = (TextView) findViewById(R.id.textViewCloudsFull);
        TextView textViewNameFull = (TextView) findViewById(R.id.textViewNameFull);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewFull);

        TextView textViewHumidityText = (TextView) findViewById(R.id.textViewHumidityText);
        TextView textViewPressureText = (TextView) findViewById(R.id.textViewPressureText);
        TextView textViewWindDirectionText = (TextView) findViewById(R.id.textViewWindDirectionText);
        TextView textViewWindSpeedText = (TextView) findViewById(R.id.textViewWindSpeedText);

        Time time = new Time();
        time.set(Long.parseLong(getIntent().getStringExtra(Day.DATE)));
        textViewDate.setText(time.format3339(true));

        textViewHumidity.setText(getIntent().getStringExtra(Day.HUMIDITY));
        textViewPressure.setText(getIntent().getStringExtra(Day.PRESSURE));
        textViewWindDirection.setText(getIntent().getStringExtra(Day.WIND_DIRECTION));
        if (getIntent().getStringExtra(Day.HUMIDITY) == null || "".equals(getIntent().getStringExtra(Day.HUMIDITY)))
            textViewHumidityText.setVisibility(View.INVISIBLE);

        if (getIntent().getStringExtra(Day.PRESSURE) == null || "".equals(getIntent().getStringExtra(Day.PRESSURE)))
            textViewPressureText.setVisibility(View.INVISIBLE);

        if (getIntent().getStringExtra(Day.WIND_DIRECTION) == null || "".equals(getIntent().getStringExtra(Day.WIND_DIRECTION)))
            textViewWindDirectionText.setVisibility(View.INVISIBLE);

        if (getIntent().getStringExtra(Day.WIND_SPEED) == null || "".equals(getIntent().getStringExtra(Day.WIND_SPEED)))
            textViewWindSpeedText.setVisibility(View.INVISIBLE);

        textViewWindFull.setText(getIntent().getStringExtra(Day.WIND_SPEED));
        textViewLowFull.setText(getIntent().getStringExtra(Day.LOW_TEMPERATURE));
        textViewHighFull.setText(getIntent().getStringExtra(Day.HIGH_TEMPERATURE));
        textViewCloudsFull.setText(getIntent().getStringExtra(Day.CLOUDS));
        textViewNameFull.setText(getIntent().getStringExtra(Town.NAME));

        try {
            Field field = R.drawable.class.getField("w" + getIntent().getIntExtra(Day.IMAGE, 3200));
            imageView.setImageResource(field.getInt(null));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.w3200);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.w3200);
        }
    }
}