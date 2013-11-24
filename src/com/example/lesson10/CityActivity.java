package com.example.lesson10;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class CityActivity extends Activity implements IEventHadler
{
    private long id_city = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cityweather);
        id_city = getIntent().getLongExtra("ID_CITY", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Console.print("Start update");
        City.findById(id_city).addEventListener(this);
        City.findById(id_city).startUpdate();
    }

    public int getImageByType(String type)
    {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("clear", R.drawable.clear);
        map.put("overcastandlightrain", R.drawable.overcastandlightrain);
        map.put("mostlyclear", R.drawable.most);
        map.put("partlycloudy", R.drawable.partlycloudy);
        map.put("cloudy", R.drawable.cloudy);
        map.put("overcast", R.drawable.overcast);
        if (map.containsKey(type))
        {
            return map.get(type);
        }
        else
        {
            return R.drawable.ic_launcher;
        }
    }

    public void setter(int resourse, String string)
    {
        TextView tv = (TextView) findViewById(resourse);
        tv.setText(string);
    }

    @Override
    public void handleEvent(Event e)
    {
          if (e.type == Event.COMPLETE)
          {
              e.target.removeEventListener(this);
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      setter(R.id.cityNameLabel, City.findById(id_city).name);
                      setter(R.id.cityTemperatureLabel, City.findById(id_city).temperature + "°");
                      setter(R.id.weatherTypeLabel, City.findById(id_city).weather_desc);
                      setter(R.id.cityweather_humidityView, City.findById(id_city).hum + "%");
                      setter(R.id.cityweather_wind_speedView, City.findById(id_city).speed + " м/c");

                      setter(R.id.cityweather_morningTemperatureView, City.findById(id_city).getToday().morning + "°");
                      setter(R.id.cityweather_dayTemperatureView, City.findById(id_city).getToday().day + "°");
                      setter(R.id.cityweather_eveningTemperatureView, City.findById(id_city).getToday().evening + "°");
                      setter(R.id.cityweather_nightTemperatureView, City.findById(id_city).getToday().night + "°");

                      setter(R.id.cityweather_offset1TemperatureView, City.findById(id_city).getDay(1).getAverage() + "°");
                      setter(R.id.cityweather_offset2TemperatureView, City.findById(id_city).getDay(2).getAverage() + "°");
                      setter(R.id.cityweather_offset3TemperatureView, City.findById(id_city).getDay(3).getAverage() + "°");

                      ImageView iv = (ImageView)findViewById(R.id.imageView);
                      iv.setImageResource(getImageByType(City.findById(id_city).weather_code));
                  }
              });
          }
    }
}
