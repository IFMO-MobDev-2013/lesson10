package com.example.MaWeather;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import org.apache.http.util.ExceptionUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class WeatherActivity extends Activity {

    //private WeatherBroadcastReceiver weatherBroadcastReceiver;
    private ArrayList<WeatherItem> nodes;

    private ImageView basePicture;
    private Bitmap[] pictures;
    private Bitmap lvPicture;
    TextView curTemperature;
    TextView curWeatherType;
    TextView sunrise;
    TextView sunset;
    TextView pressure;
    TextView humidity;
    TextView temperature1;
    TextView type1;
    TextView temperature2;
    TextView type2;
    TextView temperature3;
    TextView type3;

    ImageView picture1;
    ImageView picture2;
    ImageView picture3;

    int cityId;
    String cityName;

    private ListView listView;

    private ArrayList<HashMap<String, Object>> items;

    private LazyAdapter adapter;

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return String.format("%02d:%02d", hour, minute);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //weatherBroadcastReceiver = new WeatherBroadcastReceiver();

        cityId = getIntent().getExtras().getInt("cityId");
        cityName = getIntent().getStringExtra("city");

        Log.d("myTag", cityName);

        //imageView = (ImageView) findViewById(R.id.imageView);

        TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setTextSize(40);

        TextView city = (TextView) findViewById(R.id.city);
        city.setText(cityName);
        //city.setTextSize(40);

        //TextView lastUpdate = (TextView) findViewById(R.id.last_update);
        //lastUpdate.setText("Обновлено " + getCurrentTime());
        //lastUpdate.setTextSize(8);

        curTemperature = (TextView) findViewById(R.id.cur_temperature);
        basePicture = (ImageView) findViewById((R.id.base_picture));
        curWeatherType = (TextView) findViewById(R.id.cur_weather_type);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        temperature1 = (TextView) findViewById(R.id.temperature1);
        picture1 = (ImageView) findViewById(R.id.picture1);
        type1 = (TextView) findViewById(R.id.type1);
        temperature2 = (TextView) findViewById(R.id.temperature2);
        picture2 = (ImageView) findViewById(R.id.picture2);
        type2 = (TextView) findViewById(R.id.type2);
        temperature3 = (TextView) findViewById(R.id.temperature3);
        picture3 = (ImageView) findViewById(R.id.picture3);
        type3 = (TextView) findViewById(R.id.type3);

        listView = (ListView) findViewById(R.id.listView);

        pictures = new Bitmap[19];

        pictures[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p1);
        pictures[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p2);
        pictures[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p3);
        pictures[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p4);
        pictures[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p5);
        pictures[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p6);
        pictures[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p7);
        pictures[8] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p8);
        pictures[9] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p9);
        pictures[10] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p10);
        pictures[11] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p11);
        pictures[12] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p12);
        pictures[13] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p13);
        pictures[14] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p14);
        pictures[15] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p15);
        pictures[16] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p16);
        pictures[17] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p17);
        pictures[18] = BitmapFactory.decodeResource(this.getResources(), R.drawable.p18);

        WeatherDataBase weatherDataBase = new WeatherDataBase(this);

        nodes = weatherDataBase.getForecast(cityName);

        curTemperature.setText(nodes.get(0).getTemperature() + "\u00B0");
        //curTemperature.setTextSize(150);

        curWeatherType.setText(nodes.get(0).getWeatherType());
        //curWeatherType.setTextSize(20);

        sunrise.setText("Восход " + nodes.get(0).getSunrise());
        //sunrise.setTextSize(15);

        sunset.setText("Заход " + nodes.get(0).getSunset());
        //sunset.setTextSize(15);

        pressure.setText("Влажность " + nodes.get(0).getHumidity() + "%");
        //pressure.setTextSize(15);

        humidity.setText("Давление " + nodes.get(0).getPressure() + "мм");
        //humidity.setTextSize(15);

        temperature1.setText(nodes.get(1).getTemperature() + "\u00B0 ");

        type1.setText(nodes.get(1).getWeatherType());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader.displayImage("http://yandex.st/weather/1.1.89/i/icons/48x48/" + nodes.get(1).getPictureType() + ".png", picture1);

        temperature2.setText(nodes.get(2).getTemperature() + "\u00B0 ");

        type2.setText(nodes.get(2).getWeatherType());

        imageLoader.displayImage("http://yandex.st/weather/1.1.89/i/icons/48x48/" + nodes.get(2).getPictureType() + ".png", picture2);

        temperature3.setText(nodes.get(3).getTemperature() + "\u00B0 ");

        type3.setText(nodes.get(3).getWeatherType());

        imageLoader.displayImage("http://yandex.st/weather/1.1.89/i/icons/48x48/" + nodes.get(3).getPictureType() + ".png", picture3);


        basePicture.setImageBitmap(pictures[new Integer(nodes.get(0).getBigPictureType()).intValue()]);

        ArrayList<WeatherItem> someItems = new ArrayList<WeatherItem>(nodes.subList(4, nodes.size()));

        adapter = new LazyAdapter(WeatherActivity.this, this,  someItems);

        listView.setAdapter(adapter);

        /*IntentFilter intentFilter = new IntentFilter("com.example.MaWeather.RESPONSE");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(weatherBroadcastReceiver, intentFilter);

        Intent intentWeather = new Intent(WeatherActivity.this, WeatherGetter.class);
        startService(intentWeather.putExtra("cityId", cityId).putExtra("cityName", cityName));*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.putInt("cityId", cityId);
        editor.putString("cityName", cityName);
        editor.commit();
    }



}
