package com.weather.android.dronov;

/**
 * Support Article : http://just-another-blog.net/programming/how-to-implement-horizontal-view-swiping-with-tabs/
 */

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.weather.android.dronov.Forecast.Forecast;
import com.weather.android.dronov.database.WeatherDB;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    public static final String ITEM_INDEX = "item";
    private ViewPager mViewPager;
    private ArrayList<Forecast> cities = null;
    private IntentFilter intentFilter;
    private WeatherBroadcast weatherBroadcast;
    SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.View_Pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        WeatherDB weatherDB = new WeatherDB(this);
        weatherDB.open();
        cities = weatherDB.getAllData();

        Intent serviceIntent = new Intent(this, UpdateService.class);
        startService(serviceIntent);

        WeatherBroadcast weatherBroadcast = new WeatherBroadcast();
        intentFilter = new IntentFilter(UpdateService.key);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(weatherBroadcast, intentFilter);

        Button button = (Button) findViewById(R.id.settingsButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForecastManipulator.class);
                startActivity(intent);
                finish();
            }
        });

        ImageButton refreshButton = (ImageButton) findViewById(R.id.Refresh);
        refreshButton.setImageResource(R.drawable.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherFragment weatherFragment = (WeatherFragment) mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
                weatherFragment.FragmentUpdate();

            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new WeatherFragment();
            Bundle args = new Bundle();
            args.putString(WeatherDB.COLUMN_CITY, String.valueOf(cities.get(position).getCity()));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            if (cities == null)
                return 0;
            else
                return cities.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return cities.get(position).getCity();
        }
    }

    private class WeatherBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }

    @Override
    protected void onPause() {
        if (weatherBroadcast != null)
            unregisterReceiver(weatherBroadcast);
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onResume() {
        if (weatherBroadcast != null)
            registerReceiver(weatherBroadcast, intentFilter);
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
