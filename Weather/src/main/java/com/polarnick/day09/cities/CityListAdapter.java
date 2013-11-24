package com.polarnick.day09.cities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.j256.ormlite.misc.TransactionManager;
import com.polarnick.day09.R;
import com.polarnick.day09.dao.DatabaseHelperFactory;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastData;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class CityListAdapter extends ArrayAdapter<City> {
    private final Context context;
    private final List<City> cities;
    private final View.OnClickListener deleteCityListener;

    public CityListAdapter(final Context context, final List<City> cities) {
        super(context, R.layout.city_view, cities);
        this.context = context;
        this.cities = cities;
        this.deleteCityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final City city = (City) v.getTag();
                try {
                    TransactionManager.callInTransaction(DatabaseHelperFactory.getHelper().getConnectionSource(), new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            ForecastForCity forecastForCity = city.getForecast();
                            if (forecastForCity != null) {
                                DatabaseHelperFactory.getHelper().getForecastForCityDAO().refresh(forecastForCity);
                                DatabaseHelperFactory.getHelper().getForecastDataDAO().delete(forecastForCity.getCurrent());
                                for (ForecastData forecastData : forecastForCity.getData()) {
                                    DatabaseHelperFactory.getHelper().getForecastDataDAO().delete(forecastData);
                                }
                                DatabaseHelperFactory.getHelper().getForecastForCityDAO().delete(forecastForCity);
                            }
                            DatabaseHelperFactory.getHelper().getCityDAO().deleteById(city.getId());
                            return null;
                        }
                    });
                } catch (SQLException e) {
                    Log.e(CityListAdapter.class.getName(), "Deleting city with name=" + city.getName() + " and id=" + city.getId() + " failed!");
                    throw new RuntimeException(e);
                }
                cities.remove(city);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.city_view, null);
        }
        final TextView cityNameTextView = (TextView) convertView.findViewById(R.id.cityName);
        cityNameTextView.setText(cities.get(position).getName());

        final ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteCityButton);
        deleteButton.setOnClickListener(deleteCityListener);
        deleteButton.setTag(cities.get(position));
        deleteButton.setFocusable(false);
        return convertView;
    }
}
