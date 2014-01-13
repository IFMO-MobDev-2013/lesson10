package com.example.MeWeather;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ViruZ on 10.01.14.
 */
public class AddCityAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> data;

    public AddCityAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int index) {
        return data.get(index);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {

        @Override
        public String convertResultToString(Object resultValue) {
            return resultValue.toString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null) {
                List<String> newData = null;
                try {
                    newData = Lists.transform(GeoCoder.getLocate(constraint.toString()), new Function<City, String>() {
                        @Override
                        public String apply(City city) {
                            return city.getName();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (newData == null) {
                    return null;
                } else {
                    filterResults.values = newData;
                    filterResults.count = newData.size();
                }
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            if (results != null && results.count > 0 && results.count < 6) {
                data = (List<String>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
}
