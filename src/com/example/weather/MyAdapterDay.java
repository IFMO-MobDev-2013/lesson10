package com.example.weather;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Юрий
 * Date: 01.12.13
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
public class MyAdapterDay extends ArrayAdapter<WeatherAll> {
    Context context;

    public MyAdapterDay(Context _context, ArrayList<WeatherAll> value) {
        super(_context, R.layout.list_item, value);
        context = _context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherAll weatherAll = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setTextColor(Color.GREEN);
        textView.setTextSize(15);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        int count = 0;
        while (true) {
            if (weatherAll.weather_type.charAt(count) == '\n') break;
            count++;
        }
        textView.setText(weatherAll.date + " " + weatherAll.weather_type.substring(0, count) + "  " + ((weatherAll.temperature_from > 0) ? "+" : "") + weatherAll.temperature_from);
        if (weatherAll.image.charAt(0) == 'n') {
            weatherAll.image = weatherAll.image.substring(1);
        }
        for (int i = weatherAll.image.length() - 1; i >= 0; i--) {
            if (weatherAll.image.charAt(i) != ' ' && weatherAll.image.charAt(i) != '\n') {
                weatherAll.image = weatherAll.image.substring(0, i + 1);
            }
        }
        imageView.setImageResource(context.getResources().getIdentifier("p" + weatherAll.image, "drawable", context.getPackageName()));
        TextView textView1 = (TextView) view.findViewById(R.id.textView1);
        textView1.setTextColor(Color.RED);
        textView1.setTextSize(15);
        textView1.setText(" Давление:" + weatherAll.pressure + "мм рт. ст. Ветер:" + weatherAll.wind + "м/с");
        return view;
    }
}
