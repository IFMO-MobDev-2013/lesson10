package com.example.MeWeather;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ViruZ on 08.01.14.
 */
public class WeatherAdapter extends BaseAdapter {
    DBWeather table;
    private Context context;
    private List<DBCityInform> currentState;
    long pos;

    public WeatherAdapter(Context context, DBWeather table) {
        this.context = context;
        this.table = table;
        this.currentState = table.getAll();
    }

    public void refresh() {
        currentState = table.getAll();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentState.size();
    }

    @Override
    public Object getItem(int position) {
        return currentState.get(position);
    }

    @Override
    public long getItemId(int position) {
        return currentState.get(position).getId();
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.city, parent, false);
        DBCityInform dbCityInform = currentState.get(position);
        TextView location = (TextView) view.findViewById(R.id.cityIdCity);


        if (dbCityInform.getForecast() != null) {
            Forecast forecast = dbCityInform.getForecast();
            pos = dbCityInform.getId();


            location.setText(dbCityInform.getCityName());

        }

        if (dbCityInform.getSelected() == 0) {
            location.setTextColor(Color.GRAY);
        }

        if (dbCityInform.getSelected() == 1) {
            location.setTextColor(Color.MAGENTA);
        }

        return view;
    }
}
