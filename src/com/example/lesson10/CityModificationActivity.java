package com.example.lesson10;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.lesson10.databases.CitiesDatabase;
import com.example.lesson10.databases.WeatherDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: Genyaz
 * Date: 28.11.13
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public class CityModificationActivity extends Activity {

    public int id;
    public String cityName;
    public EditText cityText;

    public void onProceed(View view) {
        cityName = cityText.getText().toString();
        if (cityName.isEmpty()) {
            onRemove(view);
        } else {
            if (id == -1) {
                CitiesDatabase citiesDatabase = new CitiesDatabase(this);
                SQLiteDatabase wdb = citiesDatabase.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(CitiesDatabase.CITY_NAME, cityName);
                wdb.insert(CitiesDatabase.DATABASE_NAME, null, cv);
                wdb.close();
                citiesDatabase.close();
            } else {
                CitiesDatabase citiesDatabase = new CitiesDatabase(this);
                SQLiteDatabase wdb = citiesDatabase.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(CitiesDatabase.CITY_NAME, cityName);
                wdb.update(CitiesDatabase.DATABASE_NAME, cv, CitiesDatabase._ID + " = " + id, null);
                wdb.close();
                citiesDatabase.close();
            }
        }
        this.finish();
    }

    public void onRemove(View view) {
        if (id != -1) {
            CitiesDatabase citiesDatabase = new CitiesDatabase(this);
            SQLiteDatabase wdb = citiesDatabase.getWritableDatabase();
            wdb.delete(CitiesDatabase.DATABASE_NAME, CitiesDatabase._ID + " = " + id, null);
            wdb.close();
            citiesDatabase.close();
            WeatherDatabase weatherDatabase = new WeatherDatabase(this);
            wdb = weatherDatabase.getWritableDatabase();
            wdb.delete(WeatherDatabase.DATABASE_NAME, WeatherDatabase.CITY_ID + " = " + id, null);
            wdb.close();
            weatherDatabase.close();
        }
        this.finish();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_modification);
        Intent intent = getIntent();
        id = intent.getIntExtra(getString(R.string.cityid), -1);
        cityName = intent.getStringExtra(getString(R.string.cityname));
        cityText = (EditText) findViewById(R.id.cityNameText);
        cityText.setText(cityName);
    }
}