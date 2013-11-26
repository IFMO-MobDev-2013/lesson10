package ru.zulyaev.ifmo.zeather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.zulyaev.ifmo.zeather.weather.City;

import java.util.Collections;
import java.util.List;

/**
 * @author seidhe
 */
public class CitiesAdapter extends BaseAdapter {
    private final Context context;
    private List<City> cities = Collections.emptyList();

    public CitiesAdapter(Context context) {
        this.context = context;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public City getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.city_row, parent, false);
            holder = new ViewHolder(
                    (TextView) view.findViewById(R.id.title),
                    (TextView) view.findViewById(R.id.country),
                    (TextView) view.findViewById(R.id.coords)
            );
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        City city = cities.get(position);
        holder.title.setText(city.getTitle());
        holder.country.setText(city.getCountry());
        holder.coords.setText(context.getString(R.string.coords, city.getLatitude(), city.getLongitude()));
        return view;
    }

    static class ViewHolder {
        final TextView title;
        final TextView country;
        final TextView coords;

        ViewHolder(TextView title, TextView country, TextView coords) {
            this.title = title;
            this.country = country;
            this.coords = coords;
        }
    }
}
