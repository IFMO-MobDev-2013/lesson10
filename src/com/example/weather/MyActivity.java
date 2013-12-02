package com.example.weather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class MyActivity extends Activity {
    ArrayList<TownShort> Towns = new ArrayList<TownShort>();
    public long old_time = -60000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Towns = new ArrayList<TownShort>();
        Button button_add = (Button) findViewById(R.id.add_town);
        final EditText editText_newTown = (EditText) findViewById(R.id.new_town);
        editText_newTown.setText("Санкт-Петербург");
        final DBHelper dbHelper = new DBHelper(this);
        ListView listView = (ListView) findViewById(R.id.listView);
        Button button_refresh = (Button) findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - old_time >= 10000) {
                    Intent intent = new Intent(getApplicationContext(), MyIntent.class);
                    intent.putExtra("name", editText_newTown.getText().toString());
                    intent.putExtra("all", "q");
                    startService(intent);
                    show_town();
                    old_time = System.currentTimeMillis();

                }
            }
        });

        show_town();
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                String name="";
                String up=editText_newTown.getText().toString().substring(0, 1);
                up=up.toUpperCase();
                name=up+editText_newTown.getText().toString().substring(1);
                Log.d("qqq",name+"   "+up);
                cv.put(DBHelper.TOWN_NAME, name);
                SQLiteDatabase wdb = dbHelper.getWritableDatabase();
                wdb.insert(DBHelper.DATABASE_NAME, null, cv);
                Intent intent = new Intent(getApplicationContext(), MyIntent.class);
                intent.putExtra("name", name);
                intent.putExtra("all", "q");
                startService(intent);
                show_town();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("name", Towns.get(i).name);
                intent.setClass(getApplicationContext(), Weather.class);

                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("name", Towns.get(i).name);
                intent.setClass(getApplicationContext(), Change.class);

                startActivity(intent);
                show_town();
                return true;
            }
        });

        Intent intent = new Intent(getApplicationContext(), MyIntent.class);
        intent.putExtra("all", "true");

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 10000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }


    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction("show_town");
        registerReceiver(broadcastReceiver,intentFilter);
        show_town();

    }
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            show_town();
        }
    };
    public void show_town() {
        Towns.clear();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase rdb = dbHelper.getReadableDatabase();
        Cursor cursor = rdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
        int name_column = cursor.getColumnIndex(DBHelper.TOWN_NAME);
        int day_column = cursor.getColumnIndex(DBHelper.DAY);
        int temperature_column = cursor.getColumnIndex(DBHelper.TEMPERATURE_FROM);
        int image_column = cursor.getColumnIndex(DBHelper.IMAGE);
        while (cursor.moveToNext()) {
            if (cursor.getString(name_column) != null && cursor.getString(day_column) != null && (cursor.getString(day_column).equals("full"))) {
                    Towns.add(new TownShort(cursor.getString(name_column), cursor.getInt(temperature_column), cursor.getString(image_column)));

            }
        }

        Log.d("size3", new Integer(Towns.size()).toString());
        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<TownShort> arrayAdapter = new MyAdapter(getApplicationContext(), Towns);
        listView.setAdapter(arrayAdapter);
    }
}
