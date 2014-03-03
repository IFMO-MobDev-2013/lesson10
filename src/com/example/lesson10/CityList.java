package com.example.lesson10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 3:47
 * To change this template use File | Settings | File Templates.
 */
public class CityList extends Activity {
    private static ArrayList<String> city = new ArrayList<String>();
    private static ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list);

        Button find = (Button)findViewById(R.id.find);
        ListView listView = (ListView) findViewById(R.id.listcity);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.label,city);
        listView.setAdapter(arrayAdapter);
        refreshList();

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FindCity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
                MyActivity.cityName.setText(city.get(index));
                MyActivity.instance.loadWeather();
                close();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = city.get(i);
                DbCity dbCity = new DbCity(MyActivity.instance);
                SQLiteDatabase db = dbCity.getWritableDatabase();
                dbCity.onCreate(db);
                db.execSQL("DELETE FROM " + DbCity.TABLE_NAME + " WHERE " + DbCity.NAME + " = '" + name + "';", null);
                db.close();
                dbCity.close();
                refreshList();
                return true;
            }
        });
    }

    private void close(){
        super.onBackPressed();
    }

    public static void refreshList(){
        city.clear();
        DbCity dbCity = new DbCity(MyActivity.instance);
        SQLiteDatabase db = dbCity.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbCity.TABLE_NAME + ";",null);
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++){
            city.add(cursor.getString(cursor.getColumnIndex(DbCity.NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        dbCity.close();
        arrayAdapter.notifyDataSetChanged();
    }
}
