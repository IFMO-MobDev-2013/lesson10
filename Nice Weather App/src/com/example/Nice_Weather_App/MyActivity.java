package com.example.Nice_Weather_App;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;

import java.util.List;

import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MyActivity extends Activity {
    public static final String URL_ADRESS = "http://export.yandex.ru/weather-ng/forecasts/";
    public static final String KEY_FOR_WEATHER_INTENT = "key_for_weather_intent";
    public static final String KEY_CITY = "city";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_NOW = "NOW";
    public static final String KEY_LAST = "LAST";
    public static final String KEY_NO = "NO";
    public static final String KEY_YES = "YES";
    public static final String KEY_YANDEX_ID = "yandex_id";
    public static String CITY_YES = "";

    public static WeatherDataBase weatherDataBase;

    private ArrayList<Integer> allCitiesID = new ArrayList<Integer>();
    private ArrayList<HashMap<String, String>> allCities = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> addedCities = new ArrayList<HashMap<String, String>>();
    public static ArrayList<Integer> addedCitiesID = new ArrayList<Integer>();
    private ArrayList<HashMap<String, String>> currentCities = new ArrayList<HashMap<String, String>>();
    private ArrayList<Integer> currentCitiesID = new ArrayList<Integer>();

    private ListView currentCitiesListView;
    private ListView addedCitiesListView;
    private List<View> pages;
    private ViewPager viewPager;
    private ProgressDialog dialog;
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private ImageView imageView, to_left, to_right;
    private boolean isWasBroadcast = false;
    private boolean isDialog = false;
    private boolean isCurrent = false;
    private boolean isYes = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //deleteDatabase(WeatherDataBase.DATABASE_NAME);
        weatherDataBase = new WeatherDataBase(this);
        weatherDataBase.open();

        // building view pages
        buildingViewPages();

        // initialization
        currentCitiesListView = (ListView) pages.get(0).findViewById(R.id.all_cities);
        addedCitiesListView = (ListView) pages.get(1).findViewById(R.id.added_cities);

        openFileAndPrintAllCities();         // open file with cities and printing cities
        printCityFromTable();                // print city from data base
        printMainWeather(KEY_LAST);            // print last weather MAIN
        printOtherWeather(3, KEY_LAST);        // print last weather on tomorrow
        printOtherWeather(4, KEY_LAST);        // print last wather on after-tomorrow
        printOtherWeather(5, KEY_LAST);        // print last weather on after-after-tomorrow

        // first start my app. Help to understand about chooseing
        if (addedCities.isEmpty() == true) {
            helpWhenAddCityIsEmpty();
        } else {
            TextView textView = (TextView) pages.get(1).findViewById(R.id.textView);
            textView.setText("Мои города");
            viewPager.setCurrentItem(2);
         //   deleteWeatherFromPages();
        }

        // print weather. WITHOUT LOADIN (offline)
        addedCitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long number) {
                printMainWeather(addedCities.get(index).get(KEY_CITY));
                printOtherWeather(3, addedCities.get(index).get(KEY_CITY));
                printOtherWeather(4, addedCities.get(index).get(KEY_CITY));
                printOtherWeather(5, addedCities.get(index).get(KEY_CITY));
                viewPager.setCurrentItem(2);
            }
        });
        registerForContextMenu(addedCitiesListView);

        // data's for broadcast and alarm
        IntentFilter intentFilter = new IntentFilter(MainIntentWorkForWeather.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        // add a new city and loading weather
        currentCitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            TextView textView;
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long number) {
                // user helpful
                textView = (TextView) pages.get(2).findViewById(R.id.city);
                if (textView.getText().toString().equals(currentCities.get(index).get(KEY_CITY)) ||
                    textView.getText().toString().equals("Пролистав влево, выберете интересующий город из списка")) {
                    deleteWeatherFromPages();
                }

                // delete all data's if such city there is
                weatherDataBase.deleteWeatherInCity(currentCities.get(index).get(KEY_CITY), currentCities.get(index).get(KEY_COUNTRY));

                // Loading weather
                isWasBroadcast = true;
                isDialog = true;
                dialog = ProgressDialog.show(MyActivity.this, "Загрузка погоды. Пожалуйста, подождите...", null, true);
                dialog.show();
                String urlAdress = URL_ADRESS + currentCitiesID.get(index).toString() + ".xml";
                Intent intentMyIntentService = new Intent(MyActivity.this, MainIntentWorkForWeather.class);
                intentMyIntentService.putExtra(KEY_FOR_WEATHER_INTENT, urlAdress);
                intentMyIntentService.putExtra(KEY_CITY, currentCities.get(index).get(KEY_CITY));
                intentMyIntentService.putExtra(KEY_COUNTRY, currentCities.get(index).get(KEY_COUNTRY));
                intentMyIntentService.putExtra(KEY_YANDEX_ID, currentCitiesID.get(index).toString());
                startService(intentMyIntentService);

                // clearing bad information
                EditText editText = (EditText) pages.get(0).findViewById(R.id.enter_city); editText.setText("");
                textView = (TextView) pages.get(1).findViewById(R.id.textView);  textView.setText("Мои города");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                viewPager.setCurrentItem(1);
            }
        });

        // hearing click on keyboard
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                printWithSuchPrefix(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        EditText editText = (EditText) pages.get(0).findViewById(R.id.enter_city);
        editText.addTextChangedListener(textWatcher);

        // alarm manager
        Intent intent = new Intent(MyActivity.this, Updater.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 60 * 1000,   60 * 60 * 1000, pendingIntent); // every hour
        Log.d("Alaram", "" + System.currentTimeMillis());
    }

    public static final int IDM_DELETE = 102;
    public static final int IDM_REFRESH = 103;

    int pozition = -1;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        pozition = aMenuInfo.position;
        menu.add(Menu.NONE, IDM_DELETE, Menu.NONE, "Удалить");
        menu.add(Menu.NONE, IDM_REFRESH, Menu.NONE, "Обновить погоду");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        TextView textView;
        switch (item.getItemId())
        {
            case IDM_DELETE:
                textView = (TextView) pages.get(2).findViewById(R.id.city);            // watching city name on page with MAIN WEATHER
                if (textView.getText().toString().equals(addedCities.get(pozition).get(KEY_CITY))) {
                    deleteWeatherFromPages();                                                   // delete weather, which we should delete now from all pages
                }
                if (addedCities.size() == 1) {
                    textView = (TextView) pages.get(1).findViewById(R.id.textView);
                    textView.setText("Пролистав влево, выберете интересующий город из списка");
                    textView = (TextView) pages.get(2).findViewById(R.id.city);
                    textView.setText("Пролистав влево, выберете интересующий город из списка");
                    textView = (TextView) pages.get(3).findViewById(R.id.city);
                    textView.setText("Пролистав влево, выберете интересующий город из списка");
                    textView = (TextView) pages.get(4).findViewById(R.id.city);
                    textView.setText("Пролистав влево, выберете интересующий город из списка");
                    textView = (TextView) pages.get(5).findViewById(R.id.city);
                    textView.setText("Пролистав влево, выберете интересующий город из списка");
                }
                weatherDataBase.deleteWeatherInCity(addedCities.get(pozition).get(KEY_CITY), addedCities.get(pozition).get(KEY_COUNTRY)); // delete from data base this city
                printCityFromTable();
                break;
            case IDM_REFRESH:
                textView = (TextView) pages.get(2).findViewById(R.id.city);            // watching city name on page with MAIN WEATHER
                if (textView.getText().toString().equals(addedCities.get(pozition).get(KEY_CITY))) {
                    deleteWeatherFromPages();                                                   // delete weather, which we should delete now from all pages
                }
                String city_from_table = addedCities.get(pozition).get(KEY_CITY);
                String country_from_table = addedCities.get(pozition).get(KEY_COUNTRY);
                String yandex_id_from_table = addedCitiesID.get(pozition).toString();
                weatherDataBase.deleteWeatherInCity(addedCities.get(pozition).get(KEY_CITY), addedCities.get(pozition).get(KEY_COUNTRY));

                IntentFilter intentFilter = new IntentFilter(MainIntentWorkForWeather.ACTION_MyIntentService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(myBroadcastReceiver, intentFilter);

                isWasBroadcast = true;
                isDialog = true;
                dialog = ProgressDialog.show(MyActivity.this, "Загрузка погоды. Пожалуйста, подождите...", null, true);
                dialog.show();
                String urlAdress = URL_ADRESS + yandex_id_from_table + ".xml";
                Intent intentMyIntentService = new Intent(MyActivity.this, MainIntentWorkForWeather.class);
                intentMyIntentService.putExtra(KEY_FOR_WEATHER_INTENT, urlAdress);
                intentMyIntentService.putExtra(KEY_CITY, city_from_table);
                intentMyIntentService.putExtra(KEY_COUNTRY, country_from_table);
                intentMyIntentService.putExtra(KEY_YANDEX_ID, yandex_id_from_table);
                startService(intentMyIntentService);
                flagForContext = true;
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }


// register broadcast after intent
    boolean flagForContext = false;
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isDialog == false || flagForContext == true) {
                flagForContext = false;
                deleteWeatherFromPages();
                printMainWeather(KEY_LAST);
                printOtherWeather(3, KEY_LAST);
                printOtherWeather(4, KEY_LAST);
                printOtherWeather(5, KEY_LAST);
                viewPager.setCurrentItem(2);
            }
            printCityFromTable();
            if (isDialog == true) dialog.dismiss();
            isDialog = false;
        }
    }

    private void deleteWeatherFromPages() {
        for (int index = 3; index <= 5; ++index) {
            // morning
            TextView temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature);
            temperature_print.setText("");
            TextView city_print = (TextView) pages.get(index).findViewById(R.id.city);
            city_print.setText("");
            TextView data_print = (TextView) pages.get(index).findViewById(R.id.data);
            data_print.setText("");
            TextView description_print = (TextView) pages.get(index).findViewById(R.id.description);
            description_print.setText("");
            TextView humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity);
            humimdity_print.setText("");
            TextView pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure);
            pressure_print.setText("");
            TextView wind_print = (TextView) pages.get(index).findViewById(R.id.wind);
            wind_print.setText("");
            imageView = (ImageView) pages.get(index).findViewById(R.id.imageView);
            imageView.setImageBitmap(null);

            // day
            temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature1);
            temperature_print.setText("");
            data_print = (TextView) pages.get(index).findViewById(R.id.data1);
            data_print.setText("");
            description_print = (TextView) pages.get(index).findViewById(R.id.description1);
            description_print.setText("");
            humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity1);
            humimdity_print.setText("");
            pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure1);
            pressure_print.setText("");
            wind_print = (TextView) pages.get(index).findViewById(R.id.wind1);
            wind_print.setText("");
            imageView = (ImageView) pages.get(index).findViewById(R.id.imageView1);
            imageView.setImageBitmap(null);

            // evening
            temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature2);
            temperature_print.setText("");
            data_print = (TextView) pages.get(index).findViewById(R.id.data2);
            data_print.setText("");
            description_print = (TextView) pages.get(index).findViewById(R.id.description2);
            description_print.setText("");
            humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity2);
            humimdity_print.setText("");
            pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure2);
            pressure_print.setText("");
            wind_print = (TextView) pages.get(index).findViewById(R.id.wind2);
            wind_print.setText("");
            imageView = (ImageView) pages.get(index).findViewById(R.id.imageView2);
            imageView.setImageBitmap(null);
        }

        // main weather
        int index = 2;
        TextView temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature);
        temperature_print.setText("");
        TextView city_print = (TextView) pages.get(index).findViewById(R.id.city);
        city_print.setText("");
        TextView country_print = (TextView) pages.get(index).findViewById(R.id.country);
        country_print.setText("");
        TextView lust_update_print = (TextView) pages.get(index).findViewById(R.id.lust_update);
        lust_update_print.setText("");
        TextView description_print = (TextView) pages.get(index).findViewById(R.id.description);
        description_print.setText("");
        TextView humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity);
        humimdity_print.setText("");
        TextView pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure);
        pressure_print.setText("");
        TextView wind_print = (TextView) pages.get(index).findViewById(R.id.wind);
        wind_print.setText("");
        imageView = (ImageView) pages.get(index).findViewById(R.id.imageView);
        imageView.setImageBitmap(null);

        TextView textView = (TextView) pages.get(2).findViewById(R.id.city);
        textView.setText("Выберете город из ВАШЕГО списка для отображения погоды");
        textView = (TextView) pages.get(3).findViewById(R.id.city);
        textView.setText("Выберете город из ВАШЕГО списка для отображения погоды");
        textView = (TextView) pages.get(4).findViewById(R.id.city);
        textView.setText("Выберете город из ВАШЕГО списка для отображения погоды");
        textView = (TextView) pages.get(5).findViewById(R.id.city);
        textView.setText("Выберете город из ВАШЕГО списка для отображения погоды");
    }

    // print weather on tomorrow, after-tomorrow, after-after-tomorrow
    private void printOtherWeather(int index, String mainCity) {
        boolean isEmpty = true;
        Cursor cursor = weatherDataBase.sqLiteDatabase.query(WeatherDataBase.TABLE_WEATHER_NAME, new String[] {
                WeatherDataBase.ID_WEATHER, WeatherDataBase.CITY, WeatherDataBase.COUNTRY, WeatherDataBase.YANDEX_ID,
                WeatherDataBase.TEMPERATURE, WeatherDataBase.DESCRIPTION, WeatherDataBase.PRESSURE, WeatherDataBase.WIND_DIRECTION,
                WeatherDataBase.WIND_SPEED, WeatherDataBase.HUMIDITY, WeatherDataBase.DAY_PART, WeatherDataBase.WEATHER_NOW, WeatherDataBase.DATA},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        while (cursor.moveToNext()) {
            String weather_now_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_NOW));
            if (weather_now_from_table.equals(KEY_YES)) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty == true) return;

        // weahter data base not empty
        cursor.moveToLast();
        while (cursor.moveToPrevious()) {
            String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
            String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
            String weather_now_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_NOW));

            if (day_part_from_table.equals(KEY_NOW)) {
                if (mainCity.equals(KEY_LAST)) {
                    if (weather_now_from_table.equals(KEY_YES))
                        break;
                } else {
                    if (city_from_table.equals(mainCity))
                        break;
                }
            }
        }
        int stop = 1;
        while (cursor.moveToNext()) {
            String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
            if (day_part_from_table.equals("morning")) {
                ++stop;
                if (stop == index) break; // find weather on index - 2 day later
            }
        }
        // morning
        String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
        String temperature_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TEMPERATURE));
        String description_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DESCRIPTION));
        String pressure_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PRESSURE));
        String humimdity_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.HUMIDITY));
        String wind_direction_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_DIRECTION));
        String wind_speed_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_SPEED));
        String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
        String data_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DATA));

        TextView temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature);
        TextView city_print = (TextView) pages.get(index).findViewById(R.id.city);
        TextView data_print = (TextView) pages.get(index).findViewById(R.id.data);
        TextView description_print = (TextView) pages.get(index).findViewById(R.id.description);
        TextView humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity);
        TextView pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure);
        TextView wind_print = (TextView) pages.get(index).findViewById(R.id.wind);
        imageView = (ImageView) pages.get(index).findViewById(R.id.imageView);
        city_print.setText(city_from_table);
            // part_od_day
        if (day_part_from_table.equals("morning")) day_part_from_table = "Утро";
        else if (day_part_from_table.equals("day")) day_part_from_table = "День";
        else if (day_part_from_table.equals("evening")) day_part_from_table = "Вечер";
            // data
        data_print.setText(data_from_table + "   " + day_part_from_table);
            // weather icon
        if (day_part_from_table.equals("Вечер")) day_part_from_table = "ночь";
        else day_part_from_table = "день";
        setIcon(description_from_table, day_part_from_table);

        if (Integer.parseInt(temperature_from_table) >= 30) {
            imageView.setImageResource(R.drawable.hot);
            temperature_print.setTextColor(0xFFFF0000);
        }
        else if (Integer.parseInt(temperature_from_table) >= 25) temperature_print.setTextColor(0xFFFF7C3E);
        else if (Integer.parseInt(temperature_from_table) >= 20) temperature_print.setTextColor(0xFFFF5F3F);
        else if (Integer.parseInt(temperature_from_table) >= 15) temperature_print.setTextColor(0xFFFF786F);
        else if (Integer.parseInt(temperature_from_table) >= 10) temperature_print.setTextColor(0xFFFFCD9B);
        else if (Integer.parseInt(temperature_from_table) >= 5) temperature_print.setTextColor(0xFFFFF2C3);
        else if (Integer.parseInt(temperature_from_table) >= 0) temperature_print.setTextColor(0xFFFFFAD9);
        else if (Integer.parseInt(temperature_from_table) >= -5) temperature_print.setTextColor(0xFFEAF0FF);
        else if (Integer.parseInt(temperature_from_table) >= -10) temperature_print.setTextColor(0xFFD2F1FF);
        else if (Integer.parseInt(temperature_from_table) >= -15) temperature_print.setTextColor(0xFF9DEBFF);
        else if (Integer.parseInt(temperature_from_table) >= -20) temperature_print.setTextColor(0xFF76DDFF);
        else if (Integer.parseInt(temperature_from_table) >= -25) temperature_print.setTextColor(0xFF40A5FF);
        else if (Integer.parseInt(temperature_from_table) >= -30) temperature_print.setTextColor(0xFF0AA5FF);
        else {
            imageView.setImageResource(R.drawable.verycold);
            temperature_print.setTextColor(0xFF0027FF);
        }

            // other information
        temperature_print.setText(temperature_from_table + "°C");
        description_print.setText(description_from_table);
        humimdity_print.setText("Влажность: " + humimdity_from_table + "%");
        pressure_print.setText("Давление: " + pressure_from_table + " мм");
        wind_print.setText("Ветер: " + wind_speed_from_table + " м/c, ");
            // wind_direction in Russian
        if (wind_direction_from_table.equals("n")) wind_print.setText(wind_print.getText().toString() + "северный");
        else if (wind_direction_from_table.equals("ne")) wind_print.setText(wind_print.getText().toString() + "северно-восточный");
        else if (wind_direction_from_table.equals("e")) wind_print.setText(wind_print.getText().toString() + "восточный");
        else if (wind_direction_from_table.equals("se")) wind_print.setText(wind_print.getText().toString() + "юго-восточный");
        else if (wind_direction_from_table.equals("s")) wind_print.setText(wind_print.getText().toString() + "южный");
        else if (wind_direction_from_table.equals("sw")) wind_print.setText(wind_print.getText().toString() + "юго-западный");
        else if (wind_direction_from_table.equals("w")) wind_print.setText(wind_print.getText().toString() + "западный");
        else if (wind_direction_from_table.equals("nw")) wind_print.setText(wind_print.getText().toString() + "северо-западный");

        // day
        cursor.moveToNext();
        temperature_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TEMPERATURE));
        description_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DESCRIPTION));
        pressure_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PRESSURE));
        humimdity_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.HUMIDITY));
        wind_direction_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_DIRECTION));
        wind_speed_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_SPEED));
        day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
        data_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DATA));

        temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature1);
        data_print = (TextView) pages.get(index).findViewById(R.id.data1);
        description_print = (TextView) pages.get(index).findViewById(R.id.description1);
        humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity1);
        pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure1);
        wind_print = (TextView) pages.get(index).findViewById(R.id.wind1);
        imageView = (ImageView) pages.get(index).findViewById(R.id.imageView1);
            // part of day
        if (day_part_from_table.equals("morning")) day_part_from_table = "Утро";
        if (day_part_from_table.equals("day")) day_part_from_table = "День";
        if (day_part_from_table.equals("evening")) day_part_from_table = "Вечер";
            // data
        data_print.setText(data_from_table + "   " + day_part_from_table);
            // weather icon
        if (day_part_from_table.equals("Вечер")) day_part_from_table = "ночь";
        else day_part_from_table = "день";
        setIcon(description_from_table, day_part_from_table);

        if (Integer.parseInt(temperature_from_table) >= 30) {
            imageView.setImageResource(R.drawable.hot);
            temperature_print.setTextColor(0xFFFF0000);
        }
        else if (Integer.parseInt(temperature_from_table) >= 25) temperature_print.setTextColor(0xFFFF7C3E);
        else if (Integer.parseInt(temperature_from_table) >= 20) temperature_print.setTextColor(0xFFFF5F3F);
        else if (Integer.parseInt(temperature_from_table) >= 15) temperature_print.setTextColor(0xFFFF786F);
        else if (Integer.parseInt(temperature_from_table) >= 10) temperature_print.setTextColor(0xFFFFCD9B);
        else if (Integer.parseInt(temperature_from_table) >= 5) temperature_print.setTextColor(0xFFFFF2C3);
        else if (Integer.parseInt(temperature_from_table) >= 0) temperature_print.setTextColor(0xFFFFFAD9);
        else if (Integer.parseInt(temperature_from_table) >= -5) temperature_print.setTextColor(0xFFEAF0FF);
        else if (Integer.parseInt(temperature_from_table) >= -10) temperature_print.setTextColor(0xFFD2F1FF);
        else if (Integer.parseInt(temperature_from_table) >= -15) temperature_print.setTextColor(0xFF9DEBFF);
        else if (Integer.parseInt(temperature_from_table) >= -20) temperature_print.setTextColor(0xFF76DDFF);
        else if (Integer.parseInt(temperature_from_table) >= -25) temperature_print.setTextColor(0xFF40A5FF);
        else if (Integer.parseInt(temperature_from_table) >= -30) temperature_print.setTextColor(0xFF0AA5FF);
        else {
            imageView.setImageResource(R.drawable.verycold);
            temperature_print.setTextColor(0xFF0027FF);
        }

            // other information
        temperature_print.setText(temperature_from_table + "°C");
        description_print.setText(description_from_table);
        humimdity_print.setText("Влажность: " + humimdity_from_table + "%");
        pressure_print.setText("Давление: " + pressure_from_table + " мм");
        wind_print.setText("Ветер: " + wind_speed_from_table + " м/c, ");
            // wind direction in Russian
        if (wind_direction_from_table.equals("n")) wind_print.setText(wind_print.getText().toString() + "северный");
        else if (wind_direction_from_table.equals("ne")) wind_print.setText(wind_print.getText().toString() + "северно-восточный");
        else if (wind_direction_from_table.equals("e")) wind_print.setText(wind_print.getText().toString() + "восточный");
        else if (wind_direction_from_table.equals("se")) wind_print.setText(wind_print.getText().toString() + "юго-восточный");
        else if (wind_direction_from_table.equals("s")) wind_print.setText(wind_print.getText().toString() + "южный");
        else if (wind_direction_from_table.equals("sw")) wind_print.setText(wind_print.getText().toString() + "юго-западный");
        else if (wind_direction_from_table.equals("w")) wind_print.setText(wind_print.getText().toString() + "западный");
        else if (wind_direction_from_table.equals("nw")) wind_print.setText(wind_print.getText().toString() + "северо-западный");

        // evening
        cursor.moveToNext();
        temperature_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TEMPERATURE));
        description_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DESCRIPTION));
        pressure_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PRESSURE));
        humimdity_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.HUMIDITY));
        wind_direction_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_DIRECTION));
        wind_speed_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_SPEED));
        day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
        data_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DATA));

        temperature_print = (TextView) pages.get(index).findViewById(R.id.temperature2);
        data_print = (TextView) pages.get(index).findViewById(R.id.data2);
        description_print = (TextView) pages.get(index).findViewById(R.id.description2);
        humimdity_print = (TextView) pages.get(index).findViewById(R.id.humidity2);
        pressure_print = (TextView) pages.get(index).findViewById(R.id.pressure2);
        wind_print = (TextView) pages.get(index).findViewById(R.id.wind2);
        imageView = (ImageView) pages.get(index).findViewById(R.id.imageView2);
            // day_part
        if (day_part_from_table.equals("morning")) day_part_from_table = "Утро";
        if (day_part_from_table.equals("day")) day_part_from_table = "День";
        if (day_part_from_table.equals("evening")) day_part_from_table = "Вечер";
            // data
        data_print.setText(data_from_table + "   " + day_part_from_table);
            // weather icon
        if (day_part_from_table.equals("Вечер")) day_part_from_table = "ночь";
        else day_part_from_table = "день";
        setIcon(description_from_table, day_part_from_table);
            // other information
        if (Integer.parseInt(temperature_from_table) >= 30) {
            imageView.setImageResource(R.drawable.hot);
            temperature_print.setTextColor(0xFFFF0000);
        }
        else if (Integer.parseInt(temperature_from_table) >= 25) temperature_print.setTextColor(0xFFFF7C3E);
        else if (Integer.parseInt(temperature_from_table) >= 20) temperature_print.setTextColor(0xFFFF5F3F);
        else if (Integer.parseInt(temperature_from_table) >= 15) temperature_print.setTextColor(0xFFFF786F);
        else if (Integer.parseInt(temperature_from_table) >= 10) temperature_print.setTextColor(0xFFFFCD9B);
        else if (Integer.parseInt(temperature_from_table) >= 5) temperature_print.setTextColor(0xFFFFF2C3);
        else if (Integer.parseInt(temperature_from_table) >= 0) temperature_print.setTextColor(0xFFFFFAD9);
        else if (Integer.parseInt(temperature_from_table) >= -5) temperature_print.setTextColor(0xFFEAF0FF);
        else if (Integer.parseInt(temperature_from_table) >= -10) temperature_print.setTextColor(0xFFD2F1FF);
        else if (Integer.parseInt(temperature_from_table) >= -15) temperature_print.setTextColor(0xFF9DEBFF);
        else if (Integer.parseInt(temperature_from_table) >= -20) temperature_print.setTextColor(0xFF76DDFF);
        else if (Integer.parseInt(temperature_from_table) >= -25) temperature_print.setTextColor(0xFF40A5FF);
        else if (Integer.parseInt(temperature_from_table) >= -30) temperature_print.setTextColor(0xFF0AA5FF);
        else {
            imageView.setImageResource(R.drawable.verycold);
            temperature_print.setTextColor(0xFF0027FF);
        }


        temperature_print.setText(temperature_from_table + "°C");
        description_print.setText(description_from_table);
        humimdity_print.setText("Влажность: " + humimdity_from_table + "%");
        pressure_print.setText("Давление: " + pressure_from_table + " мм");
        wind_print.setText("Ветер: " + wind_speed_from_table + " м/c, ");
            // wind_direction in Russian
        if (wind_direction_from_table.equals("n")) wind_print.setText(wind_print.getText().toString() + "северный");
        else if (wind_direction_from_table.equals("ne")) wind_print.setText(wind_print.getText().toString() + "северно-восточный");
        else if (wind_direction_from_table.equals("e")) wind_print.setText(wind_print.getText().toString() + "восточный");
        else if (wind_direction_from_table.equals("se")) wind_print.setText(wind_print.getText().toString() + "юго-восточный");
        else if (wind_direction_from_table.equals("s")) wind_print.setText(wind_print.getText().toString() + "южный");
        else if (wind_direction_from_table.equals("sw")) wind_print.setText(wind_print.getText().toString() + "юго-западный");
        else if (wind_direction_from_table.equals("w")) wind_print.setText(wind_print.getText().toString() + "западный");
        else if (wind_direction_from_table.equals("nw")) wind_print.setText(wind_print.getText().toString() + "северо-западный");
    }

    private void printMainWeather(String mainCity) {
        changeFlags(mainCity);
        boolean isEmpty = true;
        Cursor cursor = weatherDataBase.sqLiteDatabase.query(WeatherDataBase.TABLE_WEATHER_NAME, new String[] {
                WeatherDataBase.ID_WEATHER, WeatherDataBase.CITY, WeatherDataBase.COUNTRY, WeatherDataBase.YANDEX_ID,
                WeatherDataBase.TEMPERATURE, WeatherDataBase.DESCRIPTION, WeatherDataBase.PRESSURE, WeatherDataBase.WIND_DIRECTION,
                WeatherDataBase.WIND_SPEED, WeatherDataBase.HUMIDITY, WeatherDataBase.DAY_PART, WeatherDataBase.WEATHER_NOW, WeatherDataBase.DATA},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        while (cursor.moveToNext()) {
            String weather_now_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_NOW));
            String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));

            if (weather_now_from_table.equals(KEY_YES)) {
                isEmpty = false;
                CITY_YES = city_from_table;
                break;
            }
        }
        if (isEmpty == true) return;

            // weather table not empty
        cursor.moveToLast();
        while (cursor.moveToPrevious()) {
            String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
            String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
            String weather_now_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_NOW));

            if (day_part_from_table.equals(KEY_NOW)) {
                if (mainCity.equals(KEY_LAST)) {
                    if (weather_now_from_table.equals(KEY_YES))
                        break;
                } else {
                    if (city_from_table.equals(mainCity))
                        break;
                }
            }
        }
        String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
        String country_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.COUNTRY));
        String temperature_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TEMPERATURE));
        String description_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DESCRIPTION));
        String pressure_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PRESSURE));
        String humimdity_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.HUMIDITY));
        String wind_direction_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_DIRECTION));
        String wind_speed_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WIND_SPEED));
        String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));
        String data_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DATA));

        TextView temperature_print = (TextView) pages.get(2).findViewById(R.id.temperature);
        TextView city_print = (TextView) pages.get(2).findViewById(R.id.city);
        TextView country_print = (TextView) pages.get(2).findViewById(R.id.country);
        TextView lust_update_print = (TextView) pages.get(2).findViewById(R.id.lust_update);
        TextView description_print = (TextView) pages.get(2).findViewById(R.id.description);
        TextView humimdity_print = (TextView) pages.get(2).findViewById(R.id.humidity);
        TextView pressure_print = (TextView) pages.get(2).findViewById(R.id.pressure);
        TextView wind_print = (TextView) pages.get(2).findViewById(R.id.wind);
        imageView = (ImageView) pages.get(2).findViewById(R.id.imageView);

        city_print.setText(city_from_table);
        country_print.setText(country_from_table);
        lust_update_print.setText("Последнее обновление: " + data_from_table.substring(0, data_from_table.indexOf("T")) +
                " " + data_from_table.substring(data_from_table.indexOf("T") + 1, data_from_table.length()));

        day_part_from_table = data_from_table.substring(data_from_table.indexOf("T") + 1, data_from_table.length());
        day_part_from_table = day_part_from_table.substring(0, day_part_from_table.indexOf(":"));
        int temp = Integer.parseInt(day_part_from_table);
        if (temp >= 20 || temp <=7) day_part_from_table = "ночь";
        else day_part_from_table = "день";
        setIcon(description_from_table, day_part_from_table);

        if (Integer.parseInt(temperature_from_table) >= 30) {
            imageView.setImageResource(R.drawable.hot);
            temperature_print.setTextColor(0xFFFF0000);
        }
        else if (Integer.parseInt(temperature_from_table) >= 25) temperature_print.setTextColor(0xFFFF7C3E);
        else if (Integer.parseInt(temperature_from_table) >= 20) temperature_print.setTextColor(0xFFFF5F3F);
        else if (Integer.parseInt(temperature_from_table) >= 15) temperature_print.setTextColor(0xFFFF786F);
        else if (Integer.parseInt(temperature_from_table) >= 10) temperature_print.setTextColor(0xFFFFCD9B);
        else if (Integer.parseInt(temperature_from_table) >= 5) temperature_print.setTextColor(0xFFFFF2C3);
        else if (Integer.parseInt(temperature_from_table) >= 0) temperature_print.setTextColor(0xFFFFFAD9);
        else if (Integer.parseInt(temperature_from_table) >= -5) temperature_print.setTextColor(0xFFEAF0FF);
        else if (Integer.parseInt(temperature_from_table) >= -10) temperature_print.setTextColor(0xFFD2F1FF);
        else if (Integer.parseInt(temperature_from_table) >= -15) temperature_print.setTextColor(0xFF9DEBFF);
        else if (Integer.parseInt(temperature_from_table) >= -20) temperature_print.setTextColor(0xFF76DDFF);
        else if (Integer.parseInt(temperature_from_table) >= -25) temperature_print.setTextColor(0xFF40A5FF);
        else if (Integer.parseInt(temperature_from_table) >= -30) temperature_print.setTextColor(0xFF0AA5FF);
        else {
            imageView.setImageResource(R.drawable.verycold);
            temperature_print.setTextColor(0xFF0027FF);
        }

        temperature_print.setText(temperature_from_table + "°C");
        description_print.setText(description_from_table);
        humimdity_print.setText("Влажность: " + humimdity_from_table + "%");
        pressure_print.setText("Давление: " + pressure_from_table + " мм");
        wind_print.setText("Ветер: " + wind_speed_from_table + " м/c");

        if (wind_direction_from_table.equals("n")) wind_print.setText(wind_print.getText().toString() + "\nСеверный");
        else if (wind_direction_from_table.equals("ne")) wind_print.setText(wind_print.getText().toString() + "\nСеверно-восточный");
        else if (wind_direction_from_table.equals("e")) wind_print.setText(wind_print.getText().toString() + "\nВосточный");
        else if (wind_direction_from_table.equals("se")) wind_print.setText(wind_print.getText().toString() + "\nЮго-восточный");
        else if (wind_direction_from_table.equals("s")) wind_print.setText(wind_print.getText().toString() + "\nЮжный");
        else if (wind_direction_from_table.equals("sw")) wind_print.setText(wind_print.getText().toString() + "\nЮго-западный");
        else if (wind_direction_from_table.equals("w")) wind_print.setText(wind_print.getText().toString() + "\nЗападный");
        else if (wind_direction_from_table.equals("nw")) wind_print.setText(wind_print.getText().toString() + "\nСеверо-западный");
    }

    private void changeFlags(String city) {
        if (city.equals(KEY_LAST)) return;
        Cursor cursor = weatherDataBase.sqLiteDatabase.query(WeatherDataBase.TABLE_WEATHER_NAME, new String[] {
                WeatherDataBase.ID_WEATHER, WeatherDataBase.CITY, WeatherDataBase.COUNTRY, WeatherDataBase.YANDEX_ID,
                WeatherDataBase.TEMPERATURE, WeatherDataBase.DESCRIPTION, WeatherDataBase.PRESSURE, WeatherDataBase.WIND_DIRECTION,
                WeatherDataBase.WIND_SPEED, WeatherDataBase.HUMIDITY, WeatherDataBase.DAY_PART, WeatherDataBase.WEATHER_NOW, WeatherDataBase.DATA},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        while (cursor.moveToNext()) {
            String weather_now_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_NOW));
            String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
            String day_part_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.DAY_PART));

            if (weather_now_from_table.equals(KEY_YES)) {
                weatherDataBase.changeYesOrNo(cursor.getString(cursor.getColumnIndex(WeatherDataBase.ID_WEATHER)), KEY_NO);
            }
            if (city_from_table.equals(city) == true && day_part_from_table.equals(KEY_NOW) == true) {
                weatherDataBase.changeYesOrNo(cursor.getString(cursor.getColumnIndex(WeatherDataBase.ID_WEATHER)), KEY_YES);
            }
        }
    }

    private void setIcon(String wind_direction_from_table, String day_part_from_table) {
        if (wind_direction_from_table.equals("облачно, небольшой дождь"))  imageView.setImageResource(R.drawable.lightrain);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.indexOf("гроза") != -1) imageView.setImageResource(R.drawable.sunnyelectricrain);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.indexOf("гроза") != -1) imageView.setImageResource(R.drawable.moonrainelectric);
        else if (wind_direction_from_table.equals("облачно")) imageView.setImageResource(R.drawable.mediumcloudy);
        else if (wind_direction_from_table.equals("облачно, временами дождь")) imageView.setImageResource(R.drawable.lightrain);
        else if (wind_direction_from_table.equals("облачно, дождь")) imageView.setImageResource(R.drawable.rain);
        else if (wind_direction_from_table.equals("облачно, временами дождь со снегом")) imageView.setImageResource(R.drawable.grad);
        else if (wind_direction_from_table.equals("облачно, дождь со снегом")) imageView.setImageResource(R.drawable.mediumcloudy);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.equals("малооблачно")) imageView.setImageResource(R.drawable.sunnyhardcloudy);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.equals("малооблачно")) imageView.setImageResource(R.drawable.moonhighcloudy);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.equals("облачно с прояснениями")) imageView.setImageResource(R.drawable.moonclody);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.equals("облачно с прояснениями")) imageView.setImageResource(R.drawable.sunnylightcloudy);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.equals("ясно")) imageView.setImageResource(R.drawable.moon);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.equals("ясно")) imageView.setImageResource(R.drawable.sunny);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.equals("переменная облачность, небольшой дождь")) imageView.setImageResource(R.drawable.moonrain);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.equals("переменная облачность, небольшой дождь")) imageView.setImageResource(R.drawable.lightcloudy);
        else if (day_part_from_table.equals("ночь") && wind_direction_from_table.equals("облачно с прояснениями, небольшой снег")) imageView.setImageResource(R.drawable.moonsnow);
        else if (day_part_from_table.equals("день") && wind_direction_from_table.equals("облачно с прояснениями, небольшой снег")) imageView.setImageResource(R.drawable.lightsnowy);
        else if (wind_direction_from_table.equals("облачно, небольшой снег")) imageView.setImageResource(R.drawable.lightsnowy);
        else if (wind_direction_from_table.equals("переменная облачность, возможен дождь, гроза")) imageView.setImageResource(R.drawable.hardcloudy);
        else if (wind_direction_from_table.indexOf("смог") != -1 || wind_direction_from_table.indexOf("туман") != -1) imageView.setImageResource(R.drawable.lightcloudy);
        else if (wind_direction_from_table.indexOf("град") != -1) imageView.setImageResource(R.drawable.grad);
        else if (wind_direction_from_table.indexOf("ветрено") != -1 || wind_direction_from_table.indexOf("ветер") != -1) imageView.setImageResource(R.drawable.windy);
        else if (wind_direction_from_table.indexOf("дождь") != -1 && wind_direction_from_table.indexOf("сильный") != -1 ) imageView.setImageResource(R.drawable.sunnyrain);
        else if (wind_direction_from_table.indexOf("жар") != -1) imageView.setImageResource(R.drawable.hot);
        else imageView.setImageResource(R.drawable.mediumcloudy);
    }

    private void printCityFromTable() {
        addedCitiesID = new ArrayList<Integer>();
        addedCities = new ArrayList<HashMap<String, String>>();
        Cursor cursor = weatherDataBase.sqLiteDatabase.query(WeatherDataBase.TABLE_WEATHER_NAME, new String[] {
                WeatherDataBase.ID_WEATHER, WeatherDataBase.CITY, WeatherDataBase.COUNTRY, WeatherDataBase.YANDEX_ID,
                WeatherDataBase.TEMPERATURE, WeatherDataBase.DESCRIPTION, WeatherDataBase.PRESSURE, WeatherDataBase.WIND_DIRECTION,
                WeatherDataBase.WIND_SPEED, WeatherDataBase.HUMIDITY, WeatherDataBase.DAY_PART, WeatherDataBase.WEATHER_NOW, WeatherDataBase.DATA},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        while (cursor.moveToNext()) {
            String city_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY));
            String country_from_table = cursor.getString(cursor.getColumnIndex(WeatherDataBase.COUNTRY));
            int id_from_table = Integer.parseInt(cursor.getString(cursor.getColumnIndex(WeatherDataBase.YANDEX_ID)));
            HashMap <String, String> map = new HashMap<String, String>();
            map.put(KEY_CITY, city_from_table);
            map.put(KEY_COUNTRY, country_from_table);
            if (addedCities.contains(map) == false) {
                addedCities.add(map);
                addedCitiesID.add(id_from_table);
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(MyActivity.this, addedCities, R.layout.row, new String[] {KEY_CITY, KEY_COUNTRY}, new int[] {R.id.ColCity, R.id.ColCountry});
        addedCitiesListView.setAdapter(adapter);
    }

    // for input text on page 0
    private void printWithSuchPrefix(String prefix) {
        prefix = prefix.toLowerCase();
        currentCitiesID = new ArrayList<Integer>();
        currentCities = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < allCities.size(); ++i) {
            if (allCities.get(i).get(KEY_CITY).toLowerCase().indexOf(prefix) == 0) {
                currentCities.add(allCities.get(i));
                currentCitiesID.add(allCitiesID.get(i));
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(MyActivity.this, currentCities, R.layout.row, new String[] {KEY_CITY, KEY_COUNTRY}, new int[] {R.id.ColCity, R.id.ColCountry});
        currentCitiesListView.setAdapter(adapter);
    }

    // Open file and print all cities
    private void openFileAndPrintAllCities() {
        try {
            InputStream inputstream = getResources().openRawResource(R.raw.all_cities);
            if (inputstream != null) {
                InputStreamReader isr = new InputStreamReader(inputstream);
                BufferedReader reader = new BufferedReader(isr);
                String str;
                HashMap<String, String> map;
                StringBuffer buffer = new StringBuffer();

                while ((str = reader.readLine()) != null) {
                    buffer.append(str + "\n");
                    map = new HashMap<String, String>();
                    map.put(KEY_CITY, str.substring(str.indexOf("&lt;city&gt;") + 12, str.indexOf("&lt;/city&gt;")));
                    map.put(KEY_COUNTRY, str.substring(str.indexOf("&lt;country&gt;") + 15, str.indexOf("&lt;/country&gt;")));
                    allCitiesID.add(Integer.parseInt(str.substring(str.indexOf("&lt;id&gt;") + 10, str.indexOf("&lt;/id&gt;"))));
                    currentCitiesID.add(Integer.parseInt(str.substring(str.indexOf("&lt;id&gt;") + 10, str.indexOf("&lt;/id&gt;"))));
                    allCities.add(map);
                    currentCities.add(map);
                }
                SimpleAdapter adapter = new SimpleAdapter(MyActivity.this, allCities, R.layout.row, new String[] {KEY_CITY, KEY_COUNTRY}, new int[] {R.id.ColCity, R.id.ColCountry});
                currentCitiesListView.setAdapter(adapter);
                inputstream.close();
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // building View pager and several arrows
    private void buildingViewPages() {
        LayoutInflater inflater = LayoutInflater.from(this);
        pages = new ArrayList<View>();
        View page = inflater.inflate(R.layout.add_cities, null);
        pages.add(page);
        page = inflater.inflate(R.layout.main, null);
        pages.add(page);
        page = inflater.inflate(R.layout.weather_now, null);
        pages.add(page);
        page = inflater.inflate(R.layout.tomorrow, null);
        pages.add(page);
        page = inflater.inflate(R.layout.tomorrow_1, null);
        pages.add(page);
        page = inflater.inflate(R.layout.tomorrow_2, null);
        pages.add(page);
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        setContentView(viewPager);
        // arrows
        to_right = (ImageView) pages.get(0).findViewById(R.id.to_right);
        to_right.setImageResource(R.drawable.to_right);
        for (int i = 1; i <= 4; ++i) {
            to_left = (ImageView) pages.get(i).findViewById(R.id.to_left);
            to_right = (ImageView) pages.get(i).findViewById(R.id.to_right);
            to_left.setImageResource(R.drawable.to_left);
            to_right.setImageResource(R.drawable.to_right);
        }
        to_left = (ImageView) pages.get(5).findViewById(R.id.to_left);
        to_left.setImageResource(R.drawable.to_left);
    }

    // help to understand, what you should do when there is no add cities
    private void helpWhenAddCityIsEmpty() {
        TextView textView = (TextView) pages.get(1).findViewById(R.id.textView);
        textView.setText("Пролистав влево, выберете интересующий город из списка");
        textView = (TextView) pages.get(2).findViewById(R.id.city);
        textView.setText("Пролистав влево, выберете интересующий город из списка");
        textView = (TextView) pages.get(3).findViewById(R.id.city);
        textView.setText("Пролистав влево, выберете интересующий город из списка");
        textView = (TextView) pages.get(4).findViewById(R.id.city);
        textView.setText("Пролистав влево, выберете интересующий город из списка");
        textView = (TextView) pages.get(5).findViewById(R.id.city);
        textView.setText("Пролистав влево, выберете интересующий город из списка");
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onDestroy() {
        weatherDataBase.close();
        super.onDestroy();
        if (isWasBroadcast == true) {
            unregisterReceiver(myBroadcastReceiver);
            isWasBroadcast = false;
        }
    }
}
