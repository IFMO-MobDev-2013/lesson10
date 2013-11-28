package com.example.h9_weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;



public class WeatherActivity extends Activity {
    TextView tv,tv1,tv2,tvCity,tvTemp;
    String city;
    final String CITY = "city_eng" ;
    final String KEY = "dwp8gg4ybvydw59x55643p3m";
    final String API_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?format=json&num_of_days=3&key=" + KEY;
    String t,t1,t2,temperature;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherscreen);
        Intent intent = getIntent();
        tv = (TextView)findViewById(R.id.textView);
        tv1 = (TextView)findViewById(R.id.textView1);
        tv2 = (TextView)findViewById(R.id.textView2);
        tvCity = (TextView)findViewById(R.id.textCity);
        tvTemp = (TextView)findViewById(R.id.textTemp);

        city = intent.getStringExtra(CITY);
        tvCity.setText(city);
        new Weather().execute(city);
    }

    private class Weather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = API_URL + "&q=" + params[0];
                HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity, "UTF-8");
                JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
                json = object.getString("data");
                object = (JSONObject) new JSONTokener(json).nextValue();
                String cond = object.getString("current_condition");
                object = new JSONArray(cond).getJSONObject(0);
                JSONObject weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                t1 = weatherJ.getString("value");
                int pressure = 76000 * Integer.parseInt(object.getString("pressure")) / 101325;
                t2 = "" + pressure;
                t = object.getString("humidity");
                temperature = object.getString("temp_C");
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (t == null)
            {
                String ci = "No information about weather in " + city;
                tvCity.setText(ci);
                tv.setText("");
                tv1.setText("");
                tv2.setText("");
                tvTemp.setText("");
                return;
            }
            t = "Air humidity: " + t;
            t2 = "Pressure: " + t2 + " mm Hg " ;
            tv.setText(t);
            tv1.setText(t1);
            tv2.setText(t2);
            tvTemp.setText(temperature);
        }
    }

}