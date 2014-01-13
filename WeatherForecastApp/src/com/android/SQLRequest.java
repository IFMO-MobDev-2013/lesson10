package com.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Ruslan
 * Date: 20.11.13
 * Time: 16:41
 */
public class SQLRequest {

    private static final String DataBaseName = "WeatherForecast";

    private static final String weatherTableName = "WeatherTable";
    private static final String keyTempNow = "tempNowKey";
    private static final String keyWeatherNow = "weatherNowKey";
    private static final String[] keyTempLow = {"tempLowKey1", "tempLowKey2", "tempLowKey3"};
    private static final String[] keyTempHigh = {"tempHighKey1", "tempHighKey2", "tempHighKey3"};
    private static final String[] keyWeatherThen = {"weatherThenKey1", "weatherThenKey2", "weatherThenKey3"};
    private static final String keyWeatherCodeNow = "keyWeatherCodeNow";
    private static final String[] keyWeatherCode = {"keyWeatherCode1", "keyWeatherCode2", "keyWeatherCode3"};
    private static final String keyId = "_id";
    private static final String keyCityId = "cityId";
    private static final String keyCountryName = "countryKey";
    private static final String keyCityName = "cityNameKey";
    private static final String keyDateNow = "dateKey";
    private static final String[] keyDate = {"date1Key", "date2Key", "date3Key"};
    private static final int Database_Version = 1;

    private boolean opened;
    private Context context;
    private SQLiteDatabase database;
    private DBHelper helper;


    private static final String weatherDataBaseCreator = "CREATE TABLE " + weatherTableName + " (" + keyId +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + keyCityName + " TEXT NOT NULL, " + keyCountryName + " TEXT NOT NULL, " +
            keyTempNow + " DOUBLE, " + keyWeatherNow + " TEXT NOT NULL, " + keyTempLow[0] + " DOUBLE, " +
            keyTempLow[1] + " DOUBLE, " + keyTempLow[2] + " DOUBLE, " + keyTempHigh[0] + " TEXT NOT NULL, " +
            keyTempHigh[1] + " TEXT NOT NULL, " + keyTempHigh[2] + " TEXT NOT NULL, " + keyWeatherThen[0] + " TEXT NOT NULL, " +
            keyWeatherThen[1] + " TEXT NOT NULL, " + keyWeatherThen[2] + " TEXT NOT NULL, " + keyDateNow + " TEXT NOT NULL, " +
            keyDate[0] + " TEXT NOT NULL, " + keyDate[1] + " TEXT NOT NULL, " + keyDate[2] + " TEXT NOT NULL, " +
            keyCityId + " INTEGER, " + keyWeatherCodeNow + " TEXT NOT NULL, " + keyWeatherCode[0] + " TEXT NOT NULL, " +
            keyWeatherCode[1] + " TEXT NOT NULL, " + keyWeatherCode[2] + " TEXT NOT NULL);";

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DataBaseName, null, Database_Version);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(weatherDataBaseCreator);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + weatherTableName);
        }
    }

    public SQLRequest(Context context) {
        this.context = context;
        this.opened = false;
    }

    public void openDB() {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
        opened = true;
    }

    public void closeDB() {
        helper.close();
        opened = false;
    }

    public void addCity(City city) {
        if (opened) {

            ContentValues values = new ContentValues();
            values.put(keyCityName, city.name);
            values.put(keyCityId, city.id);
            values.put(keyCountryName, city.country);
            values.put(keyTempNow, city.tempNow);
            values.put(keyWeatherNow, city.weatherNow);
            values.put(keyWeatherCodeNow, city.weatherNowCode);
            values.put(keyDateNow, city.date);
            for (int i = 0; i < 3; i++) {
                values.put(keyDate[i], city.dates[i]);
                values.put(keyTempLow[i], city.tempHigh[i]);
                values.put(keyWeatherThen[i], city.weather[i]);
                values.put(keyTempHigh[i], city.tempHigh[i]);
                values.put(keyTempLow[i], city.tempLow[i]);
                values.put(keyWeatherCode[i], city.weatherCode[i]);
            }
            deleteCity(city.id);
            database.insert(weatherTableName, null, values);
        }
    }


    public City getCityForecast(int id) {
        Cursor cursor = database.query(weatherTableName, null, keyCityId + "=" + id, null, null, null, null);
        cursor.moveToNext();
        String cityName = cursor.getString(cursor.getColumnIndex(keyCityName));
        int cityId = cursor.getInt(cursor.getColumnIndex(keyCityId));
        String countryName = cursor.getString(cursor.getColumnIndex(keyCountryName));
        City city = new City(cityName, countryName, cityId);
        city.tempNow = cursor.getDouble(cursor.getColumnIndex(keyTempNow));
        city.weatherNow = cursor.getString(cursor.getColumnIndex(keyWeatherNow));
        city.weatherNowCode = cursor.getInt(cursor.getColumnIndex(keyWeatherCodeNow));
        city.date = cursor.getString(cursor.getColumnIndex(keyDateNow));
        for (int i = 0; i < 3; i++) {
            city.dates[i] = cursor.getString(cursor.getColumnIndex(keyDate[i]));
            city.tempHigh[i] = cursor.getInt(cursor.getColumnIndex(keyTempHigh[i]));
            city.weather[i] = cursor.getString(cursor.getColumnIndex(keyWeatherThen[i]));
            city.tempLow[i] = cursor.getInt(cursor.getColumnIndex(keyTempLow[i]));
            city.weatherCode[i] = cursor.getInt(cursor.getColumnIndex(keyWeatherCode[i]));
        }
        return city;
    }

    public void deleteCity(int id) {
        if (opened) {
            database.delete(weatherTableName, keyCityId + "=?", new String[]{Integer.toString(id)});
        }
    }

    public ArrayList<City> getAllCities() {
        Cursor cursor;
        ArrayList<City> cities = new ArrayList<City>();
        if (opened) {
            cursor = database.query(weatherTableName, null, null, null, null, null, keyId + " asc", null);
            int size = cursor.getCount();
            for (int i = 0; i < size; ++i) {
                cursor.moveToNext();
                String cityName = cursor.getString(cursor.getColumnIndex(keyCityName));
                int cityId = cursor.getInt(cursor.getColumnIndex(keyCityId));
                String countryName = cursor.getString(cursor.getColumnIndex(keyCountryName));
                City city = new City(cityName, countryName, cityId);
                cities.add(city);
            }
        }
        return cities;
    }
}
