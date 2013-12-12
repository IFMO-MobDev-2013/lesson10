package com.example.h9_weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Vector;


public class WeatherActivity extends Activity {
    TextView tvCity,tvTempNow,tvTemp,tvHumidity,tvPressure,tvSpeed,tvCondition;
    TextView tv11,tv12,tv13,tv21,tv22,tv23,tv31,tv32,tv33,tv41,tv42,tv43;
    String city,city4search;
    final String CITY = "city_eng" ;
    final String KEY = "dwp8gg4ybvydw59x55643p3m";
    final String API_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?format=json&num_of_days=4&key=" + KEY;
    String w[][] = new String[5][9];
    ImageView imageCurBig,imageCurSmall,image1,image2,image3;
    Vector<TextView> tv = new Vector<TextView>();
    public void ititializeViews()
    {
        imageCurBig = (ImageView)findViewById(R.id.imageCurBig);
        imageCurSmall = (ImageView)findViewById(R.id.imageCurSmall);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        tvCity = (TextView)findViewById(R.id.textCity);
        tvTempNow = (TextView)findViewById(R.id.textTempNowBig);
        tvTemp = (TextView)findViewById(R.id.textTempBig);
        tvHumidity = (TextView)findViewById(R.id.textHumidityBig);
        tvPressure = (TextView)findViewById(R.id.textPressureBig);
        tvSpeed = (TextView)findViewById(R.id.textSpeedBig);
        tvCondition = (TextView)findViewById(R.id.textConditionBig);
        tv11 = (TextView)findViewById(R.id.text11);
        tv12 = (TextView)findViewById(R.id.text12);
        tv13 = (TextView)findViewById(R.id.text13);
        tv21 = (TextView)findViewById(R.id.text21);
        tv22 = (TextView)findViewById(R.id.text22);
        tv23 = (TextView)findViewById(R.id.text23);
        tv31 = (TextView)findViewById(R.id.text31);
        tv32 = (TextView)findViewById(R.id.text32);
        tv33 = (TextView)findViewById(R.id.text33);
        tv41 = (TextView)findViewById(R.id.text41);
        tv42 = (TextView)findViewById(R.id.text42);
        tv43 = (TextView)findViewById(R.id.text43);
        tv.add(tvCity);
        tv.add(tvTempNow);
        tv.add(tvTemp);
        tv.add(tvHumidity);
        tv.add(tvPressure);
        tv.add(tvSpeed);
        tv.add(tvCondition);
        tv.add(tv11);
        tv.add(tv12);
        tv.add(tv13);
        tv.add(tv21);
        tv.add(tv22);
        tv.add(tv23);
        tv.add(tv31);
        tv.add(tv32);
        tv.add(tv33);
        tv.add(tv41);
        tv.add(tv42);
        tv.add(tv43);
    }

    public String adaptate(String curcity)
    {
        String res = curcity;
        for (int i = 0; i < curcity.length(); i++){
            if (curcity.charAt(i) == ' ' || curcity.charAt(i) == '-')
            {
                 res = curcity.substring(0,i) + '+' + curcity.substring(i+1);
            }
        }
        return res;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherscreen);
        Intent intent = getIntent();
        tvCity = (TextView)findViewById(R.id.textCity);
        city = intent.getStringExtra(CITY);
        city4search = adaptate(city);
        ititializeViews();
        new Weather().execute(city4search);
    }

    private class Weather extends AsyncTask<String, Void, String> {
        int date = 0, picture = 1, condition = 2,minC = 3,maxC = 4,humidity = 5,pressure = 6, speed = 7,curTemp = 8;
        int now = 0,today = 1,day1 = 2,day2 = 3,day3 = 4;
        @Override
        protected String doInBackground(String... params) {
            try {
                String url = API_URL + "&q=" + params[0];
                HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity, "UTF-8");

                JSONObject object;
                object = (JSONObject) new JSONTokener(json).nextValue();
                object = (JSONObject) new JSONTokener(object.getString("data")).nextValue();
                String cond = object.getString("current_condition");
                object = new JSONArray(cond).getJSONObject(0);

                w[now][date] = "Today";
                w[now][picture] = object.getString("weatherCode");
                JSONObject weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                w[now][condition] =  weatherJ.getString("value");
                w[now][minC] = null;
                w[now][maxC] = null;
                w[now][humidity] = object.getString("humidity");
                w[now][pressure] = calculatePressure(Integer.parseInt(object.getString("pressure")));
                w[now][speed] = object.getString("windspeedKmph");
                w[now][curTemp] = object.getString("temp_C");

                //
                object = (JSONObject) new JSONTokener(json).nextValue();
                object = (JSONObject) new JSONTokener(object.getString("data")).nextValue();
                cond = object.getString("weather");
                object = new JSONArray(cond).getJSONObject(0);
                w[today][date] = "Today";
                w[today][picture] = object.getString("weatherCode");
                weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                w[today][condition] =  weatherJ.getString("value");
                w[today][minC] = object.getString("tempMinC");
                w[today][maxC] = object.getString("tempMaxC");
                //

                object = new JSONArray(cond).getJSONObject(1);
                w[day1][date] = object.getString("date");
                w[day1][picture] = object.getString("weatherCode");
                weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                w[day1][condition] =  weatherJ.getString("value");
                w[day1][minC] = object.getString("tempMinC");
                w[day1][maxC] = object.getString("tempMaxC");

                object = new JSONArray(cond).getJSONObject(2);
                w[day2][date] = object.getString("date");;
                w[day2][picture] = object.getString("weatherCode");
                weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                w[day2][condition] =  weatherJ.getString("value");
                w[day2][minC] = object.getString("tempMinC");
                w[day2][maxC] = object.getString("tempMaxC");

                object = new JSONArray(cond).getJSONObject(3);
                w[day3][date] = object.getString("date");;
                w[day3][picture] = object.getString("weatherCode");
                weatherJ = new JSONArray(object.getString("weatherDesc")).getJSONObject(0);
                w[day3][condition] =  weatherJ.getString("value");
                w[day3][minC] = object.getString("tempMinC");
                w[day3][maxC] = object.getString("tempMaxC");

                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }



        @Override
        protected void onPostExecute(String result) {
            if (result == null)
            {
                setNoInformation(city);
                return;
            }

            tvCity.setText(city);
            tvTempNow.setText(w[now][curTemp] + "°С");
            tvTemp.setText(w[today][minC] + "..." + w[today][maxC] + "°С");
            tvHumidity.setText(w[now][humidity] + " % " + " humidity");
            tvPressure.setText(w[now][pressure] + " mm. Hg.");
            tvSpeed.setText(w[now][speed] + " m/c");
            tvCondition.setText(w[now][condition]);
            setImageView(imageCurBig,Integer.parseInt(w[now][picture]));

            tv11.setText(w[today][date]);
            tv13.setText(w[today][minC] + "..." + w[today][maxC] + "°С");
            tv12.setText(w[today][condition]);
            setImageView(imageCurSmall,Integer.parseInt(w[today][picture]));

            tv21.setText(w[day1][date]);
            tv23.setText(w[day1][minC] + "..." + w[day1][maxC] + "°С");
            tv22.setText(w[day1][condition]);
            setImageView(image1,Integer.parseInt(w[day1][picture]));

            tv31.setText(w[day2][date]);
            tv33.setText(w[day2][minC] + "..." + w[day2][maxC] + "°С");
            tv32.setText(w[day2][condition]);
            setImageView(image2,Integer.parseInt(w[day2][picture]));

            tv41.setText(w[day3][date]);
            tv43.setText(w[day3][minC] + "..." + w[day3][maxC] + "°С");
            tv42.setText(w[day3][condition]);
            setImageView(image3,Integer.parseInt(w[day3][picture]));
        }
    }

    public String calculatePressure(int pr)
    {
        pr = (76000 * pr / 101325);
        return "" + pr;
    }

    public void setNoInformation(String city)

    {
        for (int i = 1; i < tv.size(); i++)
            tv.get(i).setText(" ");
        tvCity.setText("No information about weather in " + city);
    }

    public void setImageView(ImageView curIm, int weatherCode)
    {
        Resources res = getResources();
        int condition[];
        condition = res.getIntArray(R.array.cloud);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.cloudy);
                return;
            }
        condition = res.getIntArray(R.array.partly_cloud);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.partly_cloudy);
                return;
            }
        condition = res.getIntArray(R.array.rain);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.rain);
                return;
            }
        condition = res.getIntArray(R.array.strong_rain);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.strong_rain);
                return;
            }
        condition = res.getIntArray(R.array.sun);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.sunny);
                return;
            }
        condition = res.getIntArray(R.array.thunder);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.thunderstorm);
                return;
            }
        condition = res.getIntArray(R.array.snow);
        for (int i = 0; i < condition.length; i++)
            if (condition[i] == weatherCode)
            {
                curIm.setImageResource(R.drawable.snow);
                return;
            }
    }
}