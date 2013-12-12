package ru.ifmo.ctddev.skripnikov.Weather2;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FindLocationActivity {

    public static final String PREFERENCE_ITEM_ID = "itemid";
    public static final String BROADCAST_ACTION = "ru.ifmo.ctddev.skripnikov.Weather2.ba.update";

    private SharedPreferences sp;
    private ProgressBarView progressBar;
    private CurrentWeatherView cwv;
    private ArrayList<City> cities;
    private ArrayList<ForecastWeather> forecastWeathers;
    private BroadcastReceiver br;
    private CitiesListAdapter citiesListAdapter;
    private ForecastWeatherListAdapter forecastWeatherListAdapter;
    private City lastChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        lastChoice = new City(-1, "Add city", "", "", 0, 0, "");
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        cities = new ArrayList<City>();
        forecastWeathers = new ArrayList<ForecastWeather>();
        citiesListAdapter = new CitiesListAdapter(getBaseContext(), cities);
        getActionBar().setListNavigationCallbacks(citiesListAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                if (itemId == -1) {
                    Intent intent = new Intent(getBaseContext(), AddCityActivity.class);
                    startActivity(intent);
                } else {
                    saveSelectedItemId(itemId);
                    redisplayWeather();
                }
                return false;
            }
        });

        updateCities();
        if (cities.size() == 0) {
            Intent intent = new Intent(this, AddCityActivity.class);
            startActivity(intent);
        } else {
            citiesListAdapter.notifyDataSetChanged();
            updateSelectedItem();
        }

        viewInit();

        PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, WeatherFetcherService.class), 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60000, 2000000, pi);

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                redisplayWeather();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateCities();
        if (cities.size() == 0) {
            finish();
        } else {
            citiesListAdapter.notifyDataSetChanged();
            updateSelectedItem();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main_activity);
        viewInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_weather:
                new WeatherFetcher().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewInit() {
        cwv = (CurrentWeatherView) findViewById(R.id.current_weather);
        ListView fw = (ListView) findViewById(R.id.forecast_weather);
        forecastWeatherListAdapter = new ForecastWeatherListAdapter(getBaseContext(), forecastWeathers);
        fw.setAdapter(forecastWeatherListAdapter);
        progressBar = (ProgressBarView) findViewById(R.id.progress_bar);
        progressBar.setText(getResources().getString(R.string.fetching_weather));
        redisplayWeather();
    }

    private void redisplayWeather() {
        updateCities();
        City city = cities.get(getActionBar().getSelectedNavigationIndex());
        if (city.jsonWeather == null) {
            new WeatherFetcher().execute();
        } else {
            cwv.update(city);
            forecastWeathers.clear();
            forecastWeathers.addAll(Arrays.asList(city.getForecastWeather(this)));
            forecastWeatherListAdapter.notifyDataSetChanged();
        }
    }

    private void updateSelectedItem() {
        long id = sp.getLong(PREFERENCE_ITEM_ID, -1);
        int position = 0;
        for (int i = 0; i < cities.size(); i++)
            if (id == cities.get(i).id) {
                position = i;
                break;
            }
        if (getActionBar().getSelectedNavigationIndex() != position)
            getActionBar().setSelectedNavigationItem(position);
    }

    private void saveSelectedItemId(long id) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREFERENCE_ITEM_ID, id);
        editor.apply();
    }

    private void updateCities() {
        DBStorage dbs = new DBStorage(this);
        cities.clear();
        cities.addAll(Arrays.asList(dbs.getCities()));
        cities.add(lastChoice);
        dbs.destroy();
        findLocation();
        if (locationIsFound)
            for (City city : cities) city.setDistance(lat, lon);
    }

    private class WeatherFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DBStorage dbs = new DBStorage(getBaseContext());
            WorldWeatherOnlineAPI.updateWeather(getBaseContext(), dbs, cities.get(getActionBar().getSelectedNavigationIndex()));
            dbs.destroy();
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            redisplayWeather();
            progressBar.hide();
        }
    }
}
