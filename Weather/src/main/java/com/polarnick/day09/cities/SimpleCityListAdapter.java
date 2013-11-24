package com.polarnick.day09.cities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.polarnick.day09.R;
import com.polarnick.day09.Utils;
import com.polarnick.day09.entities.City;

import java.util.List;

/**
 * Date: 19.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class SimpleCityListAdapter extends ArrayAdapter<City> {
    private final int maxCityNameLength;
    private final List<City> cities;
    private final Context context;

    public SimpleCityListAdapter(Context context, List<City> cities, int maxNameLength) {
        super(context, R.layout.city_to_choose_view, cities);
        this.context = context;
        this.cities = cities;
        this.maxCityNameLength = maxNameLength;
        setDropDownViewResource(R.layout.city_to_choose_view);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, 0);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, 10);
    }

    private View getView(int position, View convertView, int padding) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.city_to_choose_view, null);
        }
        final TextView cityNameTextView = (TextView) convertView.findViewById(R.id.cityName);
        cityNameTextView.setPadding(padding, padding, padding, padding);
        cityNameTextView.setText(Utils.truncateString(cities.get(position).getName(), maxCityNameLength));
        convertView.setTag(cities.get(position));
        return convertView;
    }
}
