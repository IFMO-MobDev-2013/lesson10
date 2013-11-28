package com.example.lesson10;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lesson10.databases.CitiesDatabase;
import com.example.lesson10.databases.WeatherDatabase;
import com.example.lesson10.services.WeatherService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CitiesActivity extends Activity {

    public ListView citiesList;
    public CitiesAdapter citiesAdapter;
    public Context context;

    private Intent serviceIntent;

    public class CitiesAdapter extends BaseAdapter {

       public class WeatherShortInfo {
            public String icon_path;
            public int temp, temp_min, temp_max;
            public int humidity;
            public int pressure;
            public double wind_speed;

            public WeatherShortInfo(String string) {
                try {
                    JSONObject json = new JSONObject(string);
                    JSONObject result = json.getJSONArray("list").getJSONObject(0);
                    JSONObject mainInfo = result.getJSONObject("main");
                    this.temp = (int) mainInfo.getDouble("temp");
                    this.temp_min = (int) mainInfo.getDouble("temp_min");
                    this.temp_max = (int) mainInfo.getDouble("temp_max");
                    this.pressure = (int) (mainInfo.getDouble("pressure") * 0.75);
                    this.humidity = (int) mainInfo.getDouble("humidity");
                    this.wind_speed = result.getJSONObject("wind").getDouble("speed");
                    this.icon_path = result.getJSONArray("weather").getJSONObject(0).getString("icon");
                } catch (JSONException e) {
                }
            }
        }

        public class CityHolder {
            private int id;
            private String name;
            private View view;

            public CityHolder(String name, int id, WeatherShortInfo info) {
                this.name = name;
                this.id = id;
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView nameView = new TextView(context);
                nameView.setText(name);
                nameView.setTextSize(15);
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
                LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                nameView.setLayoutParams(nameParams);
                linearLayout.addView(nameView);
                ImageView imageView = new ImageView(context);
                new DownloadImageTask(imageView).execute("http://openweathermap.org/img/w/" + info.icon_path);
                imageView.setLayoutParams(otherParams);
                linearLayout.addView(imageView);
                TextView temp = new TextView(context);
                temp.setText(info.temp + "°C:\n" + info.temp_min + "..." + info.temp_max);
                temp.setTextSize(10);
                temp.setLayoutParams(otherParams);
                linearLayout.addView(temp);
                TextView pressure = new TextView(context);
                pressure.setTextSize(10);
                pressure.setText(info.pressure + "\nmm-Hg");
                pressure.setLayoutParams(otherParams);
                linearLayout.addView(pressure);
                TextView humidity = new TextView(context);
                humidity.setTextSize(10);
                humidity.setText(info.humidity + "%");
                humidity.setLayoutParams(otherParams);
                linearLayout.addView(humidity);
                TextView windSpeed = new TextView(context);
                windSpeed.setTextSize(10);
                windSpeed.setText(info.wind_speed + "\nm/s");
                windSpeed.setLayoutParams(otherParams);
                linearLayout.addView(windSpeed);
                this.view = linearLayout;
            }

            public CityHolder(String name, int id) {
                this.name = name;
                this.id = id;
                TextView textView = new TextView(context);
                textView.setTextSize(15);
                textView.setText(name + " (no info, click to check)");
                this.view = textView;
            }
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                }
                return mIcon11;
            }

            private static final int minWidth = 100;

            protected void onPostExecute(Bitmap result) {

                if (result.getWidth() < minWidth) {
                    result = Bitmap.createScaledBitmap(result, minWidth, (result.getHeight() * minWidth) / result.getWidth(), false);
                }
                bmImage.setImageBitmap(result);
            }
        }

        private Vector<CityHolder> cities = new Vector<CityHolder>();

        public void addCity(String name, int id, String weather) {
            if (weather.isEmpty()) {
                cities.add(new CityHolder(name, id));
            } else {
                cities.add(new CityHolder(name, id, new WeatherShortInfo(weather)));
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
        }

        public String getName(int position) {
            return cities.get(position).name;
        }

        public int getId(int position) {
            return cities.get(position).id;
        }

        @Override
        public long getItemId(int position) {
            return cities.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return cities.get(position).view;
        }
    }

    public void itemClicked(int position) {
        Intent intent = new Intent(this, CityWeatherActivity.class);
        intent.putExtra(getString(R.string.cityname), citiesAdapter.getName(position));
        intent.putExtra(getString(R.string.cityid), citiesAdapter.getId(position));
        startActivity(intent);
    }

    public void itemLongClicked(int position) {
        Intent intent = new Intent(this, CityModificationActivity.class);
        intent.putExtra(getString(R.string.cityname), citiesAdapter.getName(position));
        intent.putExtra(getString(R.string.cityid), citiesAdapter.getId(position));
        startActivity(intent);
    }

    public void addCityClicked(View view) {
        Intent intent = new Intent(this, CityModificationActivity.class);
        intent.putExtra(getString(R.string.cityname), "");
        intent.putExtra(getString(R.string.cityid), -1);
        startActivity(intent);
    }

    public void updateClicked(View view) {
        startService(serviceIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_list);
        citiesList = (ListView) findViewById(R.id.citiesListView);
        citiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(position);
            }
        });
        citiesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemLongClicked(position);
                return true;
            }
        });
        context = this;
        serviceIntent = new Intent(this, WeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        AlarmManager manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1800000, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    public void update() {
        citiesAdapter = new CitiesAdapter();
        citiesList.setAdapter(citiesAdapter);
        CitiesDatabase citiesdb = new CitiesDatabase(this);
        SQLiteDatabase rdb = citiesdb.getReadableDatabase();
        Cursor cursor = rdb.query(CitiesDatabase.DATABASE_NAME,
                null, null, null, null, null, null);
        int city_name_column = cursor.getColumnIndex(CitiesDatabase.CITY_NAME);
        int id_column = cursor.getColumnIndex(CitiesDatabase._ID);
        String city_name;
        int id;
        List<CityInfo> cityInfos = new ArrayList<CityInfo>();
        while (cursor.moveToNext()) {
            city_name = cursor.getString(city_name_column);
            id = cursor.getInt(id_column);
            cityInfos.add(new CityInfo(city_name, id));
        }
        cursor.close();
        rdb.close();
        citiesdb.close();
        WeatherDatabase weatherdb = new WeatherDatabase(this);
        rdb = weatherdb.getReadableDatabase();
        CityInfo cityInfo;
        for (int i = 0; i < cityInfos.size(); i++) {
            cityInfo = cityInfos.get(i);
            cursor = rdb.query(WeatherDatabase.DATABASE_NAME, null, WeatherDatabase.CITY_ID + " = " +cityInfo.id,
                    null, null, null, null, null);
            if (cursor.moveToNext()) {
                citiesAdapter.addCity(cityInfo.name, cityInfo.id, cursor.getString(cursor.getColumnIndex(WeatherDatabase.CITY_WEATHER)));
            } else {
                citiesAdapter.addCity(cityInfo.name, cityInfo.id, "");
            }
            cursor.close();
        }
        rdb.close();
        weatherdb.close();
    }
}
