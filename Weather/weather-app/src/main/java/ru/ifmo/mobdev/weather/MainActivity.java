package ru.ifmo.mobdev.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;

import ru.ifmo.mobdev.weather.adapters.TabsAdapter;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;
import ru.ifmo.mobdev.weather.fragments.CityWeatherFragment;
import ru.ifmo.mobdev.weather.fragments.CurrentWeatherFragment;
import ru.ifmo.mobdev.weather.fragments.ForecastFragment;
import ru.ifmo.mobdev.weather.service.UpdateWeatherService;

/**
 * Created by Nick Smelik on 26.11.13.
 */
public class MainActivity extends FragmentActivity {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(UpdateWeatherService.UPDATE_DONE);
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private dbOpenHelper helper;
    DataBaseTable table;
    Context ctx;
    ImageView update;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dropAnimation();
        }
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);

        helper = new dbOpenHelper(this);
        table = new DataBaseTable(helper.getWritableDatabase());

        UpdateWeatherService.ensureUpdating(this, true);
        ctx = this;

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        update = (ImageView) findViewById(R.id.refresh);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabHost.setup();

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("city_weather").setIndicator(getString(R.string.city)), CityWeatherFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("current_weather").setIndicator(getString(R.string.current)), CurrentWeatherFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("forecast").setIndicator(getString(R.string.forecast)), ForecastFragment.class, null);

        final Animation rotate_long = AnimationUtils.loadAnimation(this, R.anim.rotate_center_long);

        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                update.startAnimation(rotate_long);
                UpdateWeatherService.requestUpdate(ctx, true);
            }

        });

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        mViewPager.setCurrentItem(1);
    }

    public void dropAnimation() {
        update.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, UPDATE_FILTER);
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
}
