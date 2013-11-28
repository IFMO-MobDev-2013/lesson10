package com.example.MaWeather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class Dispatcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int cityId = 0;
        String cityName = "";

        Class<?> activityClass;
        SharedPreferences prefs = null;
        try {
            prefs = getSharedPreferences("X", MODE_PRIVATE);
            activityClass = Class.forName(prefs.getString("lastActivity", StartActivity.class.getName()));
            cityId = prefs.getInt("cityId", 0);
            cityName = prefs.getString("cityName", "");

        } catch(ClassNotFoundException ex) {
            activityClass = StartActivity.class;
        }

        startActivity(new Intent(this, activityClass).putExtra("task", "restart").putExtra("cityId", cityId).putExtra("city", cityName));
    }
}
