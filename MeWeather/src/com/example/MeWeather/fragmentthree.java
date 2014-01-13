package com.example.MeWeather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by ViruZ on 12.01.14.
 */
public class fragmentthree extends Fragment {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(iServis.UPDATE_DONE);
    private static final IntentFilter REFRESH = new IntentFilter(refresh.REFRESH);
    private DBHelper helper;
    DBWeather dbWeather;
    Context context;
    ForecastAdapter forecastAdapter;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshWeather();
        }
    };

    private final BroadcastReceiver refresh_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshWeather();
        }
    };


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thirdscreen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        helper = new DBHelper(getActivity());
        dbWeather = new DBWeather(helper.getWritableDatabase());
        final ListView listView = (ListView) getActivity().findViewById(R.id.forec);
        setList(listView, context);
        DBCityInform dbCityInform = dbWeather.getSelected();
        if (dbCityInform.getForecast() != null) {
            forecastAdapter = new ForecastAdapter(getActivity(), dbWeather);
            listView.setAdapter(forecastAdapter);
        }
        refreshWeather();
    }

    public static void setList(ListView list, Context context) {
        list.setSelector(android.R.color.transparent);
        ColorDrawable sage = new ColorDrawable(context.getResources().getColor(
                android.R.color.transparent));
        list.setDivider(sage);
        list.setDividerHeight(0);
    }

    private void refreshWeather() {
        if (dbWeather.getSelected() != null) {
            forecastAdapter.refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, UPDATE_FILTER);
        getActivity().registerReceiver(refresh_receiver, REFRESH);
        refreshWeather();
    }
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(refresh_receiver);
        super.onPause();
    }
}
