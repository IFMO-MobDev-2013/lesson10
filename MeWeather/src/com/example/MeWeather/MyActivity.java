package com.example.MeWeather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.apache.http.client.methods.HttpHead;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class MyActivity extends FragmentActivity {
    /**
     * Called when the activity is first created.
     * ViruZ
     */
    private DBHelper dbHelper;
    DBWeather dbWeather;
    private Context context;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        dbHelper = new DBHelper(this);
        dbWeather = new DBWeather(dbHelper.getWritableDatabase());


        iServis.ensureUpdating(this, true);
        context = this;
        iServis.requestUpdate(context, true);
        try {
            ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

            Vector<Fragment> fragments = new Vector<>();
            fragments.add(Fragment.instantiate(this, fragmentone.class.getName()));
            fragments.add(Fragment.instantiate(this, fragmenttwo.class.getName()));
            fragments.add(Fragment.instantiate(this, fragmentthree.class.getName()));
            viewPager.setOffscreenPageLimit(fragments.size());
            adscroll adScroll = new adscroll(super.getSupportFragmentManager(), fragments);
            viewPager.setAdapter(adScroll);
            viewPager.setCurrentItem(1);
        } catch (NullPointerException e) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
