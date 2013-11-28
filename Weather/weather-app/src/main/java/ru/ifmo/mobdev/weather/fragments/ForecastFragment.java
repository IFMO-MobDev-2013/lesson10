package ru.ifmo.mobdev.weather.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.ifmo.mobdev.weather.R;
import ru.ifmo.mobdev.weather.adapters.ForecastAdapter;
import ru.ifmo.mobdev.weather.database.CityInformation;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;

/**
 * Created by Nick Smelik on 26.11.13.
 */
public class ForecastFragment extends Fragment {
    private dbOpenHelper helper;
    DataBaseTable table;
    Context ctx;
    ForecastAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.three_day_forecast_activity, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ctx = getActivity();

        helper = new dbOpenHelper(getActivity());
        table = new DataBaseTable(helper.getWritableDatabase());

        final ListView listView = (ListView) getActivity().findViewById(R.id.forecastListView);
        setList(listView, ctx);
        CityInformation cityInformation = table.getSelected();
        if (cityInformation.getForecast() != null) {
            adapter = new ForecastAdapter(getActivity(), table, table.getSelected().getId());
            listView.setAdapter(adapter);
        }
    }

    public static void setList(ListView list, Context context) {
        list.setSelector(android.R.color.transparent);
        ColorDrawable sage = new ColorDrawable(context.getResources().getColor(
                android.R.color.transparent));
        list.setDivider(sage);
        list.setDividerHeight(0);
    }
}
