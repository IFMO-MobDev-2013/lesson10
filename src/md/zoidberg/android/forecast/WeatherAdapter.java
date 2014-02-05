package md.zoidberg.android.forecast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gfv on 03.02.14.
 */

public class WeatherAdapter extends FragmentStatePagerAdapter {
    List<String> cities;
    public WeatherAdapter(FragmentManager fm, List<String> cities) {
        super(fm);
        this.cities = cities;
    }

    @Override
    public Fragment getItem(int i) {
        return new WeatherFragment(cities.get(i));
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return cities.get(i);
    }
}
