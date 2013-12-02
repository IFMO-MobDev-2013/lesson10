package com.example.weather;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Weather extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        String town_name = getIntent().getStringExtra("name");
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase rdb = dbHelper.getReadableDatabase();
        Cursor cursor = rdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
        int name_column = cursor.getColumnIndex(DBHelper.TOWN_NAME);
        int day_column = cursor.getColumnIndex(DBHelper.DAY);
        int part_column = cursor.getColumnIndex(DBHelper.PART);


        ArrayList<WeatherAll> arrayList = new ArrayList<WeatherAll>();

        while (cursor.moveToNext()) {
            if (cursor.getString(name_column) != null && cursor.getString(day_column) != null && cursor.getString(name_column).equals(town_name) && !cursor.getString(day_column).equals("full") &&
                    cursor.getString(part_column).equals("day_short")) {
                arrayList.add(new WeatherAll(cursor));
            }
        }

        ListView listView_day = (ListView) findViewById(R.id.listView_day);
        ArrayAdapter<WeatherAll> arrayAdapter = new MyAdapterDay(getApplicationContext(), arrayList);
        listView_day.setAdapter(arrayAdapter);

    }
}
