package com.example.lesson10;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;

public class Program extends Activity implements IEventHadler {
    public CitiesAdapter adapter;
    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities);

        Database.init(this);

        Vector<City> e = new Vector<City>();

        adapter = new CitiesAdapter(this, e);
        adapter.addEventListener(this);

        ListView list_view = (ListView) findViewById(R.id.cities_citiesList);
        list_view.setAdapter(adapter);

        new Reloader().start(this);
    }

    public void syncAll(View v)
    {
        Cursor sth = Database.gi().query("select * from cities where chosen = 1");
        count = sth.getCount();
        while (sth.moveToNext())
        {
            City city = City.findById(Integer.parseInt(sth.getString(0)));
            city.addEventListener(this);
            city.startUpdate();
        }
         Toast.makeText(this, "Start updating...", 3000).show();
    }

    public void gotoEditActivity(View v)
    {
        Intent intent = new Intent(this, ChooseCityActivity.class);
        startActivity(intent);
    }

    @Override
    public void handleEvent(Event e)
    {
         if (e.type == Event.COMPLETE)
         {
             e.target.addEventListener(this);
             count--;
             final Activity fix = this;
             if (count <= 0)
             {
                 count = 0;
                 runOnUiThread(new Runnable()
             {
                 @Override
                 public void run() {
                     Toast.makeText(fix, "Data is updated", 3000).show();
                     adapter.notifyDataSetChanged();
                 }
             });
             }
         }
         else if (e.type == Event.ERROR)
         {
             e.target.addEventListener(this);
             count--;
         }
         else if (e.type == TouchEvent.CLICK)
         {
             Intent intent = new Intent(this, CityActivity.class);
             intent.putExtra("ID_CITY", (Long)e.data.get("ID_CITY"));
             startActivity(intent);
         }
         else if (e.type == TouchEvent.LONG_CLICK)
         {
             Intent intent = new Intent(this, CityEditActivity.class);
             intent.putExtra("ID_CITY", (Long)e.data.get("ID_CITY"));
             startActivity(intent);
         }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.cities.clear();

        Cursor sth = Database.gi().query("select * from cities where chosen = 1");
        while (sth.moveToNext())
        {
            City city = City.findById(Integer.parseInt(sth.getString(0)));
            city.name = sth.getString(1);
            city.chosen = Integer.parseInt(sth.getString(2)) != 0;
            city.temperature = Integer.parseInt(sth.getString(3));
            //Console.print(city.getId()+ " " + city.name + " " + city.chosen);
            adapter.cities.add(city);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
