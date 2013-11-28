package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.support.v4.view.ViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 25.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class Main extends Activity implements DbConstants{

    private static final String TAG = "FUCKENFUCK::Main ";

    // label
    TextView currentCity;
    TextView currentCountry;
    ProgressBar progressBar;
    ImageButton imb;

    //view pager's
    LayoutInflater inflater;
    List<View> pages;
    ViewPager viewPager;
    SamplePagerAdapter pagerAdapter;

    // local storage
    List<City> cities;
    List<Current> current;
    List<Forecast> forecast;

    // saved
    private static final String KEY_SAVED = "currentCity.saved";
    SharedPreferences sPref;
    public static final String KEY_GRAD = "°";

    DbAdapter mDbHelper;


    private static final int DELETE_ID = Menu.FIRST;

    //now
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    ImageView image;

    //cities
    AutoCompleteTextView autoCompleteTextView;
    List<City> found;
    ArrayAdapter<String> cityAdapter;
    ListView list;
    SimpleCursorAdapter ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        mDbHelper = new DbAdapter(this);

        //insertDefaultCities();

        currentCity = (TextView)findViewById(R.id.cur_city);
        currentCountry = (TextView)findViewById(R.id.cur_country);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imb.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        imb = (ImageButton) findViewById(R.id.imageButton);
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imb.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(Main.this, "update", 1000).show();

                Intent intent = new Intent(Main.this, UpdateService.class);
                try{
                    startService(intent);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        //-------
        // create local storage
        cities = new ArrayList<City>();
        current = new ArrayList<Current>();
        forecast = new ArrayList<Forecast>();
        //------


        //-------------------------------------------------
        // here begins the ViewPager Construction

        inflater = LayoutInflater.from(this);
        pages = new ArrayList<View>();




        pages.add(fillCities());
        pages.add(fillMain());
        //pages.add(fillForecast());



        pagerAdapter = new SamplePagerAdapter(pages);
        viewPager = (ViewPager) findViewById(R.id.myViewPager);
        viewPager.setAdapter(pagerAdapter);
        //viewPager.setCurrentItem(1);

        // the ViewPager is completed
        //-----------------------------------------------------

        setCurrent(0);
        upAllData();
        upAllUI();

        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(UpdateService.ACTION_UP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        start();
    }

//    private View fillFavourites()
//    {
//        View page;
//        page = inflater.inflate(R.layout.favorite, null);
//        lv1 = (ListView) page.findViewById(R.id.listViewCurrent);
//
//
//        lv1UP();
//
//
//
//        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Cursor cursor1 = (Cursor)ad1.getItem(i);
//                int id = cursor1.getInt(cursor1.getColumnIndex(KEY_ROWID));
//                cursor1.close();
//                setCurrent(id);
//                Toast.makeText(ViewPagerSampleActivity.this, "CURRENT!!!!!!", 3000).show();
//            }
//        });
//
//        final String[] COUNTRIES = new String[] {
//                "Belgium", "France", "Italy", "Germany", "Spain"
//        };
//        final AutoCompleteTextView textView = (AutoCompleteTextView)page.findViewById(R.id.editText);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//
//        textView.setAdapter(adapter);
//        return page;
//    }

    private View fillMain()
    {
        View page;
        page = inflater.inflate(R.layout.weth, null);


        tv2 = (TextView) page.findViewById(R.id.descr);
        tv3 = (TextView) page.findViewById(R.id.wind);
        tv1 = (TextView) page.findViewById(R.id.curtemp);
        tv4 = (TextView) page.findViewById(R.id.observed);
        image = (ImageView) page.findViewById(R.id.imageView);
        //Cursor cursor = null;
        try
        {
            tv1.setText(current.get(getCurrent()).tempC+ " " + KEY_GRAD);
            tv4.setText(current.get(getCurrent()).observed);
            tv3.setText(current.get(getCurrent()).wind);
            tv2.setText(current.get(getCurrent()).descr);
            image.setImageResource(current.get(getCurrent()).image);
        }
        catch(Exception e)
        {
            Log.w(TAG, "can't load main page!!!" + e.getMessage());
        }

        ListView lv = (ListView)page.findViewById(R.id.listView);
        ForecastAdapter customAdapter = new ForecastAdapter(this, R.layout.forecast_list_item, forecast);
        lv.setAdapter(customAdapter);

        return page;
    }

    private int getCurrent()
    {
        // todo test
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(KEY_SAVED, "-1");
            return (int)Integer.parseInt(savedText);
    }
    private void setCurrent(int id)
    {
        //todo    test
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(KEY_SAVED, new String(new Integer(id).toString()));
        ed.commit();
        // надо бы переставить метку
        updateCurrentLabel();
    }
//    private void clearCurrent()
//    {
//        sPref = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        ed.clear();
//        ed.commit();
//    }


    private void updateCurrentLabel()
    {
        //todo test

        try {
            currentCity.setText(cities.get(getCurrent()).name);
            currentCountry.setText("");  // todo reduce this by editing the layout file
        }
        catch(Exception e)
        {
            Log.w(TAG, "unable to update Label " + e.getMessage());
        }
    }

    public void start()
    {
        Intent intent = new Intent(Main.this, UpdateService.class);
        startService(intent);
    }

    public void upAllData()
    {
        // написал ночью. хрен его знает, но должно работать.
        // просто апдейтит из базы три листа, из тоторыз планириется потом показывать.
        Cursor cursor = null;
        try
        {
            mDbHelper.open();
            cursor = mDbHelper.fetchCity();
            if(cursor!= null && (cities.size() == 0 || cursor.getCount() > 0) )
                cities.clear();
            else
            {
                Log.w(TAG, "unable to update local storage. troubles with DB");
                Toast.makeText(Main.this, "unable", 1000);
                return;
            }


            while(cursor.moveToNext())
            {

                cities.add(new City(cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_LAT)),
                        cursor.getString(cursor.getColumnIndex(KEY_LONG))));
            }

            cursor = null;
            cursor = mDbHelper.fetchCurrent();
            if(cursor!= null && (current.size() == 0 || cursor.getCount() > 0) )
                current.clear();
            else
            {
                Log.w(TAG, "unable to update local storage. troubles with DB");
                Toast.makeText(Main.this, "unable", 1000);
                return;
            }

            while(cursor.moveToNext())
            {
                current.add(new Current(cursor.getString(cursor.getColumnIndex(KEY_TEMPC)),
                        cursor.getString(cursor.getColumnIndex(KEY_WSK)),
                        cursor.getString(cursor.getColumnIndex(KEY_WTHDESC)),
                        cursor.getString(cursor.getColumnIndex(KEY_OBSTIME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_WTHCODE))));  // todo check if Int or String ot int
            }

            cursor = null;
            String s = cities.get(getCurrent()).name;
            cursor = mDbHelper.fetchForecast(s);
            if(cursor!= null && (forecast.size() == 0 || cursor.getCount() > 0) )
                forecast.clear();
            else
            {
                Log.w(TAG, "unable to update local storage. troubles with DB");
                Toast.makeText(Main.this, "unable", 1000);
                return;
            }

            while(cursor.moveToNext())
            {
                forecast.add(new Forecast(cursor.getString(cursor.getColumnIndex(KEY_TEMPMINC)),
                        cursor.getString(cursor.getColumnIndex(KEY_TEMPMAXC)),
                        cursor.getString(cursor.getColumnIndex(KEY_WSK)),
                        cursor.getString(cursor.getColumnIndex(KEY_WTHDESC)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_WTHCODE))));

            }
            cursor.close();

            mDbHelper.close();
        }
        catch(Exception e)
        {
            Log.w(TAG, "whole crash in thre bit try-catch block  " + e.getMessage());
            if(cursor!=null)cursor.close();
            mDbHelper.close();
        }
    }

    public void upAllUI()
    {
        // не написал. здесь просто показать собержимое трех листов на экранах.
        // на сегодня легко, а вот для городов(может их не нао вообше) и для прогноза - надо катомный адаптер писать!!

        // временно:
        pages.clear();
        pages.add(fillMain());
        pages.add(fillCities());
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();

        updateCurrentLabel();
    }

    public void stopProgressBar()
    {
        progressBar.setVisibility(View.INVISIBLE);
        imb.setVisibility(View.VISIBLE);
    }

    private void insertDefaultCities()
    {
        mDbHelper.open();
        mDbHelper.dropCities();
        HashMap<String, String> h = new HashMap<String, String>();
        h.put(KEY_NAME, "London, UK");
        h.put(KEY_LAT, "51.517");
        h.put(KEY_LONG, "-0.106");
        mDbHelper.createCity(h);

        h.put(KEY_NAME, "Moscow, Russia");
        h.put(KEY_LAT, "55.752");
        h.put(KEY_LONG, "37.616");
        mDbHelper.createCity(h);

        h.put(KEY_NAME, "Saint Petersburg, Russia");
        h.put(KEY_LAT, "59.894");
        h.put(KEY_LONG, "30.264");
        mDbHelper.createCity(h);
        mDbHelper.close();
    }


    private View fillCities()
    {
        View page;
//        View inflate = inflater.inflate(R.layout.cities, null);
        page = inflater.inflate(R.layout.cities, null);


        list = (ListView) page.findViewById(R.id.listViewCurrent);


        List<String> data = new ArrayList<String>();
        for(int i=0; i<cities.size(); ++i)
        {
            data.add(cities.get(i).name);
        }

        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.city_item, R.id.city_item_text, data);
        list.setAdapter(ad);
//        mDbHelper.open();
//        //try {
//        Cursor cursor =  mDbHelper.fetchCurrent();
//        startManagingCursor(cursor);
//        ad = new SimpleCursorAdapter(this, R.layout.city_item, cursor, from, to);
//        list.setAdapter(ad);
//
//        cursor.moveToFirst();
//        String s = cursor.getString(cursor.getColumnIndex(KEY_TEMPC));
//
//        mDbHelper.close();
//
//
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                setCurrent(i);
                Toast.makeText(Main.this, "CURRENT!!!!!!    -   " + new Integer(i).toString(), 3000).show();
            }
        });

        registerForContextMenu(list);



        found = new ArrayList<City>();
        autoCompleteTextView = (AutoCompleteTextView)page.findViewById(R.id.editText);
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.ACTION_DOWN == keyEvent.getAction() && i == KeyEvent.KEYCODE_ENTER) {
                    String s = autoCompleteTextView.getText().toString();
                    CityAsyncTask task = new CityAsyncTask();
                    task.execute(new String[]{s});
                }
                return true;
            }
        });
//        TODO manage the autocomplete textview onKey, queries


        return page;
    }

    private class CityAsyncTask extends AsyncTask<String, Void, Void > {
        @Override
        protected Void doInBackground(String... parameter) {
            CityGeocoder cgc = new CityGeocoder();
            found = cgc.getCities(parameter[0]);
//            String[] a = new String[found.size()];
//            for(int i=0; i<found.size(); ++i)
//            {
//                a[i] = found.get(i).name;
//            }

//            return a;
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
//            cityAdapter = new ArrayAdapter<String>(Main.this, android.R.layout.simple_dropdown_item_1line, res);
//            autoCompleteTextView.setAdapter(cityAdapter);
              if(found.size() > 0)
              {
                  cities.add(found.get(0));
                  commitCity(found.get(0));
                  upAllUI();
                  start();
              }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                if(cities.size()==1)
                {
                    Toast.makeText(this, "can't delete the last city!", 1000).show();
                    return true;
                }
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String name = cities.get((int)info.id).name;
                if(getCurrent() == (int)info.id)
                {
                    setCurrent(0);
                }
                else if(getCurrent()!= 0 && getCurrent()>=info.id)
                {
                    setCurrent(getCurrent()-1);
                }

                cities.remove(info.id);
                removeFromBase(name);
                start();
                upAllUI();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void commitCity(City c)
    {
        mDbHelper.open();
        HashMap<String, String> h = new HashMap<String, String>();
        h.put(KEY_NAME, c.name);
        h.put(KEY_LAT, c.latitude);
        h.put(KEY_LONG, c.longitude);
        mDbHelper.createCity(h);
        mDbHelper.close();
    }
    private void removeFromBase(String name)
    {
        mDbHelper.open();
        int id = (int)mDbHelper.getCityId(name);
        mDbHelper.deleteCity((long)id);
        mDbHelper.deleteCurrent((long)id);
        mDbHelper.deleteForecast((long)id);
        mDbHelper.close();
    }

}





