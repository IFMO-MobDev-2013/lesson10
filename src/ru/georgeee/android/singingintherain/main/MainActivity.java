package ru.georgeee.android.singingintherain.main;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.viewpagerindicator.TitlePageIndicator;
import ru.georgeee.android.singingintherain.R;
import ru.georgeee.android.singingintherain.fragment.ForecastFragment;
import ru.georgeee.android.singingintherain.fragment.HourlyFragment;
import ru.georgeee.android.singingintherain.fragment.NowSectionFragment;
import ru.georgeee.android.singingintherain.fragment.WeekFragment;
import ru.georgeee.android.singingintherain.misc.UpdateForecastService;
import ru.georgeee.android.singingintherain.model.City;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final int NOW_FRAGMENT_ID = 0;
    private static final int HOURLY_FRAGMENT_ID = 1;
    private static final int WEEK_FRAGMENT_ID = 2;
    private static final String CURRENT_FRAGMENT_TAG = "fragmentPosition";
    private static int settingsActivityRequestCode = 1;
    AppSectionsPagerAdapter pagerAdapter;
    ArrayAdapter<City> spinnerAdapter;
    ViewPager pager;
    TitlePageIndicator titles;
    City selectedCity;
    IntentFilter intentFilter;
    FBroadcastReceiver broadcastReceiver;
    ActionBar actionBar;
    TextView lastUpdatedTextView;
    DateFormat lastUpdatedFormat;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        spinnerAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_spinner_dropdown_item, City.loadAllFromDB());

        lastUpdatedTextView = (TextView) findViewById(R.id.forecastLastUpdated);
        lastUpdatedFormat = new SimpleDateFormat(getString(R.string.lastUpdatedTimeFormat));

        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                selectedCity = spinnerAdapter.getItem(itemPosition);
                updateCity();
                return true;
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);


        titles = (TitlePageIndicator) findViewById(R.id.indicator);
        titles.setViewPager(pager);

        broadcastReceiver = new FBroadcastReceiver();
        intentFilter = new IntentFilter(UpdateForecastService.BROADCAST_KEY);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);
        actionBar.setSelectedNavigationItem(savedInstanceState != null
                ? savedInstanceState.getInt(CURRENT_FRAGMENT_TAG)
                : 0);
        selectedCity = spinnerAdapter.getItem(actionBar.getSelectedNavigationIndex());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT_TAG, actionBar.getSelectedNavigationIndex());
    }

    public void updateCity() {
        pagerAdapter.update(selectedCity = selectedCity.getUpdated());
        Date lastUpdated = selectedCity==null?null: selectedCity.getForecastLastUpdated();
        if(lastUpdated == null)
            lastUpdatedTextView.setText("");
        else
            lastUpdatedTextView.setText(getString(R.string.lastUpdatedLabel, lastUpdatedFormat.format(lastUpdated)));

    }

    public void reloadCities() {
        List<City> cities = City.loadAllFromDB();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(cities);
        int pos = spinnerAdapter.getPosition(selectedCity);
        if (pos < 0) {
            pos = 0;
            selectedCity = spinnerAdapter.getItem(0);
        }
        actionBar.setSelectedNavigationItem(pos);
        updateCity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadCities();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                UpdateForecastService.startService(this, selectedCity);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, settingsActivityRequestCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == settingsActivityRequestCode) {
            reloadCities();
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends ForecastFragment {
        @Override
        protected int getLayoutId() {
            return R.layout.fragment_section_dummy;
        }

        @Override
        protected void onUpdateViewImpl() {
            if (rootView != null && city != null)
                ((TextView) rootView.findViewById(R.id.text1)).setText(getString(R.string.dummy_section_text, city.getName()));
        }

    }

    class FBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            City _city = (City) extras.getSerializable(UpdateForecastService.RESPONSE_VARIABLE_CITY);
            if (_city.getId() == selectedCity.getId()) {
                selectedCity = _city;
            }
            updateCity();
        }
    }

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        ForecastFragment nowFragment;
        ForecastFragment hourlyFragment;
        ForecastFragment weekFragment;

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            nowFragment = new NowSectionFragment();
            hourlyFragment = new HourlyFragment();
            weekFragment = new WeekFragment();
            List<Fragment> fragments = fm.getFragments();
            if (fragments != null) {
                if (NOW_FRAGMENT_ID < fragments.size())
                    nowFragment = (ForecastFragment) fragments.get(NOW_FRAGMENT_ID);
                if (HOURLY_FRAGMENT_ID < fragments.size())
                    hourlyFragment = (ForecastFragment) fragments.get(HOURLY_FRAGMENT_ID);
                if (WEEK_FRAGMENT_ID < fragments.size())
                    weekFragment = (ForecastFragment) fragments.get(WEEK_FRAGMENT_ID);
            }
        }

        public void update(City city) {
            if (nowFragment != null) nowFragment.setCity(city);
            if (hourlyFragment != null) hourlyFragment.setCity(city);
            if (weekFragment != null) weekFragment.setCity(city);
        }

        @Override
        public ForecastFragment getItem(int i) {
            Log.d(MainActivity.class.getCanonicalName(), "pagerAdapter.getItem(" + i + ")");
            switch (i) {
                case NOW_FRAGMENT_ID:
                    return nowFragment;
                case HOURLY_FRAGMENT_ID:
                    return hourlyFragment;
                case WEEK_FRAGMENT_ID:
                    return weekFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            int id = 0;
            switch (i) {
                case NOW_FRAGMENT_ID:
                    id = R.string.nowSectionLabel;
                    break;
                case HOURLY_FRAGMENT_ID:
                    id = R.string.hourlySectionLabel;
                    break;
                case WEEK_FRAGMENT_ID:
                    id = R.string.weekSectionLabel;
                    break;
            }
            return getResources().getString(id);
        }


    }
}