package com.example.GoodWeather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class WeatherActivity extends Activity {
    private final String RESPONSE = "weatherResponse";
    private final String URL = "url";
    private final String WEATHER = "weather";
    private final String MAXC = "maxc";
    private final String MINC = "minc";
    private final String WINDSPEED = "windspeed";
    private final String HUMIDITY = "humidity";
    private final String DATE = "date";
    private final String CODE = "code";
    private final String RU = "ru";
    TextView nowTemp, nowSpeed, nowHum, temp, dat, time, nowWeather;
    ImageView imageNow, image;
    private String w1, w2;
    int[] temps, dats, images;
    private final int dayCount = 5;
    DBAdapter db;
    String city;
    DBCodes dbc;
    String[] weather, minC, maxC, windspeed, humidity, date, code;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        db = new DBAdapter(this);
        time = (TextView) findViewById(R.id.time);
        dbc = new DBCodes(this);
        w1 = getResources().getString(R.string.wurl1);
        w2 = getResources().getString(R.string.wurl2);
        city = getIntent().getStringExtra(DBAdapter.CITY);
        register();
        getWeather();
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.status));
        TextView c = (TextView) findViewById(R.id.city);
        c.setText(city);
    }

    private void setWeather() {
        nowTemp = (TextView) findViewById(R.id.nowtemp);
        nowHum = (TextView) findViewById(R.id.nowhum);
        nowSpeed = (TextView) findViewById(R.id.nowspeed);
        imageNow = (ImageView)findViewById(R.id.image);
        temps = new int[]{0, R.id.l1_t2,R.id.l2_t2,R.id.l3_t2,R.id.l4_t2,R.id.l5_t2};
        dats = new int[]{0, R.id.l1_t1,R.id.l2_t1,R.id.l3_t1,R.id.l4_t1,R.id.l5_t1};
        images = new int[]{0, R.id.l1_i1,R.id.l2_i1,R.id.l3_i1,R.id.l4_i1,R.id.l5_i1};
        nowWeather = (TextView) findViewById(R.id.nowWeather);

        nowTemp.setText(maxC[0]);
        nowSpeed.setText(windspeed[0]);
        nowHum.setText(humidity[0] + "%");
        nowWeather.setText(dbc.getWeather(code[0]));
        imageNow.setImageResource(dbc.getImage(code[0]));

        for (int i = 1; i <= 5; i++) {
            temp = (TextView) findViewById(temps[i]); dat = (TextView) findViewById(dats[i]); image = (ImageView) findViewById(images[i]);
            temp.setText(minC[i] + ".." + maxC[i]);
            dat.setText(date[i]);
            image.setImageResource(dbc.getImage(code[i]));
        }
        setTime();
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (day.length() == 1) day = "0" + day;
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        if (month.length() == 1) month = "0" + month;
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        if (hour.length() == 1) hour = "0" + hour;
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        if (minute.length() == 1) minute = "0" + minute;
        time.setText(day+"."+month+" "+hour+":"+minute);
    }

    private void getWeather() {
        String coor = db.getCoor(city.substring(0, city.indexOf(",")));
        Intent intent = new Intent(this, WeatherService.class);
        intent.putExtra(URL, w1 + coor + w2);
        startService(intent);
    }

    private void register() {
        WeatherReceiver wet = new WeatherReceiver();
        IntentFilter filter = new IntentFilter(RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(wet, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getWeather();
        }
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(this, CityActivity.class));
            this.finish();
        }
        return true;
    }
    private void convertSpeed() {
        for (int i = 0; i < 5; i++) {
            int speed = (int)(Integer.parseInt(windspeed[i]) / 3.6);
            windspeed[i] = Integer.toString(speed);
        }
    }
    private void convertDate() {
        String day;
        String[] months = new String[]{"a", "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня", "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
        for (int i = 1; i <= 5; i++) {
            day = date[i].substring(8, date[i].length());
            date[i] = day + " " + months[Integer.parseInt(date[i].substring(5, 7))];
        }
    }
    public class WeatherReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            weather = intent.getStringArrayExtra(WEATHER);
            minC = intent.getStringArrayExtra(MINC);
            maxC = intent.getStringArrayExtra(MAXC);
            humidity = intent.getStringArrayExtra(HUMIDITY);
            windspeed = intent.getStringArrayExtra(WINDSPEED);
            convertSpeed();
            date = intent.getStringArrayExtra(DATE);
            convertDate();
            code = intent.getStringArrayExtra(CODE);
            setWeather();
        }
    }


}
