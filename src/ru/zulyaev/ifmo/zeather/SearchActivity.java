package ru.zulyaev.ifmo.zeather;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import ru.zulyaev.ifmo.zeather.weather.City;
import ru.zulyaev.ifmo.zeather.weather.SearchResult;
import ru.zulyaev.ifmo.zeather.weather.WeatherApi;

import java.io.IOException;
import java.util.List;

/**
 * @author seidhe
 */
public class SearchActivity extends Activity {
    public static final String QUERY_INDEX = "query";

    private static final WeatherApi API = new WeatherApi();

    private String initialQuery;

    private ProgressBar progressBar;
    private ListView listView;
    private TextView message;

    private CitiesAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initialQuery = intent.getStringExtra(QUERY_INDEX);

        setContentView(R.layout.search);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.search_results);
        message = (TextView) findViewById(R.id.message);

        adapter = new CitiesAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = adapter.getItem(position);
                WeatherDbOpenHelper helper = new WeatherDbOpenHelper(SearchActivity.this);
                WeatherTable table = new WeatherTable(helper.getWritableDatabase());
                table.addLocation(city.getTitle(), city.getLatitude(), city.getLongitude());
                helper.close();
                finish();
            }
        });

        if (initialQuery == null) {
            setState(SearchState.INITITAL);
        } else {
            new SearchTask().execute(initialQuery);
            setState(SearchState.LOADING);
        }
    }

    private void setState(SearchState state) {
        state.apply(this);
    }

    private void setCities(List<City> cities) {
        if (cities == null) {
            setState(SearchState.ERROR);
        } else if (cities.isEmpty()) {
            setState(SearchState.NO_RESULTS);
        } else {
            adapter.setCities(cities);
            setState(SearchState.RESULTS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setState(SearchState.LOADING);
                new SearchTask().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
        if (initialQuery == null) {
            searchView.setIconified(false);
        } else {
            searchView.setQuery(initialQuery, false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    class SearchTask extends AsyncTask<String, Void, SearchResult> {

        @Override
        protected SearchResult doInBackground(String... params) {
            try {
                return API.search(params[0]);
            } catch (IOException e) {
                Log.w(SearchActivity.class.toString(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchResult searchResult) {
            setCities(searchResult == null ? null : searchResult.getCities());
        }
    }

    enum SearchState {
        LOADING {
            @Override
            void apply(SearchActivity activity) {
                activity.listView.setVisibility(View.GONE);
                activity.progressBar.setVisibility(View.VISIBLE);
                activity.message.setVisibility(View.GONE);
            }
        },
        RESULTS {
            @Override
            void apply(SearchActivity activity) {
                activity.listView.setVisibility(View.VISIBLE);
                activity.progressBar.setVisibility(View.GONE);
                activity.message.setVisibility(View.GONE);
            }
        },
        ERROR {
            @Override
            void apply(SearchActivity activity) {
                activity.listView.setVisibility(View.GONE);
                activity.progressBar.setVisibility(View.GONE);
                activity.message.setVisibility(View.VISIBLE);
                activity.message.setText(R.string.search_error);
            }
        },
        NO_RESULTS {
            @Override
            void apply(SearchActivity activity) {
                activity.listView.setVisibility(View.GONE);
                activity.progressBar.setVisibility(View.GONE);
                activity.message.setVisibility(View.VISIBLE);
                activity.message.setText(R.string.search_no_results);
            }
        },
        INITITAL {
            @Override
            void apply(SearchActivity activity) {
                activity.listView.setVisibility(View.GONE);
                activity.progressBar.setVisibility(View.GONE);
                activity.message.setVisibility(View.VISIBLE);
                activity.message.setText(R.string.search_initial);
            }
        };

        abstract void apply(SearchActivity activity);
    }
}