package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;


public class CitiesActivity extends Activity implements AppResultsReceiver.Receiver {

    private List<City> cities;
    private ListView list;
    private EditText editText;

    private static String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?";
    private static String KEY = "key=ks7tbdcd34tzgwbwhgahhrj9";
    private static String FORMAT = "format=json";

    private final String URL_TAG = "url";
    private final String RECEIVER = "receiver";

    AppResultsReceiver mReceiver;
    DataBase db;
    MyArrayAdapter adapter;
    private final String PREFS_NAME = "MyPrefsFile";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        list = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.EditText01);

        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);

        db = new DataBase(getApplicationContext());
        cities = db.getAllCities();
        adapter = new MyArrayAdapter(this, R.layout.row, cities);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(CitiesActivity.this, MyActivity.class);
                City c = cities.get(position);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("city_id", c.getId());
                editor.commit();
                startActivity(intent);
                finish();
            }
        });
    }

    public void addCity(View v) {
        Intent downloadService = new Intent(this, DownloadService.class);

        String query = editText.getText().toString();
        editText.setText("");
        String url = URL + "q=" + query.replace(' ', '+') + "&" +
                FORMAT + "&" +
                "num_of_days=3" + "&" +
                KEY;

        downloadService.putExtra(RECEIVER, mReceiver);
        downloadService.putExtra(URL_TAG, url);
        boolean updating = false;
        downloadService.putExtra("status", updating);
        startService(downloadService);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        boolean updating = resultData.getBoolean("status", false);
        int city_id = resultData.getInt("city_id");
        if (!updating) {
        City c = db.getCity(city_id);
        cities.add(c);
        adapter.notifyDataSetChanged();
        }
    }
}