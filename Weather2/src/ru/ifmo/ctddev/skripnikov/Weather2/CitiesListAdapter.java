package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CitiesListAdapter extends ArrayAdapter<City> implements SpinnerAdapter {


    public CitiesListAdapter(Context context, City[] cities) {
        super(context, android.R.layout.simple_list_item_2, cities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;
        if (convertView == null) {
            item = ((LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        } else {
            item = convertView;
        }
        ((TextView) item.findViewById(android.R.id.text1))
                .setText(getItem(position).name + ", " + getItem(position).country);
        ((TextView) item.findViewById(android.R.id.text2)).setText(getItem(position).region +
                getItem(position).getDistance());
        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }
}
