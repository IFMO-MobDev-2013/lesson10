package ru.ifmo.ctddev.isaev.WeatherForecast;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.ifmo.ctddev.isaev.WeatherForecast.orm.City;

import java.util.List;

/**
 * User: Xottab
 * Date: 06.12.13
 */
public class CityAdapter extends ArrayAdapter<City> {
    public CityAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        TextView view = new TextView(getContext());
        view.setText(city.getName() + ", " + city.getCountry());
        view.setTextSize(25);
        return view;
    }
}
