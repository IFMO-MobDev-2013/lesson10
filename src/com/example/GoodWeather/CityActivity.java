package com.example.GoodWeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class CityActivity extends Activity {
    private String cityUrl1, cityUrl2;
    private final String RESPONSE = "cityResponse";
    private final String URL = "url";
    private final String EN = "en", RU = "ru";
    private final String LONGTITUDE = "longtitude";
    private final String LATITUDE = "latitude";
    private final String COUNTRY = "country";

    String word,ruWord;
    DBAdapter db;
    ListView listView;
    TextView text;
    String[] cities, countries;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);
        listView = (ListView) findViewById(R.id.listView);
        db = new DBAdapter(this);
        text = (TextView) findViewById(R.id.textView);
        cityUrl1 = getResources().getString(R.string.curl1);
        cityUrl2 = getResources().getString(R.string.curl2);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.status));
        makeList();
        CityReceiver city = new CityReceiver();
        IntentFilter intentFilter = new IntentFilter(RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(city, intentFilter);
    }


    private void makeList() {
        cities = db.getAllCities();
        countries = db.getAllCountries();
        if (cities == null)  {
            cities = new String[0];
            countries = new String[0];
        }
        for (int i = 0; i < cities.length; i++) {
            cities[i] = cities[i] + ", " + countries[i];
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listitem, R.id.textView, cities);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View view,
                                    int position, long id) {
                Intent intent = new Intent(CityActivity.this, WeatherActivity.class);
                intent.putExtra(DBAdapter.CITY, cities[position]);
                startActivity(intent);
                CityActivity.this.finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CityActivity.this);
                builder.setTitle(getResources().getString(R.string.delete));
                final int pos = position;
                builder.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteCity(cities[pos]);
                        makeList();
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.nope), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return false;
            }
        });
        listView.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            programAddCity();
        }
        return true;
    }

    public void programAddCity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        final EditText edit = new EditText(this);
        builder.setView(edit);
        builder.setTitle(getResources().getString(R.string.title));
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                word = edit.getText().toString();
                int i = word.length() - 1;
                while (i > 0 && word.charAt(i) == ' ') i--;
                if (i != word.length() - 1) {
                    word = word.substring(0, i + 1);
                }
                if (!word.equals("")) {
                    TranslateCity trans = new TranslateCity(word, EN);
                    trans.async.execute();
                    try {
                        ruWord = new TranslateCity(word, RU).async.execute().get();
                        word = trans.async.get();
                    } catch (Exception e) {
                    }
                    if (!db.check(ruWord)) {
                        String ww = word;
                        for (int j = 0; j < ww.length(); j++) {
                            if (ww.charAt(j) == '-') {
                                ww = ww.substring(0, j) + "_" + ww.substring(j + 1, ww.length());
                            }

                        }
                        Intent intent = new Intent(CityActivity.this, CityService.class);
                        intent.putExtra(URL, cityUrl1 + ww + cityUrl2);
                        startService(intent);
                    }
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    private void add(String latitude, String longtitude, String country) {
        String coun = country;
        try {
            coun = new TranslateCity(country, RU).async.execute().get();
        } catch(Exception e) {}
        db.insert(ruWord, longtitude, latitude, coun);
        makeList();
    }

    public class CityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String latitude = intent.getStringArrayExtra(LATITUDE)[0];
            String longtitude = intent.getStringArrayExtra(LONGTITUDE)[0];
            String country = intent.getStringArrayExtra(COUNTRY)[0];
            add(latitude, longtitude, country);
        }
    }
}
