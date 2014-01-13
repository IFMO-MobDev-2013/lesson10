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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ViruZ on 08.01.14.
 */
public class fragmenttwo extends Fragment {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(iServis.UPDATE_DONE);
    private static final IntentFilter REFRESH = new IntentFilter(refresh.REFRESH);
    DBWeather dbWeather;
    Context context;
    DBCityInform dbCityInform;

    ImageView weatherImage;
    TextView description;
    TextView temp;
    TextView wind;
    TextView lastUpdate;

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
        return inflater.inflate(R.layout.secondscreen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        DBHelper dbHelper = new DBHelper(getActivity());
        dbWeather = new DBWeather(dbHelper.getWritableDatabase());
        weatherImage = (ImageView) getActivity().findViewById(R.id.WeatherImage);
        description = (TextView) getActivity().findViewById(R.id.Description);
        temp = (TextView) getActivity().findViewById(R.id.CurrentTemp);
        wind = (TextView) getActivity().findViewById(R.id.Wind);
        lastUpdate = (TextView) getActivity().findViewById(R.id.LastUpdate);
        refreshWeather();
    }

    private void refreshWeather() {
        if (dbWeather.getSelected() != null) {
            dbCityInform = dbWeather.getSelected();
            if (dbCityInform.getForecast() != null) {
                PictureClass pic = PictureManager.getWeather(dbCityInform.getForecast().getToday().getCode());
                weatherImage.setImageResource(pic.dayPict);
                description.setText(pic.subj);

                temp.setText(Integer.toString(dbCityInform.getForecast().getToday().getMaxTemp()) + "°C");
                wind.setText(Integer.toString(dbCityInform.getForecast().getToday().getWindSpeed()) + "km/h " + dbCityInform.getForecast().getToday().getWindDir());


                long time = System.currentTimeMillis();

                if (time - dbCityInform.getLastUpdate() <= 300000) {
                    lastUpdate.setText("Last Update: Just now.");
                } else if (time - dbCityInform.getLastUpdate() <= 1800000) {
                    lastUpdate.setText("Last Update: Half hour ago.)");
                } else if (time - dbCityInform.getLastUpdate() <= 3600000) {
                    lastUpdate.setText("Last Update: Hour ago)");
                } else if (time - dbCityInform.getLastUpdate() > 3600000) {
                    lastUpdate.setText("Last Update:" + " " + ((time - dbCityInform.getLastUpdate()) / 3600000) + "hours ago");
                }
            }
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
