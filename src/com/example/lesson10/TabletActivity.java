package com.example.lesson10;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lesson10.databases.CitiesDatabase;
import com.example.lesson10.databases.WeatherDatabase;
import com.example.lesson10.services.WeatherService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Genyaz
 * Date: 12.12.13
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class TabletActivity extends Activity {

    public ListView citiesList;
    public CitiesAdapter citiesAdapter;
    public EditText cityEditText;
    public TextView cityName;
    public TextView cityTemperature;
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
                nameView.setText("\n" + name + "\n");
                nameView.setTextSize(20);
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
                LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
                nameView.setLayoutParams(nameParams);
                linearLayout.addView(nameView);
                ImageView imageView = new ImageView(context);
                new DownloadImageTask(imageView, 50).execute("http://openweathermap.org/img/w/" + info.icon_path);
                imageView.setLayoutParams(otherParams);
                linearLayout.addView(imageView);
                TextView temp = new TextView(context);
                temp.setText("\n" + info.temp + "°C:\n" + info.temp_min + "°C ... " + info.temp_max + "°C");
                temp.setTextSize(15);
                temp.setLayoutParams(otherParams);
                linearLayout.addView(temp);
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
        loadData(citiesAdapter.getName(position), citiesAdapter.getId(position));
    }

    public void itemLongClicked(int position) {
        CitiesDatabase citiesDatabase = new CitiesDatabase(this);
        SQLiteDatabase wdb = citiesDatabase.getWritableDatabase();
        wdb.delete(CitiesDatabase.DATABASE_NAME, CitiesDatabase._ID + " = " + citiesAdapter.getId(position), null);
        wdb.close();
        citiesDatabase.close();
        WeatherDatabase weatherDatabase = new WeatherDatabase(this);
        wdb = weatherDatabase.getWritableDatabase();
        wdb.delete(WeatherDatabase.DATABASE_NAME, WeatherDatabase.CITY_ID + " = " + citiesAdapter.getId(position), null);
        wdb.close();
        weatherDatabase.close();
        update();
    }

    public void addCity(String cityName) {
        CitiesDatabase citiesDatabase = new CitiesDatabase(this);
        SQLiteDatabase wdb = citiesDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CitiesDatabase.CITY_NAME, cityName);
        wdb.insert(CitiesDatabase.DATABASE_NAME, null, cv);
        wdb.close();
        citiesDatabase.close();
        update();
    }

    public void updateClicked(View view) {
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    public void update(View view) {
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


    private String getMonthName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            default:
                return "Dec";
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int size;

        public DownloadImageTask(ImageView bmImage, int size) {
            this.bmImage = bmImage;
            this.size = size;
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

        protected void onPostExecute(Bitmap result) {
            if (result.getWidth() < size) {
                result = Bitmap.createScaledBitmap(result, size, (result.getHeight() * size) / result.getWidth(), false);
            }
            bmImage.setImageBitmap(result);
        }
    }

    public void parseWeatherInfo(String s) {
        try {
            JSONObject json = new JSONObject(s);
            JSONArray jsonArray = json.getJSONArray("list");
            int d_day = 0;
            double lon = json.getJSONObject("city").getJSONObject("coord").getDouble("lon");
            double d_time = (360 + lon) * 24 / 360;
            int d_hours = 3 * ((int)(d_time / 3));
            if (d_time - d_hours >= 1.5) {
                d_hours += 3;
            }
            JSONObject result = jsonArray.getJSONObject(0);
            JSONObject mmainInfo = result.getJSONObject("main");
            int curtemp = (int) mmainInfo.getDouble("temp");
            int curtemp_min = (int) mmainInfo.getDouble("temp_min");
            int curtemp_max = (int) mmainInfo.getDouble("temp_max");
            String curtemp_info = curtemp + "°C:\n" + curtemp_min + "..." + curtemp_max;
            ((TextView) findViewById(R.id.currentTemperature)).setText(curtemp_info);
            int curpressure = (int) (mmainInfo.getDouble("pressure") * 0.75);
            ((TextView) findViewById(R.id.currentPressure)).setText("Pressure: " + curpressure + " Hg-mm");
            int curhumidity = (int) mmainInfo.getDouble("humidity");
            ((TextView) findViewById(R.id.currentHumidity)).setText("Humidity: " + curhumidity + "%");
            double curwind_speed = result.getJSONObject("wind").getDouble("speed");
            ((TextView) findViewById(R.id.currentWind)).setText("Wind speed: " + curwind_speed + " m/s");
            String curicon_path = result.getJSONArray("weather").getJSONObject(0).getString("icon");
            new DownloadImageTask((ImageView) findViewById(R.id.curWeatherPicture), 100).execute("http://openweathermap.org/img/w/" + curicon_path);
            String rraw_dt_txt = result.getString("dt_txt");
            String ddt_txt = rraw_dt_txt.split("\\s")[1];
            ddt_txt = ddt_txt.split("\\:")[0];
            ((TextView) findViewById(R.id.lastUpdated)).setText("Upd: " + (Integer.parseInt(ddt_txt) + d_hours) % 24 + ":00");
            int mmonth = Integer.parseInt(rraw_dt_txt.split("-")[1]);
            int dday = Integer.parseInt(rraw_dt_txt.split("-")[2].split(" ")[0]);
            ((TextView) findViewById(R.id.currentTime)).setText(
                    "Today - " + dday + " " + getMonthName(mmonth));
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONObject mainInfo = object.getJSONObject("main");
                int temp_min = (int) mainInfo.getDouble("temp_min");
                int temp_max = (int) mainInfo.getDouble("temp_max");
                String temp_info = temp_min + "°C\n" + temp_max + "°C";
                String iconPath = object.getJSONArray("weather").getJSONObject(0).getString("icon");
                String raw_dt_txt = object.getString("dt_txt");
                String dt_txt = raw_dt_txt.split("\\s")[1];
                dt_txt = dt_txt.split("\\:")[0];
                int month = Integer.parseInt(raw_dt_txt.split("-")[1]);
                int day = Integer.parseInt(raw_dt_txt.split("-")[2].split(" ")[0]);
                int hours = (Integer.parseInt(dt_txt) + d_hours) % 24;
                if (hours == 0) {
                    d_day++;
                }
                switch (d_day) {
                    case 1:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.tDate)).setText(
                                        "Tomorrow - " + day + " " + getMonthName(month));
                                ((TextView) findViewById(R.id.tomMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomMorI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                ((TextView) findViewById(R.id.tomDayT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomDayI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 21:
                                ((TextView) findViewById(R.id.tomEveT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.tomEveI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                        }
                        break;
                    case 2:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.ttDate)).setText(day + " " + getMonthName(month));
                                ((TextView) findViewById(R.id.ttMorT)).setText(temp_info);
                                new DownloadImageTask((ImageView) findViewById(R.id.ttMorI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttDayI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.ttEveI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.ttEveT)).setText(temp_info);
                                break;
                        }
                        break;
                    case 3:
                        switch (hours) {
                            case 9:
                                ((TextView) findViewById(R.id.tttDate)).setText(day + " " + getMonthName(month));
                                new DownloadImageTask((ImageView) findViewById(R.id.tttMorI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttMorT)).setText(temp_info);
                                break;
                            case 15:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttDayI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttDayT)).setText(temp_info);
                                break;
                            case 21:
                                new DownloadImageTask((ImageView) findViewById(R.id.tttEveI), 50).execute("http://openweathermap.org/img/w/" + iconPath);
                                ((TextView) findViewById(R.id.tttEveT)).setText(temp_info);
                                break;
                        }
                        break;
                }

            }
        } catch (Exception e) {
            weatherParsingError();
        }
    }

    public void weatherParsingError() {
        ((TextView) findViewById(R.id.currentTime)).setText("No data has been found.");
    }

    public void loadData(String name, int id) {
        cityName = (TextView) findViewById(R.id.city_name);
        cityName.setText(name);
        WeatherDatabase weatherDatabase = new WeatherDatabase(this.context);
        SQLiteDatabase rdb = weatherDatabase.getReadableDatabase();
        Cursor cursor = rdb.query(WeatherDatabase.DATABASE_NAME, null, WeatherDatabase.CITY_ID + " = " + id,
                null, null, null, null);
        if (cursor.moveToNext()) {
            parseWeatherInfo(cursor.getString(cursor.getColumnIndex(WeatherDatabase.CITY_WEATHER)));
        } else {
            weatherParsingError();
        }
        cursor.close();
        rdb.close();
        weatherDatabase.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_fragments_layout);
        context = this;
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
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        cityEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addCity(cityEditText.getText().toString());
                    cityEditText.setText("");
                    return true;
                }
                return false;
            }
        });
        serviceIntent = new Intent(this, WeatherService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0);
        AlarmManager manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 1800000, pendingIntent);
    }
}
