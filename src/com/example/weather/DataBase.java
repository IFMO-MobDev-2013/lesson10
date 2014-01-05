package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: elena
 * Date: 02.01.14
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;

    private static final String DATABASE_NAME = "weatherDataBase";

    private static final String TABLE_CITY = "cities";
    private static final String TABLE_WEATHER = "weather";

    private static final String KEY_ID = "id";

    //city table
    private static final String KEY_CITY = "city";
    private static final String KEY_CREATED_AT = "created_at";

    //weather table
    private static final String KEY_CITY_ID = "city_id";
    private static final String KEY_CLOUD_COVER = "cloud_cover";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_PRESSURE = "pressure";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEMP_C = "temp_C";
    private static final String KEY_TEMP_F = "temp_F";
    private static final String KEY_WEATHER_DESC = "weather_desc";
    private static final String KEY_ICON_URL = "icon_url";
    private static final String KEY_WIND_DIR = "wind_dir";
    private static final String KEY_WIND_SPEED = "wind_speed";
    private static final String KEY_TEMP_MIN_C = "temp_min_C";
    private static final String KEY_TEMP_MAX_C = "temp_max_C";
    private static final String KEY_IMAGE = "image";

    private static final String CREATE_TABLE_CITY = "CREATE TABLE "
            + TABLE_CITY + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CITY + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    private static final String CREATE_TABLE_WEATHER = "CREATE TABLE "
            + TABLE_WEATHER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CITY_ID + " INTEGER," + KEY_CLOUD_COVER + " INTEGER,"
            + KEY_HUMIDITY + " REAL," + KEY_PRESSURE + " INTEGER,"
            + KEY_DATE + " DATETIME,"
            + KEY_TEMP_C + " INTEGER," + KEY_TEMP_F + " INTEGER,"
            + KEY_WEATHER_DESC + " TEXT," + KEY_ICON_URL + " TEXT,"
            + KEY_WIND_DIR + " INTEGER," + KEY_WIND_SPEED + " INTEGER,"
            + KEY_TEMP_MIN_C + " INTEGER," + KEY_TEMP_MAX_C + " INTEGER,"
            + KEY_IMAGE + " BLOB" + ")";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);

        onCreate(db);
    }

    public long createCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CITY, city.getCity());
        values.put(KEY_CREATED_AT, getDateTime());

        long todo_id = db.insert(TABLE_CITY, null, values);

        return todo_id;
    }

    public City getCity(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CITY + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        City city = new City();
        city.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        city.setCity((c.getString(c.getColumnIndex(KEY_CITY))));
        city.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

        return city;
    }

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CITY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                City city = new City();
                city.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                city.setCity((c.getString(c.getColumnIndex(KEY_CITY))));
                city.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                cities.add(city);
            } while (c.moveToNext());
        }

        return cities;
    }

    public int updateCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CITY, city.getCity());

        return db.update(TABLE_CITY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(city.getId())});
    }

    public void deleteCity(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITY, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        deleteWeather(id);
    }

    public void createWeather(List<Weather> w, int city_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < w.size(); i++) {
            ContentValues values = new ContentValues();
            w.get(i).setCityID(city_id);
            putWeatherValues(values, w.get(i));
            db.insert(TABLE_WEATHER, null, values);
        }
    }

    public List<Weather> getWeather(int city_id) {
        List<Weather> wf = new ArrayList<Weather>();

        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER + " WHERE " +
                KEY_CITY_ID + "=" + city_id +
                " ORDER BY " + KEY_DATE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Weather w = new Weather();
                w.setID(c.getInt((c.getColumnIndex(KEY_ID))));
                w.setCityID(c.getInt((c.getColumnIndex(KEY_CITY_ID))));
                w.setCloudCover(c.getInt((c.getColumnIndex(KEY_CLOUD_COVER))));
                w.setHumidity(c.getFloat((c.getColumnIndex(KEY_HUMIDITY))));
                w.setPressure(c.getInt((c.getColumnIndex(KEY_PRESSURE))));
                w.setDate(c.getString((c.getColumnIndex(KEY_DATE))));
                w.setTempC(c.getInt((c.getColumnIndex(KEY_TEMP_C))));
                w.setTempF(c.getInt((c.getColumnIndex(KEY_TEMP_F))));
                w.setWeatherDesc(c.getString((c.getColumnIndex(KEY_WEATHER_DESC))));
                w.setIconURL(c.getString((c.getColumnIndex(KEY_ICON_URL))));
                w.setWindDir(c.getString((c.getColumnIndex(KEY_WIND_DIR))));
                w.setWindSpeed(c.getInt((c.getColumnIndex(KEY_WIND_SPEED))));
                w.setTempMinC(c.getInt((c.getColumnIndex(KEY_TEMP_MIN_C))));
                w.setTempMaxC(c.getInt((c.getColumnIndex(KEY_TEMP_MAX_C))));
                w.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));

                wf.add(w);
            } while (c.moveToNext());
        }
        return wf;
    }

    public void updateWeather(long city_id, List<Weather> weather) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_WEATHER + " WHERE " +
                KEY_CITY_ID + " = " + city_id;

        List<Integer> ids = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                ids.add(c.getInt((c.getColumnIndex(KEY_ID))));
            } while (c.moveToNext());
        }

        // if ids.size() != weather.size() todo

        for (int i = 0; i < weather.size(); i++) {
            ContentValues values = new ContentValues();
            putWeatherValues(values, weather.get(i));
            db.update(TABLE_WEATHER, values, KEY_ID + " = " + ids.get(i), null);
        }
    }

    public void deleteWeather(long city_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEATHER, KEY_CITY_ID + " = " + city_id, null);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void putWeatherValues(ContentValues values, Weather w) {
        values.put(KEY_CITY_ID, w.getCityID());
        values.put(KEY_CLOUD_COVER, w.getCloudCover());
        values.put(KEY_HUMIDITY, w.getHumidity());
        values.put(KEY_PRESSURE, w.getPressure());
        values.put(KEY_DATE, w.getDate());
        values.put(KEY_TEMP_C, w.getTempC());
        values.put(KEY_TEMP_F, w.getTempF());
        values.put(KEY_WEATHER_DESC, w.getWeatherDesc());
        values.put(KEY_ICON_URL, w.getIconURL());
        values.put(KEY_WIND_DIR, w.getWindDir());
        values.put(KEY_WIND_SPEED, w.getWindSpeed());
        values.put(KEY_TEMP_MIN_C, w.getTempMinC());
        values.put(KEY_TEMP_MAX_C, w.getTempMaxC());
        values.put(KEY_IMAGE, w.getImage());
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
