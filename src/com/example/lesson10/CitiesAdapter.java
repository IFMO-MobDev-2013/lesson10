package com.example.lesson10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Vector;

public class CitiesAdapter extends ArrayAdapter<City> implements IEventDispatcher {
    private final Context context;
    public Vector<City> cities;
    public EventDispatcher event_pull;

    public CitiesAdapter(Context context, Vector<City> subjects) {
        super(context, R.layout.city, subjects);
        this.context = context;
        this.cities = subjects;
        event_pull = new EventDispatcher();
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = inflater.inflate(R.layout.city, parent, false);

        TextView cityNameView = (TextView) entryView.findViewById(R.id.cityName);
        TextView temperatureLabelView = (TextView) entryView.findViewById(R.id.temperatureLabel);

        final City city = cities.get(index);

        cityNameView.setText(city.name);
        if (city.temperature != 1000)
        {
            temperatureLabelView.setText(city.temperature + "°");
        }

        final IEventDispatcher fix = this;
        entryView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Event e = new TouchEvent(fix, TouchEvent.CLICK);
                e.data.put("ID_CITY", city.getId());
                dispatchEvent(e);
            }
        });

        entryView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Event e = new TouchEvent(fix, TouchEvent.LONG_CLICK);
                e.data.put("ID_CITY", city.getId());
                dispatchEvent(e);
                return true;
            }
        });

        return entryView;
    }

    @Override
    public void addEventListener(IEventHadler listener) {
        event_pull.addEventListener(listener);
    }

    @Override
    public void removeEventListener(IEventHadler listener) {
        event_pull.removeEventListener(listener);
    }

    @Override
    public void dispatchEvent(Event e) {
        event_pull.dispatchEvent(e);
    }
}
