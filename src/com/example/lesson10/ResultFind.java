package com.example.lesson10;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 03.03.14
 * Time: 4:44
 * To change this template use File | Settings | File Templates.
 */
public class ResultFind extends Activity {
    public static ArrayList<City> cities = new ArrayList<City>();
    public static ArrayList<String> city = new ArrayList<String>();
    public static ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_find_city);

        ListView listView = (ListView)findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.label,city);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String name = city.get(index);
                City curCity = new City();
                for (City city1 : cities){
                    if (city1.name.equals(name))
                        curCity = city1;
                }
                DbCity dbCity = new DbCity(MyActivity.instance);
                SQLiteDatabase db = dbCity.getWritableDatabase();
                dbCity.onCreate(db);
                ContentValues cv = new ContentValues();
                cv.put(DbCity.NAME, curCity.name);
                cv.put(DbCity.LAT, curCity.latitude);
                cv.put(DbCity.LON, curCity.longitude);
                db.insert(DbCity.TABLE_NAME,null,cv);
                db.close();
                dbCity.close();
                CityList.refreshList();
                close();
            }
        });
    }

    private void close(){
        super.onBackPressed();
    }
}
