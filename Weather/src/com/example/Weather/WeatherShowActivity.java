package com.example.Weather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WeatherShowActivity extends Activity {
    ListView listView;
    ArrayList<Weather> arrayList;
    ArrayAdapter<Weather> arrayAdapter;
    String name;
    int id;

    void fillArray() {
        WeatherDataBaseHelper weatherDataBaseHelper = new WeatherDataBaseHelper(getApplicationContext(), name);
        SQLiteDatabase sqLiteDatabase = weatherDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(weatherDataBaseHelper.dataBaseName(), null, null, null, null, null, null);
        int dateColumn = cursor.getColumnIndex(WeatherDataBaseHelper.DATE);
        int lowTempColumn = cursor.getColumnIndex(WeatherDataBaseHelper.LOW_TEMPERATURE);
        int maxTempColumn = cursor.getColumnIndex(WeatherDataBaseHelper.HIGH_TEMPERATURE);
        int windSpeedColumn = cursor.getColumnIndex(WeatherDataBaseHelper.WIND_SPEED);
        int windDirectColumn = cursor.getColumnIndex(WeatherDataBaseHelper.WIND_DIRECTION);
        int weatherCodeColumn = cursor.getColumnIndex(WeatherDataBaseHelper.WEATHER_CODE);
        int weatherDescColumn = cursor.getColumnIndex(WeatherDataBaseHelper.WEATHER_DESCRIPTION);

        arrayList.clear();
        while (cursor.moveToNext())
            arrayList.add(new Weather(cursor.getString(dateColumn), cursor.getInt(lowTempColumn), cursor.getInt(maxTempColumn), cursor.getInt(windSpeedColumn), cursor.getString(windDirectColumn), cursor.getInt(weatherCodeColumn), cursor.getString(weatherDescColumn)));

        cursor.close();
        sqLiteDatabase.close();
        weatherDataBaseHelper.close();
        arrayAdapter.notifyDataSetChanged();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_show_activity);
        listView = (ListView) findViewById(R.id.lvWeather);

        arrayList = new ArrayList<Weather>();
        arrayAdapter = new WeatherArrayAdapter(getApplicationContext(), arrayList);
        listView.setAdapter(arrayAdapter);
        Intent intent = getIntent();
        name = intent.getStringExtra(MyActivity.CITY_NAME);
        id = intent.getIntExtra(MyActivity.ID, 0);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateService.class);
                intent.putExtra(MyActivity.FLAG, false);
                intent.putExtra(MyActivity.ID, id);
                startService(intent);
            }
        });
        fillArray();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fillArray();
            Toast.makeText(context, (intent.getBooleanExtra(UpdateService.RESULT, false) ? "Successful Update" : "Bad news... Update wasn't successful"), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        registerReceiver(mMessageReceiver, new IntentFilter(UpdateService.ACTION));
        fillArray();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        unregisterReceiver(mMessageReceiver);
    }
}