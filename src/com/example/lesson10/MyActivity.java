package com.example.lesson10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.lesson10.City.AddCityActivity;
import com.example.lesson10.databases.WeatherDB;

public class MyActivity extends Activity {

    private Button button;
    private ListView listView;
    private WeatherDB weatherDB;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);

        weatherDB = new WeatherDB(this);
        weatherDB.open();

        cursor = weatherDB.getAllDataCursor();
        startManagingCursor(cursor);

        String [] from = new String[] {
            WeatherDB.COLUMN_CITY
        };

        int[] to = new int[]{
            R.id.city
        };
        adapter = new SimpleCursorAdapter(this, R.layout.adapter, cursor, from, to);
        listView.setAdapter(adapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddCityActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentCity = ((TextView)view.findViewById(R.id.city)).getText().toString();
                Intent intent = new Intent(view.getContext(), WeatherActivity.class);
                intent.putExtra("city", currentCity);
                startActivity(intent);
            }
        });
    }
}
