package ru.zulyaev.ifmo.zeather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

public class MainActivity extends Activity {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(WeatherUpdateService.UPDATE_DONE);
    private static final String DIALOG = "DIALOG";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
        }
    };
    private final SearchView.OnQueryTextListener ON_SEARCH_QUERY_LISTENER = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchItem.collapseActionView();
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra(SearchActivity.QUERY_INDEX, query);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
    private final CityDeletionDialogFragment.CityDeletionListener cityDeletionListener = new CityDeletionDialogFragment.CityDeletionListener() {
        @Override
        public void onDelete(WeatherForecast forecast) {
            table.removeLocation(forecast.getId());
            refreshWeather();
        }
    };
    private WeatherDbOpenHelper helper;
    private WeatherTable table;
    private WeatherAdapter adapter;
    private View noCities;
    private MenuItem searchItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new WeatherDbOpenHelper(this);
        table = new WeatherTable(helper.getWritableDatabase());

        setContentView(R.layout.main);

        ListView view = (ListView) findViewById(R.id.cities);
        noCities = findViewById(R.id.no_cities);

        adapter = new WeatherAdapter(this, table);
        view.setAdapter(adapter);

        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new CityDeletionDialogFragment(adapter.getItem(position), cityDeletionListener).show(getFragmentManager(), DIALOG);
                return true;
            }
        });

        noCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        WeatherUpdateService.ensureUpdating(this, true);
    }

    private void refreshWeather() {
        adapter.refresh();
        if (adapter.getCount() == 0) {
            noCities.setVisibility(View.VISIBLE);
        } else {
            noCities.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, UPDATE_FILTER);
        refreshWeather(); // in case if we added a city in SearchActivity
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(ON_SEARCH_QUERY_LISTENER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            WeatherUpdateService.requestUpdate(this, true);
            return true;
        }
        return false;
    }
}
