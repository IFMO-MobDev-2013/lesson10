package com.example.weathr;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.hcpl.android.forecast.model.DailyWeatherData;
import be.hcpl.android.forecast.model.ForecastResponse;
import be.hcpl.android.forecast.model.HourlyWeatherData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements WeatherManager.UpdateListener {
    WeatherManager currentWeatherManager;

    WeatherDBAdapter mDb;

    long currentCityId = -2;

    void setCity(long cityId) {
        currentCityId = cityId;
        currentWeatherManager = new WeatherManager(this, mDb.getCityById(cityId), this);
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(WeatherUpdateService.CITY_ID) && intent.getLongExtra(WeatherUpdateService.CITY_ID, -1) == currentCityId) {
                currentWeatherManager.setCurrentCity(mDb.getCityById(intent.getLongExtra(WeatherUpdateService.CITY_ID, -1)));
                invalidateOptionsMenu();
                MainActivity.this.onSuccessfulUpdate();
                hideProgress();
            }
        }
    };

    void showProgress() {
        findViewById(R.id.lyt_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.lyt_progress).bringToFront();
    }

    void hideProgress() {
        findViewById(R.id.lyt_progress).setVisibility(View.GONE);
    }

    private BroadcastReceiver failReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(WeatherUpdateService.CITY_ID) && intent.getLongExtra(WeatherUpdateService.CITY_ID, -1) == currentCityId) {
                hideProgress();
            }
        }
    };

    @Override
    protected void onResume() {

        IntentFilter f = new IntentFilter();

        f.addAction("com.ifmomd.igushkin.weathr.WEATHER_UPDATED");
        f.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(updateReceiver, f);
        f = new IntentFilter();
        f.addAction("com.ifmomd.igushkin.weathr.UPDATE_FAILED");
        f.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(failReceiver, f);

        super.onResume();
    }

    private void onSuccessfulUpdate() {
        Toast.makeText(this, getString(R.string.tst_on_successful_update), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {

        unregisterReceiver(updateReceiver);
        unregisterReceiver(failReceiver);

        super.onPause();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerAlarm();
        setContentView(R.layout.main);
        mDb = new WeatherDBAdapter(this);
        mDb.open();
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long cityId = defaultSharedPreferences.getLong(LAST_CITY, -1);
        if (cityId == -1) {
            Intent i = new Intent(this, CitiesActivity.class);
            startActivityForResult(i, REQUEST_CODE_CITY);
        } else {
            setCity(cityId);
            currentWeatherManager.setUpdateListener(this);
        }
    }

    private void registerAlarm() {AlarmManager m = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, WeatherUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,Intent.FILL_IN_DATA);
        m.cancel(pi);
        m.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+300000,AlarmManager.INTERVAL_HOUR,pi);
    }

    public static final String LAST_CITY = "last city";

    @Override
    public void onViewsUpdate(WeatherManager weather) {
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

        /*findViewById(R.id.sv_scroller).setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/

        LinearLayout lyt_lists_container = (LinearLayout) findViewById(R.id.lyt_lists_container);
        lyt_lists_container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onStartLongOperation() {
        showProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentWeatherManager != null && currentWeatherManager.currentCity != null) {
            final MenuItem item = menu.findItem(R.id.mnu_choose_city);
            if (item != null)
                item.setTitle(currentWeatherManager.currentCity.name);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static final int REQUEST_CODE_CITY = 153;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_choose_city) {
            Intent i = new Intent(this, CitiesActivity.class);
            startActivityForResult(i, REQUEST_CODE_CITY);
        }
        if (item.getItemId() == R.id.mnu_refresh) {
            Intent i = new Intent(this, WeatherUpdateService.class);
            startService(i);
            Toast.makeText(this, getString(R.string.tst_updating_started), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.main);
            currentWeatherManager.setCurrentCity(currentWeatherManager.currentCity);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            final long longExtra = data.getLongExtra(CitiesActivity.EXTRA_ID, -1);
            if (longExtra != -1) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                edit.putLong(LAST_CITY, longExtra);
                edit.commit();
            }
            setCity(longExtra);
            invalidateOptionsMenu();
            return;
        }
        if (requestCode == REQUEST_CODE_CITY && resultCode != RESULT_OK && mDb.fetchAllCities().getCount() == 0)
        {
            finish();
        }
    }
}

class WeatherManager {
    private static final long UPDATE_INTERVAL = 1500000;
    public  City           currentCity;
    private Context        mContext;
    private UpdateListener mUpdateListener;
    WeatherDBAdapter mDb;

    WeatherManager(Context context, City city, UpdateListener listener) {
        mContext = context;
        mDb = new WeatherDBAdapter(context);
        mUpdateListener = listener;
        mDb.open();
        if (city != null) {
            setLocation(city);
            if (city.weather == null || (System.currentTimeMillis() - city.lastUpdate.getTime()) >= UPDATE_INTERVAL) {
                getWeather(city.id);
                mUpdateListener.onStartLongOperation();
            }
            else
                updateViews(city);
        }
    }

    private void getWeather(final Long city_id) {
        /*new WeatherGetter() {
            @Override
            protected void onProgressUpdate(ForecastResponse... values) {
                super.onProgressUpdate(values);

                if (values != null && values.length > 0 && values[0] != null)
                    updateViews(values[0]);
                mDb.putCity(mCity);
            }
        }.execute(mDb.getCityById(city_id));*/
        Intent i = new Intent(mContext, WeatherUpdateService.class);
        i.putExtra(WeatherUpdateService.CITY_ID, city_id);
        mContext.startService(i);
    }

    public void setLocation(City city) {currentCity = city;}

    public void setUpdateListener(UpdateListener listener) {
        mUpdateListener = listener;
    }

    private String generateTimeAgoText(long moment) {
        long diff = System.currentTimeMillis() - moment;
        if (diff < 5*60*1000) //less than 5 mins
            return "";
        if (diff < 60*60*1000) //less than hour
            return diff / (60*1000) +" "+ mContext.getString(R.string.time_minutes);
        if (diff < 120*60*1000)
            return mContext.getString(R.string.time_an_hour);
        if (diff < 24*60*60*1000)
            return diff / (60*60*1000) +" "+ mContext.getString(R.string.time_hours);
        return mContext.getString(R.string.time_more_than_a_day);
    }

    public void setCurrentCity(City city) {
        currentCity = city;
        if (city.weather != null)
            updateViews(city);
    }

    private void updateViews(City c) {
        ForecastResponse data = c.weather;

        HourlyWeatherData current = data.getCurrently();
        //Weather now
        weatherNowView = View.inflate(mContext, R.layout.weather_now, null);

        TextView tv_date_time = (TextView) weatherNowView.findViewById(R.id.tv_date_time);
        tv_date_time.setText(generateTimeAgoText(c.lastUpdate.getTime()));

        TextView tv_temperature = (TextView) weatherNowView.findViewById(R.id.tv_temperature);
        tv_temperature.setText(Long.toString(Math.round(current.getTemperature())) + mContext.getString(R.string.txt_degrees));

        TextView tv_summary = (TextView) weatherNowView.findViewById(R.id.tv_summary);
        tv_summary.setText(current.getSummary() + "\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), current.getHumidity() * 100) + "%\n");
        tv_summary.append(String.format(mContext.getString(R.string.txt_wind), current.getWindSpeed(), WeatherManager.determineWindDirection(current.getWindBearing()) + "\n"));
        tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), current.getPressure() * 0.75006));

        ImageView iv_weather_icon = (ImageView) weatherNowView.findViewById(R.id.iv_weather_icon);
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

            tv_date_time = (TextView) weatherSoonView.findViewById(R.id.tv_date_time);
            Date d = new Date(h.getTime() * 1000);
            tv_date_time.setText(format.format(d));

            tv_temperature = (TextView) weatherSoonView.findViewById(R.id.tv_temperature);
            tv_temperature.setText(Long.toString(Math.round(h.getTemperature())) + mContext.getString(R.string.txt_degrees));

            tv_summary = (TextView) weatherSoonView.findViewById(R.id.tv_summary);
            tv_summary.setText(h.getSummary() + "\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), h.getHumidity() * 100) + "%\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_wind), h.getWindSpeed(), WeatherManager.determineWindDirection(h.getWindBearing()) + "\n"));
            tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), h.getPressure() * 0.75006));

            iv_weather_icon = (ImageView) weatherSoonView.findViewById(R.id.iv_weather_icon);
            iv_weather_icon.setImageDrawable(getWeatherIcon(h.getIcon()));

            weatherSoonViews.add(weatherSoonView);
        }

        format = new SimpleDateFormat("E', 'MMM d");

        //Weather later

        weatherLaterViews.clear();

        tv_summary = (TextView) View.inflate(mContext, R.layout.weather_soon_summary, null);
        tv_summary.setText(data.getDaily().getSummary());

        weatherLaterViews.add(tv_summary);

        for (int i = 1; i < data.getDaily().getData().length && i <= 4; i++) {
            DailyWeatherData w = data.getDaily().getData()[i];
            View weatherLaterView = View.inflate(mContext, R.layout.weather_soon_daily, null);

            tv_date_time = (TextView) weatherLaterView.findViewById(R.id.tv_date_time);
            Date d = new Date(w.getTime() * 1000);
            tv_date_time.setText(format.format(d));

            tv_temperature = (TextView) weatherLaterView.findViewById(R.id.tv_temperature);
            tv_temperature.setText(Long.toString(Math.round(w.getApparentTemperatureMin())) + mContext.getString(R.string.txt_degrees));
            tv_temperature.append(".." + Long.toString(Math.round(w.getApparentTemperatureMax())) + mContext.getString(R.string.txt_degrees));

            tv_summary = (TextView) weatherLaterView.findViewById(R.id.tv_summary_long);
            tv_summary.setText(w.getSummary() + "\n");

            tv_summary = (TextView) weatherLaterView.findViewById(R.id.tv_summary);
            tv_summary.append(String.format(mContext.getString(R.string.txt_humidity), w.getHumidity() * 100) + "%\n");
            tv_summary.append(String.format(mContext.getString(R.string.txt_wind), w.getWindSpeed(), WeatherManager.determineWindDirection(w.getWindBearing()) + "\n"));
            tv_summary.append(String.format(mContext.getString(R.string.txt_pressure), w.getPressure() * 0.75006));

            iv_weather_icon = (ImageView) weatherLaterView.findViewById(R.id.iv_weather_icon);
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
        void onViewsUpdate(WeatherManager weather);
        void onStartLongOperation();
    }
}