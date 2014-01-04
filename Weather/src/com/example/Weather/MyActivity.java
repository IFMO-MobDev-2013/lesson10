package com.example.Weather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Random;

public class MyActivity extends Activity {

    public static final String API_KEY = "c52mv9u4u49cvxc8se4nqk2r";
    public static final String URL_CITY = "http://api.worldweatheronline.com/free/v1/search.ashx";
    public static final String URL_WEATHER = "http://api.worldweatheronline.com/free/v1/weather.ashx";
    public static ListView listView;
    public static final String FLAG = "flag";
    public static final String ID = "id";
    public static final String CITY_NAME = "city_name";

    Button btnAdd, btnUpdate;
    ArrayList<City> arrayList;
    ArrayAdapter<City> arrayAdapter;

    void readArray() {
        CityDataBaseHelper cityDataBaseHelper = new CityDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = cityDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(CityDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int nameColumn = cursor.getColumnIndex(CityDataBaseHelper.NAME);
        int idColumn = cursor.getColumnIndex(CityDataBaseHelper._ID);
        arrayList.clear();
        while (cursor.moveToNext()) {
            arrayList.add(new City(cursor.getString(nameColumn), cursor.getInt(idColumn)));
        }
        cursor.close();
        sqLiteDatabase.close();
        cityDataBaseHelper.close();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        intent.putExtra(FLAG, true);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), new Random().nextInt(1000000000), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 15000, AlarmManager.INTERVAL_HOUR, pendingIntent);


        arrayList = new ArrayList<City>();
        arrayAdapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.adapter_view, arrayList);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CityDataBaseHelper cityDataBaseHelper = new CityDataBaseHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = cityDataBaseHelper.getWritableDatabase();
                sqLiteDatabase.delete(CityDataBaseHelper.DATABASE_NAME, CityDataBaseHelper._ID + "=" + arrayList.get(position).id, null);
                sqLiteDatabase.close();
                cityDataBaseHelper.close();
                readArray();
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), WeatherShowActivity.class);
                intent.putExtra(CITY_NAME, arrayList.get(position).name);
                intent.putExtra(ID, position);
                startActivity(intent);
            }
        });
        listView.setAdapter(arrayAdapter);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AddCityActivity.class);
                startActivity(intent);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateService.class);
                intent.putExtra(FLAG, true);
                startService(intent);
            }
        });
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readArray();
            Toast.makeText(context, (intent.getBooleanExtra(UpdateService.RESULT, false) ? "Successful Update" : "Bad news... Update wasn't successful"), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        registerReceiver(mMessageReceiver, new IntentFilter(UpdateService.ACTION));
        readArray();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        unregisterReceiver(mMessageReceiver);
    }
    @Override
    protected void onStart() {
        super.onStart();
        readArray();
    }
}
