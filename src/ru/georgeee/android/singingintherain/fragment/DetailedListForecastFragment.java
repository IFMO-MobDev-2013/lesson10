package ru.georgeee.android.singingintherain.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.*;
import ru.georgeee.android.singingintherain.misc.DetailExpandableListAdapter;
import ru.georgeee.android.singingintherain.R;
import ru.georgeee.android.singingintherain.model.Forecast;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
abstract public class DetailedListForecastFragment extends ForecastFragment {
    ExpandableListView expandableListView;

    DetailedListAdapter listAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.detailed_list_fragment;
    }

    protected abstract List<Forecast.DataPoint> getDataPoints(Forecast forecast);

    @Override
    protected void onUpdateViewImpl() {
        if (rootView != null) {
            Forecast forecast = city==null?null:city.getForecast();
            List<Forecast.DataPoint> dataPoints;
            listAdapter.clear();
            if (forecast != null && (dataPoints = getDataPoints(forecast)) != null) {
                listAdapter.addAll(dataPoints);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        if (rootView != null) {
            expandableListView = (ExpandableListView) rootView.findViewById(R.id.hourList);
            listAdapter = createListAdapter(inflater);
            expandableListView.setAdapter(listAdapter);
            onUpdateViewImpl();
        }
    }

    protected abstract DetailedListAdapter createListAdapter(LayoutInflater inflater);

    protected abstract class DetailedListAdapter extends DetailExpandableListAdapter<Forecast.DataPoint> {
        LayoutInflater inflater;


        public DetailedListAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

    }
}
