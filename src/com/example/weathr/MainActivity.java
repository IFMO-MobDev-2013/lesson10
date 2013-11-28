package com.example.weathr;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.hcpl.android.forecast.model.DailyWeatherData;
import be.hcpl.android.forecast.model.ForecastResponse;
import be.hcpl.android.forecast.model.HourlyWeatherData;
import be.hcpl.android.forecast.model.WeatherData;

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
        Weather w = new Weather(this, new Location(59.96, 30.30));
        w.setUpdateListener(this);
    }

    @Override
    public void onViewsUpdate(Weather weather) {
        LinearLayout lyt_weather_now = (LinearLayout) findViewById(R.id.lyt_weather_now);
        lyt_weather_now.removeAllViewsInLayout();
        lyt_weather_now.addView(weather.getWeatherNowView());

        ViewGroup vg_weather_soon = (ViewGroup) findViewById(R.id.lv_weather_soon);
        vg_weather_soon.removeAllViews();
        List<View> views = weather.getWeatherSoonViews();
        for (View v : views)
            vg_weather_soon.addView(v);

        ViewGroup vg_weather_later = (ViewGroup) findViewById(R.id.lv_weather_later);
        vg_weather_later.removeAllViews();
        views = weather.getWeatherLaterViews();
        for (View v : views)
            vg_weather_later.addView(v);

        findViewById(R.id.sv_scroller).setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout lyt_lists_container = (LinearLayout) findViewById(R.id.lyt_lists_container);
        lyt_lists_container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
    private Location       mCurrentLocation;
    private Context        mContext;
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

        TextView tv_temperature = (TextView) weatherNowView.findViewById(R.id.tv_temperature);
        tv_temperature.setText(Long.toString(Math.round(current.getTemperature())) + mContext.getString(R.string.txt_degrees));

        TextView tv_summary = (TextView) weatherNowView.findViewById(R.id.tv_summary);
        tv_summary.setText(current.getSummary() + "\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), current.getHumidity() * 100) + "%\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_wind), current.getWindSpeed(), Weather.determineWindDirection(current.getWindBearing()) + "\n"));
        tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), current.getPressure() * 0.75006));

        ImageView iv_weather_icon = (ImageView)weatherNowView.findViewById(R.id.iv_weather_icon);
        iv_weather_icon.setImageDrawable(getWeatherIcon(current.getIcon()));


        weatherSoonViews.clear();

        //Weather soon
        tv_summary = (TextView) View.inflate(mContext, R.layout.weather_soon_summary, null);
        tv_summary.setText(data.getHourly().getSummary());

        weatherSoonViews.add(tv_summary);

        DateFormat format = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < data.getHourly().getData().length && i <= 6; i += 2) //show 4 of them only
        {
            HourlyWeatherData h = data.getHourly().getData()[i];
            View weatherSoonView = View.inflate(mContext, R.layout.weather_soon_hourly, null);

            TextView tv_date_time = (TextView) weatherSoonView.findViewById(R.id.tv_date_time);
            Date d = new Date(h.getTime() * 1000);
            tv_date_time.setText(format.format(d));

            tv_temperature = (TextView) weatherSoonView.findViewById(R.id.tv_temperature);
            tv_temperature.setText(Long.toString(Math.round(h.getTemperature())) + mContext.getString(R.string.txt_degrees));

            tv_summary = (TextView) weatherSoonView.findViewById(R.id.tv_summary);
            tv_summary.setText(h.getSummary() + "\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), h.getHumidity() * 100) + "%\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_wind), h.getWindSpeed(), Weather.determineWindDirection(h.getWindBearing()) + "\n"));
            tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), h.getPressure() * 0.75006));

            iv_weather_icon = (ImageView)weatherSoonView.findViewById(R.id.iv_weather_icon);
            iv_weather_icon.setImageDrawable(getWeatherIcon(h.getIcon()));

            weatherSoonViews.add(weatherSoonView);
        }

        format = new SimpleDateFormat("MMM d', 'E");

        //Weather later

        weatherLaterViews.clear();

        tv_summary = (TextView) View.inflate(mContext, R.layout.weather_soon_summary, null);
        tv_summary.setText(data.getDaily().getSummary());

        weatherLaterViews.add(tv_summary);

        for (int i = 1; i < data.getDaily().getData().length && i <= 4; i++) {
            DailyWeatherData w = data.getDaily().getData()[i];
            View weatherLaterView = View.inflate(mContext, R.layout.weather_soon_daily, null);

            TextView tv_date_time = (TextView) weatherLaterView.findViewById(R.id.tv_date_time);
            Date d = new Date(w.getTime() * 1000);
            tv_date_time.setText(format.format(d));

            tv_temperature = (TextView) weatherLaterView.findViewById(R.id.tv_temperature);
            tv_temperature.setText(Long.toString(Math.round(w.getApparentTemperatureMin())) + mContext.getString(R.string.txt_degrees));
            tv_temperature.append(".." + Long.toString(Math.round(w.getApparentTemperatureMax())) + mContext.getString(R.string.txt_degrees));

            tv_summary = (TextView)weatherLaterView.findViewById(R.id.tv_summary_long);
            tv_summary.setText(w.getSummary() + "\n");

            tv_summary = (TextView) weatherLaterView.findViewById(R.id.tv_summary);
            tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), w.getHumidity() * 100) + "%\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_wind), w.getWindSpeed(), Weather.determineWindDirection(w.getWindBearing()) + "\n"));
            tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), w.getPressure() * 0.75006));

            iv_weather_icon = (ImageView)weatherLaterView.findViewById(R.id.iv_weather_icon);
            iv_weather_icon.setImageDrawable(getWeatherIcon(w.getIcon()));

            weatherLaterViews.add(weatherLaterView);
        }


        if (mUpdateListener != null)
            mUpdateListener.onViewsUpdate(this);
    }

    private static double PI = 3.14159265358;

    public static String determineWindDirection(double direction) {
        double north = Math.sin(direction / 180 * PI);
        double east = Math.cos(direction / 180 * PI);
        String result = north > 0.3 ? "N" : north < -0.3 ? "S" : "";
        result += east > 0.3 ? "E" : east < -0.3 ? "W" : "";
        return result;
    }

    public Drawable getWeatherIcon(String tag) {
        return mContext.getResources().getDrawable(mContext.getResources().
                getIdentifier(tag.replace('-', '_'), "drawable", mContext.getPackageName()));
    }

    View weatherNowView;

    List<View> weatherSoonViews = new ArrayList<View>(),
            weatherLaterViews   = new ArrayList<View>();

    public View getWeatherNowView() {
        return weatherNowView;
    }

    public List<View> getWeatherSoonViews() {
        return weatherSoonViews;
    }

    public List<View> getWeatherLaterViews() { return weatherLaterViews; }

    interface UpdateListener {
        void onViewsUpdate(Weather weather);
    }
}