package ru.ifmo.ctddev.skripnikov.Weather2;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FindLocationActivity {

    public static final String PREFERENCE_ITEM_ID = "itemid";
    public static final String BROADCAST_ACTION = "ru.ifmo.ctddev.skripnikov.Weather2.ba.update";

    private SharedPreferences sp;
    private ProgressBarView progressBar;
    private CurrentWeatherView cwv;
    private City[] cities;
    private ListView fw;
    private BroadcastReceiver br;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        cwv = (CurrentWeatherView) findViewById(R.id.current_weather);
        fw = (ListView) findViewById(R.id.forecast_weather);
        progressBar = (ProgressBarView) findViewById(R.id.progress_bar);
        progressBar.setText(getResources().getString(R.string.fetching_weather));
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        updateCities();
        if (cities.length == 0) {
            Intent intent = new Intent(this, AddCityActivity.class);
            startActivity(intent);
        } else {
            cityListInit();
        }

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

    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BROADCAST_ACTION));
    }

    private void cityListInit() {
        CitiesListAdapter adapter = new CitiesListAdapter(getBaseContext(), cities);
        getActionBar().setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                saveSelectedItemId(itemId);
                redisplayWeather();
                return false;
            }
        });
        updateSelectedItem();
    }

    private void redisplayWeather() {
        updateCities();
        City city = cities[getActionBar().getSelectedNavigationIndex()];
        if (city.jsonWeather == null) {
            new WeatherFetcher().execute();
        } else {
            cwv.update(city);
            ForecastWeatherListAdapter adapter = new ForecastWeatherListAdapter(getBaseContext(),
                    city.getForecastWeather(getBaseContext()));
            fw.setAdapter(adapter);
        }
    }

    private void updateSelectedItem() {
        long id = sp.getLong(PREFERENCE_ITEM_ID, -1);
        int position = 0;
        for (int i = 0; i < cities.length; i++)
            if (id == cities[i].id) {
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
        cities = dbs.getCities();
        dbs.destroy();
        findLocation();
        if (locationIsFound)
            for (City city : cities) city.setDistance(lat, lon);
    }

    public void onRestart() {
        super.onRestart();
        updateCities();
        if (cities.length == 0) {
            finish();
        } else {
            cityListInit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_city:
                Intent intent = new Intent(this, AddCityActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_weather:
                new WeatherFetcher().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class WeatherFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DBStorage dbs = new DBStorage(getBaseContext());
            WorldWeatherOnlineAPI.updateWeather(getBaseContext(), dbs, cities[getActionBar().getSelectedNavigationIndex()]);
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
