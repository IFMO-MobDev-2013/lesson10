package com.example.Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {
    Context context;
    ArrayList<Weather> array;

    public WeatherArrayAdapter(Context context, ArrayList<Weather> objects) {
        super(context, R.layout.weather_view, objects);
        this.context = context;
        this.array = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.weather_view, parent, false);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvWeather = (TextView) view.findViewById(R.id.tvWeather);
        TextView tvSummary = (TextView) view.findViewById(R.id.tvSummary);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        imageView.setImageResource(context.getResources().getIdentifier("p" + String.valueOf(array.get(position).weatherCode), "drawable", context.getPackageName()));
        tvDate.setText(array.get(position).date);
        tvSummary.setText(array.get(position).weatherDescription);
        tvWeather.setText(((array.get(position).lowTemp > 0) ? "+" : "") + array.get(position).lowTemp + ".." + ((array.get(position).maxTemp > 0) ? "+" : "") + array.get(position).maxTemp + "\nWind Speed, " + array.get(position).windSpeed + " kmph\nWind Direction " + array.get(position).windDirect);
        return view;
    }
}
