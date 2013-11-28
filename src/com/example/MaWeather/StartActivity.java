package com.example.MaWeather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StartActivity extends Activity {

    private Button addButton;
    private WeatherBroadcastReceiver weatherBroadcastReceiver;
    private ForecastBroadcastReceiver forecastBroadcastReceiver;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        Toast toast = Toast.makeText(getApplicationContext(), "Загружаем необходимые данные, подождите несколько секунд", Toast.LENGTH_SHORT);
        toast.show();

        listView = (ListView) findViewById(R.id.start_listview);

        IntentFilter intentFilter = new IntentFilter("com.example.MaWeather.RESPONSE_CITY");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        weatherBroadcastReceiver = new WeatherBroadcastReceiver();
        registerReceiver(weatherBroadcastReceiver, intentFilter);

        Intent intentCity = new Intent(StartActivity.this, CityGetter.class);
        startService(intentCity);

        Intent intent = new Intent(this, Refresher.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30 * 60 * 1000, 30 * 60 * 1000, pendingIntent);

        addButton = (Button) findViewById(R.id.button_add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

                WeatherDataBase weatherDataBase = new WeatherDataBase(StartActivity.this);

                Map map = (Map) parent.getItemAtPosition(position);

                Intent intent = new Intent(StartActivity.this, WeatherActivity.class);
                intent.putExtra("city", (String) map.get("city"));
                intent.putExtra("cityId", weatherDataBase.getCityId((String) map.get("city")));
                startActivity(intent.putExtra("task", "start"));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView <?> parent, View view, int position, long id) {

                Toast toast = Toast.makeText(getApplicationContext(), "Удалено", Toast.LENGTH_SHORT);
                toast.show();

                WeatherDataBase weatherDataBase = new WeatherDataBase(StartActivity.this);

                Map map = (Map) parent.getItemAtPosition(position);

                weatherDataBase.delete((String)map.get("city"));

                SimpleAdapter adapter = new SimpleAdapter(StartActivity.this, weatherDataBase.getListOfCites(), R.layout.start_listview,
                        new String[]{"city"},
                        new int[] {R.id.city});

                listView.setAdapter(adapter);

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(weatherBroadcastReceiver);
        unregisterReceiver(forecastBroadcastReceiver);
    }

    public class WeatherBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            final int[] cityId = {27612, 26063, 26094};
            final String[] city = {"Москва", "Санкт-Петербург", "Тихвин"};

            Toast toast = Toast.makeText(getApplicationContext(), "Данные загружены!", Toast.LENGTH_SHORT);
            toast.show();

            WeatherDataBase weatherDataBase = new WeatherDataBase(StartActivity.this);

            /*SimpleAdapter adapter = new SimpleAdapter(StartActivity.this, weatherDataBase.getListOfCites(), R.layout.start_listview,
                    new String[]{"city"},
                    new int[] {R.id.city});

            listView.setAdapter(adapter); */

            for (int i = 0; i < cityId.length; i++) {
                IntentFilter intentFilter = new IntentFilter("com.example.MaWeather.RESPONSE_FORECAST");
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                forecastBroadcastReceiver = new ForecastBroadcastReceiver();
                registerReceiver(forecastBroadcastReceiver, intentFilter);

                Intent intentWeather = new Intent(StartActivity.this, WeatherGetter.class);
                startService(intentWeather.putExtra("cityId", weatherDataBase.getCityId(city[i])).putExtra("cityName", city[i]).putExtra("task", "load"));
            }
        }
    }

    public class ForecastBroadcastReceiver extends BroadcastReceiver {

        WeatherDataBase weatherDataBase = new WeatherDataBase(StartActivity.this);

        @Override
        public void onReceive(Context context, Intent intent) {

            SimpleAdapter adapter = new SimpleAdapter(StartActivity.this, weatherDataBase.getListOfCites(), R.layout.start_listview,
                    new String[]{"city"},
                    new int[] {R.id.city});

            listView.setAdapter(adapter);


        }
    }
}
