package com.example.weathr;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Sergey on 11/29/13.
 */
public class CitiesActivity extends Activity implements TextWatcher, AdapterView.OnItemClickListener {
    WeatherDBAdapter mDbAdapter;

    ListView lv_cities;
    EditText tv_search;

    private boolean searchMode         = false;
    private boolean currentlySearching = false;

    private void startSearch(String s) {
        searchMode = true;
        currentlySearching = true;
        (findViewById(R.id.pb_progress_indicator)).setVisibility(View.VISIBLE);
        new GeoSearchProvider().execute(URLEncoder.encode(s.trim()));
    }

    private void finishSearch() {
        actual = null;
        currentlySearching = false;
        (findViewById(R.id.pb_progress_indicator)).setVisibility(View.GONE);
    }

    private void exitSearch() {
        searchMode = false;
        finishSearch();
        fillListViewWithCities();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.cities);

        lv_cities = (ListView) findViewById(R.id.lv_cities);
        lv_cities.setOnItemClickListener(this);

        tv_search = (EditText) findViewById(R.id.tv_search);
        tv_search.addTextChangedListener(this);

        ((ImageButton) findViewById(R.id.btn_clear_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_search.getText().clear();
            }
        });

        mDbAdapter = new WeatherDBAdapter(this);
        mDbAdapter.open();

        fillListViewWithCities();
    }

    private void fillListViewWithCities() {
        Cursor c = mDbAdapter.fetchAllCities();
        startManagingCursor(c);
        lv_cities.setAdapter(new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_activated_1,
                c,
                new String[]{WeatherDBAdapter.KEY_NAME},
                new int[]{android.R.id.text1}));
        lv_cities.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_cities.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(String.format(getString(R.string.amo_title), lv_cities.getCheckedItemCount()));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.history_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.mnu_delete) {
                    ArrayList<Long> idsToDelete = new ArrayList<Long>();
                    SparseBooleanArray checked = lv_cities.getCheckedItemPositions();
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i))
                            idsToDelete.add(lv_cities.getAdapter().getItemId(checked.keyAt(i)));
                    }
                    WeatherDBAdapter mDb = new WeatherDBAdapter(CitiesActivity.this);
                    mDb.open();
                    mDb.batchDeleteCities(idsToDelete);
                    mode.finish();
                    fillListViewWithCities();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            startSearch(s.toString());
            (findViewById(R.id.btn_clear_text)).setVisibility(View.VISIBLE);
        } else {
            exitSearch();
            (findViewById(R.id.btn_clear_text)).setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    GeoCompleter actual;

    public static final String EXTRA_ID = "ID";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!searchMode) {
            Intent result = new Intent();
            result.putExtra(EXTRA_ID, id);
            setResult(RESULT_OK, result);
            finish();
        } else {
            GeoCoordsResolver resolver = (GeoCoordsResolver) new GeoCoordsResolver() {
                @Override
                protected void onPostExecute(City city) {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_ID, mDbAdapter.putCity(city));
                    setResult(RESULT_OK, result);
                    finish();
                }
            }.execute(((TextView) view.findViewById(android.R.id.text1)).getText().toString().trim());
        }
    }

    class GeoSearchProvider extends GeoCompleter {
        @Override
        protected void onPreExecute() {
            actual = this;
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if (actual == this && strings != null) {
                lv_cities.setMultiChoiceModeListener(null);
                lv_cities.setAdapter(new ArrayAdapter<String>(
                        CitiesActivity.this
                        , android.R.layout.simple_list_item_1
                        , android.R.id.text1
                        , strings));
                lv_cities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                finishSearch();
            }
        }
    }
}