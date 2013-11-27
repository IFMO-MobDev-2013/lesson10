package ru.georgeee.android.singingintherain;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.viewpagerindicator.TitlePageIndicator;
import ru.georgeee.android.singingintherain.model.City;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 26.11.13
 * Time: 2:24
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends FragmentActivity {

    private static final int CITIES_FRAGMENT_ID = 0;
    AppSectionsPagerAdapter pagerAdapter;
    ViewPager pager;
    TitlePageIndicator titles;

    @Override
    protected void onDestroy() {
        setResult(RESULT_OK, getIntent());
        super.onDestroy();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_fragment_activity);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        titles = (TitlePageIndicator) findViewById(R.id.indicator);
        titles.setViewPager(pager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        CitiesFragment citiesFragment;

        public CitiesFragment getCitiesFragment() {
            return citiesFragment;
        }

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            citiesFragment = new CitiesFragment();
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case CITIES_FRAGMENT_ID:
                    return citiesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            int id = 0;
            switch (i) {
                case CITIES_FRAGMENT_ID:
                    id = R.string.citiesSettingsTitle;
                    break;
            }
            return getResources().getString(id);
        }

    }
}