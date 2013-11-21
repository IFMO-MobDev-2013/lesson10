package com.example.weathr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import be.hcpl.android.forecast.model.ForecastResponse;
import be.hcpl.android.forecast.model.HourlyWeatherData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements Weather.UpdateListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Weather w = new Weather(this, new Location(59.96,30.30));
        w.setUpdateListener(this);
    }

    @Override
    public void onViewsUpdate(Weather weather) {
        LinearLayout lyt_weather_now = (LinearLayout)findViewById(R.id.lyt_weather_now);
        lyt_weather_now.removeAllViewsInLayout();
        lyt_weather_now.addView(weather.getWeatherNowView());

        ListView lv_weather_soon = (ListView)findViewById(R.id.lv_weather_soon);
        lv_weather_soon.setAdapter(new StupidViewAdapter(this, weather.getWeatherSoonViews()));
    }

    class StupidViewAdapter extends ArrayAdapter<View> {
        List<View> views;

        public StupidViewAdapter(Context context, List<View> objects) {
            super(context, 0, 0, objects);
            views = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }


    }
}

class Weather {
    private Location mCurrentLocation;
    private Context  mContext;
    private UpdateListener mUpdateListener;

    Weather(Context context) {this(context, null);}

    Weather(Context context, Location location) {
        mContext = context;
        if (location != null) {
            setLocation(location);
            getWeather(location);
        }
    }

    private void getWeather(Location location) {
        new WeatherGetter() {
            @Override
            protected void onProgressUpdate(ForecastResponse... values) {
                super.onProgressUpdate(values);
                if (values != null && values.length > 0 && values[0] != null)
                    updateViews(values[0]);
            }
        }.execute(location);
    }

    public void setLocation(Location location) {mCurrentLocation = location;}

    public void setUpdateListener(UpdateListener listener) {
        mUpdateListener = listener;
    }

    private void updateViews(ForecastResponse data) {
        HourlyWeatherData current = data.getCurrently();
        //Weather now
        weatherNowView = View.inflate(mContext, R.layout.weather_now, null);

        //TODO icon

        TextView tv_temperature = (TextView)weatherNowView.findViewById(R.id.tv_temperature);
        tv_temperature.setText(Long.toString(Math.round(current.getTemperature()))+mContext.getString(R.string.txt_degrees));

        TextView tv_summary = (TextView)weatherNowView.findViewById(R.id.tv_summary);
        tv_summary.setText(current.getSummary() + "\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), current.getHumidity() * 100) + "%\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), current.getPressure()*0.75006));


        weatherSoonViews.clear();

        //Weather soon
        tv_summary = (TextView)View.inflate(mContext, R.layout.weather_soon_summary, null);
        tv_summary.setText(data.getHourly().getSummary());

        weatherSoonViews.add(tv_summary);

        DateFormat format = new SimpleDateFormat("HH:mm");

        for (int i = 0; i<data.getHourly().getData().length && i<=6; i+=2) //show 4 of them only
        {
            HourlyWeatherData h = data.getHourly().getData()[i];
            View weatherSoonView = View.inflate(mContext, R.layout.weather_soon_hourly, null);

            TextView tv_date_time = (TextView)weatherSoonView.findViewById(R.id.tv_date_time);
            Date d = new Date(h.getTime()*1000);
            tv_date_time.setText(format.format(d));

            tv_temperature = (TextView)weatherSoonView.findViewById(R.id.tv_temperature);
            tv_temperature.setText(Long.toString(Math.round(h.getTemperature()))+mContext.getString(R.string.txt_degrees));

            tv_summary = (TextView)weatherSoonView.findViewById(R.id.tv_summary);
            tv_summary.setText(current.getSummary() + "\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), h.getHumidity() * 100) + "%\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), h.getPressure()*0.75006));

            weatherSoonViews.add(weatherSoonView);
        }


        if (mUpdateListener != null)
            mUpdateListener.onViewsUpdate(this);
    }

    View weatherNowView;

    List<View> weatherSoonViews = new ArrayList<View>();

    public View getWeatherNowView() {
        return weatherNowView;
    }

    public List<View> getWeatherSoonViews() {
        return weatherSoonViews;
    }

    interface UpdateListener {
        void onViewsUpdate(Weather weather);
    }
}