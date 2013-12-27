package ru.ifmo.ctddev.isaev.WeatherForecast;

import android.content.Context;
import android.location.Address;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.List;

/**
 * User: Xottab
 * Date: 06.12.13
 */
public class AddressAdapter extends ArrayAdapter<Address> {
    public AddressAdapter(Context context, int resource, List<Address> objects) {
        super(context, resource, objects);
    }

   @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Address address = getItem(position);
        TextView view = new TextView(getContext());
        view.setText(address.getLocality()+", "+address.getCountryName());
        view.setTextSize(25);
        return view;
    }
}
