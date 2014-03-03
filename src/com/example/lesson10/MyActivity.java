package com.example.lesson10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyActivity extends Activity {
    public static String refreshMsg;
    public static TextView cityName;
    public static MyAdapter adapter;
    public static ArrayList<ItemWeather> itemList = new ArrayList<ItemWeather>();
    public static MyActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        instance = this;
        refreshMsg = getString(R.string.refreshmsg);

        cityName = (TextView) findViewById(R.id.cityName);
        Button list = (Button) findViewById(R.id.list);
        Button load = (Button) findViewById(R.id.load);
        ListView weather = (ListView) findViewById(R.id.listweather);
        adapter = new MyAdapter(this,itemList);
        weather.setAdapter(adapter);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CityList.class);
                startActivity(intent);
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeather();
            }
        });
    }

    public void loadWeather(){
        if (!"".equals(cityName.getText())){
            String name = (String) cityName.getText();
            itemList.clear();
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(this,MyIntentServ.class);

            DbCity dbCity = new DbCity(MyActivity.instance);
            SQLiteDatabase db = dbCity.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DbCity.TABLE_NAME + " WHERE " + DbCity.NAME + " = '" + name + "';",null);
            cursor.moveToFirst();
            City city = new City();
            city.name = cursor.getString(cursor.getColumnIndex(DbCity.NAME));
            city.latitude = cursor.getString(cursor.getColumnIndex(DbCity.LAT));
            city.longitude = cursor.getString(cursor.getColumnIndex(DbCity.LON));
            cursor.close();
            db.close();
            dbCity.close();

            startService(intent.putExtra("task","load").putExtra("lat", city.latitude).putExtra("lon",city.longitude).putExtra("date",0));
            startService(intent.putExtra("task","load").putExtra("lat", city.latitude).putExtra("lon",city.longitude).putExtra("date",1));
            startService(intent.putExtra("task","load").putExtra("lat", city.latitude).putExtra("lon",city.longitude).putExtra("date",2));
        }
    }
}
