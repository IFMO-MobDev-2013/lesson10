package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ForecastWeatherListAdapter extends ArrayAdapter<ForecastWeather>{

    public ForecastWeatherListAdapter(Context context, ArrayList<ForecastWeather> weathers) {
        super(context, R.layout.forecast_weather_view, weathers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;
        if (convertView == null) {
            item = new ForecastWeatherView(getContext());
        } else {
            item = convertView;
        }
        ((ForecastWeatherView)item).update(getItem(position));
        return item;
    }
}
