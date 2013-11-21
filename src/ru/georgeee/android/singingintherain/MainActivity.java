package ru.georgeee.android.singingintherain;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.viewpagerindicator.TitlePageIndicator;
import ru.georgeee.android.singingintherain.model.City;

public class MainActivity extends FragmentActivity {

    private static final int NOW_FRAGMENT_ID = 0;
    private static final int T48H_FRAGMENT_ID = 1;
    private static final int WEEK_FRAGMENT_ID = 2;
    AppSectionsPagerAdapter pagerAdapter;
    SpinnerAdapter spinnerAdapter;
    ViewPager pager;
    TitlePageIndicator titles;
    City selectedCity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.names,
                android.R.layout.simple_spinner_dropdown_item);

        final String[] cities = getResources().getStringArray(R.array.names);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                selectedCity = new City(cities[itemPosition]);
                pagerAdapter.update(selectedCity);
                return true;
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        titles = (TitlePageIndicator) findViewById(R.id.indicator);
        titles.setViewPager(pager);

        selectedCity = new City(cities[0]);

    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends CityDependentFragment {
        @Override
        protected int getLayoutId() {
            return R.layout.fragment_section_dummy;
        }

        @Override
        protected void onUpdateViewImpl() {
            ((TextView) rootView.findViewById(R.id.text1)).setText(getString(R.string.dummy_section_text, city.getName()));
        }

    }

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        CityDependentFragment[] fragments;


        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new CityDependentFragment[3];
            fragments[NOW_FRAGMENT_ID] = new NowSectionFragment();
            fragments[T48H_FRAGMENT_ID] = new DummySectionFragment();
            fragments[WEEK_FRAGMENT_ID] = new DummySectionFragment();
        }

        public void update(City city) {
            for (CityDependentFragment fragment : fragments) fragment.setCity(city);
        }

        @Override
        public CityDependentFragment getItem(int i) {
            Log.d(getClass().getCanonicalName(), "getItem(" + i + ")");
            CityDependentFragment fragment = fragments[i];
            fragment.setCity(selectedCity);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            int id = 0;
            switch (i) {
                case NOW_FRAGMENT_ID:
                    id = R.string.nowSectionLabel;
                    break;
                case T48H_FRAGMENT_ID:
                    id = R.string.t48HSectionLabel;
                    break;
                case WEEK_FRAGMENT_ID:
                    id = R.string.weekSectionLabel;
                    break;
            }
            return getResources().getString(id);
        }
    }
}