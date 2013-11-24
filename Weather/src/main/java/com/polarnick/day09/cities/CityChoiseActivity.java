package com.polarnick.day09.cities;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.polarnick.day09.R;
import com.polarnick.day09.entities.City;

import java.util.List;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class CityChoiseActivity extends ListActivity {
    public static final String SELECTED_CITY = "selected city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.city_choise);

        final List<City> cities = (List<City>) getIntent().getExtras().get(CitiesManagementActivity.CITIES_LIST_EXTRA);
        setListAdapter(new SimpleCityListAdapter(this, cities, Integer.MAX_VALUE));
        setListAdapter(new ArrayAdapter<City>(this, R.layout.city_to_choose_view, cities) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(CityChoiseActivity.this).inflate(R.layout.city_to_choose_view, null);
                }
                final TextView cityNameTextView = (TextView) convertView.findViewById(R.id.cityName);
                cityNameTextView.setText(cities.get(position).getName());
                convertView.setTag(cities.get(position));
                return convertView;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_CITY, (City) v.getTag());
        setResult(RESULT_OK, intent);
        finish();
    }
}
