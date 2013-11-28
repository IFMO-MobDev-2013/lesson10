package ru.ifmo.Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lesson9.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 27.11.13
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class TownAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Location> objects;

    TownAdapter(Context context, ArrayList<Location> locations) {
        ctx = context;
        objects = locations;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Location l = getTown(position);

        ((TextView) view.findViewById(R.id.itemTownName)).setText(l.param[Location.TOWN]);
        ((TextView) view.findViewById(R.id.itemCountryName)).setText(l.param[Location.COUNTRY]);

        return view;
    }

    Location getTown(int position) {
        return ((Location) getItem(position));
    }


}
