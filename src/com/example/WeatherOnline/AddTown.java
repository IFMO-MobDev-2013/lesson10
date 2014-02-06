package com.example.WeatherOnline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 03.02.14
 * Time: 21:43
 * To change this template use File | Settings | File Templates.
 */
public class AddTown extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtown);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, "Сохранить");
        menu.add(0, 2, 1, "Назад");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == 1){
            WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());
            String newCity = ((EditText) findViewById(R.id.town)).getText().toString();
            newCity = newCity.trim().replaceAll(" ", "");
            weatherDataBase.addCity(newCity, null, null, null, null, null);
            Intent intent = new Intent(AddTown.this, CityChoose.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(AddTown.this, CityChoose.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.setGroupVisible(0, true);
        return super.onPrepareOptionsMenu(menu);
    }
}
