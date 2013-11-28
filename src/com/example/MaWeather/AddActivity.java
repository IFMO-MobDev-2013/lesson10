package com.example.MaWeather;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 28.11.13
 * Time: 19:55
 */
public class AddActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WeatherDataBase weatherDataBase = new WeatherDataBase(AddActivity.this);

                String cityName = editText.getText().toString();

                IntentFilter intentFilter = new IntentFilter("com.example.MaWeather.RESPONSE");
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                //registerReceiver(weatherBroadcastReceiver, intentFilter);

                Intent intentWeather = new Intent(AddActivity.this, WeatherGetter.class);
                startService(intentWeather.putExtra("cityId", weatherDataBase.getCityId(cityName)).putExtra("cityName", cityName).putExtra("task", "load"));

                Intent intent = new Intent(AddActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }
}

