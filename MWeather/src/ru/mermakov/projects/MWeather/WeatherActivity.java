package ru.mermakov.projects.MWeather;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import ru.mermakov.projects.MWeather.Utils.*;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends Activity {

    private TextView date, temp, descr, wind, press, hum, curCity;
    private ImageView picture;
    private String city, country;
    private long city_id;
    ListView lv2, lv3;
    WeatherDBAdapter mDbHelper;
    private EditText addCity;
    private ImageButton imgcity, update;
    public static Dialog dialog;
    View vvv;
    WeatherBroadcastReceiver myBroadcastReceiver;
    WeatherUpdater wUpdater;
    private static final long TWENTY_MINUTES = 1000 * 60 * 15;
    private static final long TWO_HOURS = TWENTY_MINUTES * 3 * 2;
    static AlarmManager am;
    PendingIntent pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        mDbHelper = new WeatherDBAdapter(this);
        mDbHelper.open();
        //mDbHelper.drop();

        LayoutInflater inflater = LayoutInflater.from(WeatherActivity.this);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            List<View> pages = new ArrayList<View>();

            View page1 = inflater.inflate(R.layout.today_layout, null);
            View page2 = inflater.inflate(R.layout.week_layout, null);
            View page3 = inflater.inflate(R.layout.cities_layout, null);

            initialiseViews(page1, page2, page3);
            initialiseReceivers(page1, page2, page3);

            city_id = mDbHelper.getLastUpdatedID();
            if (city_id != -1) {
                curCity.setText(mDbHelper.getCityNameByID(city_id));
            } else {
                curCity.setText("Select city ->");
                mDbHelper.createLast(-1);
            }

            startAlarm();

            setToday();
            setWeek();
            setCities();

            pages.add(page1);
            pages.add(page2);
            pages.add(page3);

            WeatherPagerAdapter pagerAdapter = new WeatherPagerAdapter(pages);
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);

            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            initialiseViewsTab();
            View all = inflater.inflate(R.layout.main, null);
            initialiseReceiversTab();

            city_id = mDbHelper.getLastUpdatedID();
            if (city_id != -1) {
                curCity.setText(mDbHelper.getCityNameByID(city_id));
            } else {
                curCity.setText("Select city ->");
                mDbHelper.createLast(-1);
            }

            startAlarm();

            setToday();
            setWeek();
            setCities();

            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(wUpdater);
        super.onDestroy();
    }

    private void startAlarm() {
        Intent in = new Intent(WeatherUpdater.ACTION_WEATHERUPDATER);
        pi = PendingIntent.getBroadcast(WeatherActivity.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.cancel(pi);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + TWO_HOURS, TWO_HOURS, pi);
    }

    private void initialiseReceivers(View page1, View page2, View page3) {
        myBroadcastReceiver = new WeatherBroadcastReceiver(curCity,
                (ListView)page2.findViewById(R.id.lv2), (ListView)page3.findViewById(R.id.lv3), (TextView)page1.findViewById(R.id.txtvDate),
                (TextView)page1.findViewById(R.id.txtvtemp), (TextView)page1.findViewById(R.id.txtvDescr), (TextView)
                page1.findViewById(R.id.txtvWind),(TextView)page1.findViewById(R.id.txtvPress), (TextView)page1.findViewById(R.id.txtwHum),
                (ImageButton)findViewById(R.id.update), (ImageView)page1.findViewById(R.id.imageView), (EditText)page3.findViewById(R.id.addcity));
        IntentFilter intentFilter = new IntentFilter(WeatherIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        wUpdater = new WeatherUpdater();
        IntentFilter intentFilter3 = new IntentFilter(WeatherUpdater.ACTION_WEATHERUPDATER);
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(wUpdater, intentFilter3);
    }

    private void initialiseReceiversTab() {
        myBroadcastReceiver = new WeatherBroadcastReceiver(curCity,
                (ListView)findViewById(R.id.lv2), (ListView)findViewById(R.id.lv3), (TextView)findViewById(R.id.txtvDate),
                (TextView)findViewById(R.id.txtvtemp), (TextView)findViewById(R.id.txtvDescr), (TextView)
                findViewById(R.id.txtvWind),(TextView)findViewById(R.id.txtvPress), (TextView)findViewById(R.id.txtwHum),
                (ImageButton)findViewById(R.id.update), (ImageView)findViewById(R.id.imageView), (EditText)findViewById(R.id.addcity));
        IntentFilter intentFilter = new IntentFilter(WeatherIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        wUpdater = new WeatherUpdater();
        IntentFilter intentFilter3 = new IntentFilter(WeatherUpdater.ACTION_WEATHERUPDATER);
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(wUpdater, intentFilter3);
    }

    private void initialiseViewsTab() {
        //------today-----
        date =    (TextView) findViewById(R.id.txtvDate);
        temp =    (TextView) findViewById(R.id.txtvtemp);
        descr =   (TextView) findViewById(R.id.txtvDescr);
        wind =    (TextView) findViewById(R.id.txtvWind);
        press =   (TextView) findViewById(R.id.txtvPress);
        hum =     (TextView) findViewById(R.id.txtwHum);
        picture = (ImageView) findViewById(R.id.imageView);
        //-----all-----
        curCity = (TextView) findViewById(R.id.upCity);
        update = (ImageButton) findViewById(R.id.update);
        //-----week------
        lv2 = (ListView) findViewById(R.id.lv2);
        //-----city------
        addCity = (EditText) findViewById(R.id.addcity);
        imgcity = (ImageButton) findViewById(R.id.imbv3);
        lv3 = (ListView) findViewById(R.id.lv3);
        //-------------

        imgcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country = addCity.getText().toString();
                int ind = country.indexOf(',');
                if (ind != -1) {
                    city = country.substring(0, ind);
                    country = country.substring(ind + 1);
                    country = country.trim();
                    city = city + ", " + country;
                    mDbHelper.createCity(city, country);
                    mDbHelper.createLast(mDbHelper.getCityIdByName(city));
                    curCity.setText(city);
                    Intent intentMyIntentService = new Intent(WeatherActivity.this, WeatherIntentService.class);
                    intentMyIntentService.putExtra("city", city);
                    intentMyIntentService.putExtra("screen", "YES");
                    startService(intentMyIntentService);
                    setCities();
                    dialog = ProgressDialog.show(WeatherActivity.this, "Wait, please", null, true);
                } else {
                    Toast toast = Toast.makeText(WeatherActivity.this, "Wrong format!", 3000);
                    toast.show();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country = curCity.getText().toString();
                int ind = country.indexOf(',');
                city = country.substring(0, ind);
                country = country.substring(ind + 1);
                country = country.trim();
                city = city + ", " + country;
                mDbHelper.createCity(city, country);
                curCity.setText(city);
                Intent intentMyIntentService = new Intent(WeatherActivity.this, WeatherIntentService.class);
                intentMyIntentService.putExtra("city", city);
                intentMyIntentService.putExtra("screen", "YES");
                startService(intentMyIntentService);
                setCities();
                dialog = ProgressDialog.show(WeatherActivity.this, "Wait, please", null, true);
            }
        });

        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ct = (TextView) view.findViewById(R.id.tv3city);
                //TextView cn = (TextView) view.findViewById(R.id.tv3country);
                city = ct.getText().toString();
                //country = cn.getText().toString();
                curCity.setText(city);
                mDbHelper.createLast(mDbHelper.getCityIdByName(city));
                setToday();
                setWeek();
            }
        });
        lv3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vvv = view;
                AlertDialog.Builder ad = new AlertDialog.Builder(WeatherActivity.this);
                ad.setTitle(R.string.sure);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView ctv = (TextView) vvv.findViewById(R.id.tv3city);
                        String ct = ctv.getText().toString();
                        mDbHelper.deleteCity(mDbHelper.getCityIdByName(ct));
                        setCities();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.show();
                return true;
            }
        });
    }

    private void initialiseViews(View page1, View page2, View page3) {
        //------today-----
        date =    (TextView) page1.findViewById(R.id.txtvDate);
        temp =    (TextView) page1.findViewById(R.id.txtvtemp);
        descr =   (TextView) page1.findViewById(R.id.txtvDescr);
        wind =    (TextView) page1.findViewById(R.id.txtvWind);
        press =   (TextView) page1.findViewById(R.id.txtvPress);
        hum =     (TextView) page1.findViewById(R.id.txtwHum);
        picture = (ImageView) page1.findViewById(R.id.imageView);
        //-----all-----
        curCity = (TextView) findViewById(R.id.upCity);
        update = (ImageButton) findViewById(R.id.update);
        //-----week------
        lv2 = (ListView) page2.findViewById(R.id.lv2);
        //-----city------
        addCity = (EditText) page3.findViewById(R.id.addcity);
        imgcity = (ImageButton) page3.findViewById(R.id.imbv3);
        lv3 = (ListView) page3.findViewById(R.id.lv3);
        //-------------

        imgcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country = addCity.getText().toString();
                int ind = country.indexOf(',');
                if (ind != -1) {
                    city = country.substring(0, ind);
                    country = country.substring(ind + 1);
                    country = country.trim();
                    city = city + ", " + country;
                    mDbHelper.createCity(city, country);
                    mDbHelper.createLast(mDbHelper.getCityIdByName(city));
                    curCity.setText(city);
                    Intent intentMyIntentService = new Intent(WeatherActivity.this, WeatherIntentService.class);
                    intentMyIntentService.putExtra("city", city);
                    intentMyIntentService.putExtra("screen", "YES");
                    startService(intentMyIntentService);
                    setCities();
                    dialog = ProgressDialog.show(WeatherActivity.this, "Wait, please", null, true);
                } else {
                    Toast toast = Toast.makeText(WeatherActivity.this, "Wrong format!", 3000);
                    toast.show();
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country = curCity.getText().toString();
                int ind = country.indexOf(',');
                city = country.substring(0, ind);
                country = country.substring(ind + 1);
                country = country.trim();
                city = city + ", " + country;
                mDbHelper.createCity(city, country);
                curCity.setText(city);
                Intent intentMyIntentService = new Intent(WeatherActivity.this, WeatherIntentService.class);
                intentMyIntentService.putExtra("city", city);
                intentMyIntentService.putExtra("screen", "YES");
                startService(intentMyIntentService);
                setCities();
                dialog = ProgressDialog.show(WeatherActivity.this, "Wait, please", null, true);
            }
        });
        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ct = (TextView) view.findViewById(R.id.tv3city);
                //TextView cn = (TextView) view.findViewById(R.id.tv3country);
                city = ct.getText().toString();
                //country = cn.getText().toString();
                curCity.setText(city);
                mDbHelper.createLast(mDbHelper.getCityIdByName(city));
                setToday();
                setWeek();
            }
        });
        lv3.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vvv = view;
                AlertDialog.Builder ad = new AlertDialog.Builder(WeatherActivity.this);
                ad.setTitle(R.string.sure);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView ctv = (TextView) vvv.findViewById(R.id.tv3city);
                        String ct = ctv.getText().toString();
                        mDbHelper.deleteCity(mDbHelper.getCityIdByName(ct));
                        setCities();
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.show();
                return true;
            }
        });
    }

    private void setToday() {
        city = curCity.getText().toString();
        city_id = mDbHelper.getCityIdByName(city);
        Cursor cursor = mDbHelper.fetchCityToday(city_id);

        int dateind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DATE);
        int tempind = cursor.getColumnIndex(WeatherDBAdapter.KEY_TEMP);
        int descrind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DESCR);
        int windind = cursor.getColumnIndex(WeatherDBAdapter.KEY_WIND);
        int pressind = cursor.getColumnIndex(WeatherDBAdapter.KEY_PRESS);
        int humind = cursor.getColumnIndex(WeatherDBAdapter.KEY_HUM);
        int pic_id = cursor.getColumnIndex(WeatherDBAdapter.KEY_ICON_ID);

        cursor.moveToNext();
        String dt, dr, tm, wn, pr, hm;
        int p_id;

        try {
            dt = cursor.getString(dateind);
            dr = cursor.getString(descrind);
            tm = cursor.getString(tempind);
            wn = cursor.getString(windind);
            pr = cursor.getString(pressind);
            hm = cursor.getString(humind);
            p_id = cursor.getInt(pic_id);
            date.setText("Observation time: " + dt);
            if (Integer.parseInt(tm) > 0) temp.setText("+" + tm + "°C");
            else temp.setText(tm + "°C");
            descr.setText(dr);
            wind.setText("Wind " + wn + " Km/h");
            press.setText("Pressure " + pr + " mb");
            hum.setText("Humidity " + hm + "%");
            picture.setImageResource(p_id);
        } catch (Exception e) {
        }
        cursor.close();
    }

    private void setWeek() {
        city = curCity.getText().toString();
        city_id = mDbHelper.getCityIdByName(city);
        Cursor artCursor = mDbHelper.fetchCityWeek(city_id);
        String[] from = new String[]{WeatherDBAdapter.KEY_DATE, WeatherDBAdapter.KEY_MIN_TEMP, WeatherDBAdapter.KEY_MAX_TEMP, WeatherDBAdapter.KEY_DESCR, WeatherDBAdapter.KEY_ICON_ID};
        int[] to = new int[]{R.id.tv2date, R.id.tv2mintemp, R.id.tv2maxtemp, R.id.tv2descr, R.id.weekPic};
        SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(WeatherActivity.this, R.layout.week_list_item, artCursor, from, to);
        lv2.setAdapter(artAdapter);
    }

    private void setCities() {
        Cursor artCursor = mDbHelper.fetchAllCities();
        String[] from = new String[]{WeatherDBAdapter.KEY_CITY};
        int[] to = new int[]{R.id.tv3city};
        SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(WeatherActivity.this, R.layout.city_list_item, artCursor, from, to);
        lv3.setAdapter(artAdapter);
    }
}
