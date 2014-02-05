package md.zoidberg.android.forecast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends FragmentActivity implements YahooWeatherInfoListener {
    private static final String WAKE_EVENT = "md.zoidberg.wake";
    private List<String> places;
    public static final String NEW_CITY_EXTRA = "NEW_CITY_EXTRA";
    public static final int NEW_CITY_CODE = 1;

    private PagerAdapter weatherAdapter;
    private ForecastDBHelper helper;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ForecastDBHelper(this);
        places = helper.getCities();
        updateScreenView();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (String city: helper.getCities()) {
                    YahooWeather.getInstance().queryYahooWeatherByPlaceName(context, city, ForecastActivity.this);
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(WAKE_EVENT));
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(WAKE_EVENT);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC, AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, alarmIntent);

    }

    public void updateScreenView() {
        weatherAdapter = new WeatherAdapter(getSupportFragmentManager(), places);
        if (places.size() == 0) {
            setContentView(R.layout.no_cities);
        } else {
            setContentView(R.layout.main);
            ((ViewPager) findViewById(R.id.weatherPager)).setAdapter(weatherAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.btn_add_city:
                startActivityForResult(new Intent(this, CityAddActivity.class), NEW_CITY_CODE);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_CITY_CODE && resultCode == RESULT_OK) {
            String newCity = data.getStringExtra(NEW_CITY_EXTRA);
            Log.d("cityValue", newCity);
            places.add(newCity);
            weatherAdapter.notifyDataSetChanged();
            updateScreenView();
        }
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        helper.updateCity(weatherInfo.getLocationCity(), weatherInfo);
    }
}
