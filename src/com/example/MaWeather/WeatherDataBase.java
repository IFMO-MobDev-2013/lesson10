package com.example.MaWeather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 28.11.13
 * Time: 20:09
 */
public class WeatherDataBase {

    private static final String DATABASE_NAME = "weather_database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CITY_ID = "city_id";
    private static final String CITY_NAME = "city_name";
    private static final String LIST_OF_CITY = "list_of_city";
    private static final String CITES = "cites";
    private static final String TABLE_NAME = "table_name";
    private static final String WEATHER_TYPE = "weather_type";
    private static final String SUNRISE = "sunrise";
    private static final String SUNSET = "sunset";
    private static final String TEMPERATURE = "temperature";
    private static final String HUMIDITY = "humidity";
    private static final String PRESSURE = "pressure";
    private static final String PICTURE_TYPE = "picture_type";
    private static final String BIG_PICTURE_TYPE = "big_picture_type";

    private SQLiteDatabase myDataBase;
    private DataBaseHelper dataBaseHelper;
    private Context context;
    private static int counterTables = 0;


    public WeatherDataBase(Context context) {
        this.context = context;
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();
        dataBaseHelper.close();
        myDataBase.close();
    }

    public void addCites(ArrayList<CityItem> items) {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        String request =  "CREATE TABLE "
                + LIST_OF_CITY + " (" + DataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY_NAME + " TEXT,"
                + CITY_ID + " TEXT);";
        try {
            myDataBase.execSQL(request);

            for (int i = 0; i < items.size(); i++) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(CITY_NAME, items.get(i).getCityName());
                contentValues.put(CITY_ID, items.get(i).getCityId());

                myDataBase.insert(LIST_OF_CITY, null, contentValues);
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        dataBaseHelper.close();
        myDataBase.close();

    }

    public ArrayList<WeatherItem> getForecast(String name) {

        String tableName = "";

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        ArrayList<WeatherItem> res = new ArrayList<WeatherItem>();

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            if (name.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME)))) {
                tableName = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TABLE_NAME));
            }
        }

        cursor.close();

        cursor = myDataBase.query(tableName, new String[] {
                DataBaseHelper._ID, WeatherDataBase.WEATHER_TYPE, WeatherDataBase.SUNRISE, WeatherDataBase.SUNSET, WeatherDataBase.TEMPERATURE, WeatherDataBase.HUMIDITY, WeatherDataBase.PRESSURE, WeatherDataBase.PICTURE_TYPE, WeatherDataBase.BIG_PICTURE_TYPE},
                null,
                null,
                null,
                null,
                null
        );

        String weatherType;
        String sunrise;
        String sunset;
        String temperature;
        String humidity;
        String pressure;
        String pictureType;
        String bigPictureType;

        while (cursor.moveToNext()) {
            weatherType = cursor.getString(cursor.getColumnIndex(WeatherDataBase.WEATHER_TYPE));
            sunrise = cursor.getString(cursor.getColumnIndex(WeatherDataBase.SUNRISE));
            sunset = cursor.getString(cursor.getColumnIndex(WeatherDataBase.SUNSET));
            temperature = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TEMPERATURE));
            humidity = cursor.getString(cursor.getColumnIndex(WeatherDataBase.HUMIDITY));
            pressure = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PRESSURE));
            pictureType = cursor.getString(cursor.getColumnIndex(WeatherDataBase.PICTURE_TYPE));
            bigPictureType = cursor.getString(cursor.getColumnIndex(WeatherDataBase.BIG_PICTURE_TYPE));

            if ("null".equals(weatherType)) {
                weatherType = null;
            }
            if ("null".equals(sunrise)) {
                sunrise = null;
            }
            if ("null".equals(sunset)) {
                sunset = null;
            }
            if ("null".equals(temperature)) {
                temperature = null;
            }
            if ("null".equals(humidity)) {
                humidity = null;
            }
            if ("null".equals(pressure)) {
                pressure = null;
            }
            if ("null".equals(pictureType)) {
                pictureType = null;
            }
            if ("null".equals(bigPictureType)) {
                bigPictureType = null;
            }
            res.add(new WeatherItem(weatherType, sunrise, sunset, temperature, humidity, pressure, pictureType, bigPictureType));
        }
        return res;
    }

    public void delete(String cityName) {

        int index = getId(cityName);
        String name = getTableName(cityName);

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        myDataBase.delete(CITES, DataBaseHelper._ID + " = " + index, null);
        myDataBase.execSQL("DROP TABLE IF EXISTS "+ name);

        dataBaseHelper.close();
        myDataBase.close();
    }

    public int getTableCount() {

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        int answer = cursor.getCount();

        dataBaseHelper.close();
        myDataBase.close();
        cursor.close();

        return answer;
    }

    public String getName(int index)  {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        String answer = "";

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToPosition(index);
        answer = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME));

        dataBaseHelper.close();
        myDataBase.close();
        cursor.close();

        return answer;
    }

    public String getTableName(String cityName) {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        String result = "";

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {

            result = cursor.getString(cursor.getColumnIndex(WeatherDataBase.TABLE_NAME));

            if (cityName.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME))))   {
                break;
            }
        }

        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();

        return result;
    }

    public int getId(String cityName) {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        int result = 0;

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {

            result = cursor.getInt(cursor.getColumnIndex(DataBaseHelper._ID));

            if (cityName.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME))))   {
                break;
            }
        }

        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();

        return result;
    }

    public String getCityId(String cityName) {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        String result = "";

        Cursor cursor = myDataBase.query(WeatherDataBase.LIST_OF_CITY, new String[] {
                DataBaseHelper._ID, WeatherDataBase.CITY_ID, WeatherDataBase.CITY_NAME },
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {

            result = cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_ID));

            if (cityName.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME))))   {
                break;
            }
        }

        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();

        return result;
    }

    private boolean checkName(String name) {
        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        boolean result = false;

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            if (name.equals(cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME)))) {
                result = true;
            }
        }

        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();

        return result;
    }

    private String toTrueString(String s) {
        if (s != null) {
            return s;
        }
        return "null";
    }

    private int getTrueIndex() {

        int result = 0;

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        //Log.d("azaza", new Integer(cursor.getCount()).toString());
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(cursor.getCount()-1);

            try {
                result = cursor.getInt(cursor.getColumnIndex(DataBaseHelper._ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();

        return result + 1;
    }

    public ArrayList<HashMap<String, String>> getListOfCites() {
        ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        Cursor cursor = myDataBase.query(CITES, new String[] {
                DataBaseHelper._ID, WeatherDataBase.TABLE_NAME, WeatherDataBase.CITY_NAME},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            try {
                map.put("city", cursor.getString(cursor.getColumnIndex(WeatherDataBase.CITY_NAME)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            res.add(map);
            //Log.d("ahahah", new Integer(res.size()).toString());
        }

        cursor.close();
        dataBaseHelper.close();
        myDataBase.close();
        return res;

    }

    public void addCity(ArrayList<WeatherItem> items, String cityName) {
        if (checkName(cityName)) {
            return;
        }

        int trueIndex = getTrueIndex();

        dataBaseHelper = new DataBaseHelper(context);
        myDataBase = dataBaseHelper.getWritableDatabase();

        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put(TABLE_NAME, "table" + trueIndex);
            contentValues.put(CITY_NAME, cityName);

            myDataBase.insert(CITES, null, contentValues);

            String request =  "CREATE TABLE "
                    + "table" + trueIndex + " (" + DataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + WEATHER_TYPE + " TEXT,"
                    + SUNRISE + " TEXT,"
                    + SUNSET + " TEXT,"
                    + TEMPERATURE + " TEXT,"
                    + HUMIDITY + " TEXT,"
                    + PRESSURE + " TEXT,"
                    + PICTURE_TYPE + " TEXT,"
                    + BIG_PICTURE_TYPE + " TEXT);";

            myDataBase.execSQL(request);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < items.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WEATHER_TYPE, toTrueString(items.get(i).getWeatherType()));
            contentValues.put(SUNRISE, toTrueString(items.get(i).getSunrise()));
            contentValues.put(SUNSET, toTrueString(items.get(i).getSunset()));
            contentValues.put(TEMPERATURE, toTrueString(items.get(i).getTemperature()));
            contentValues.put(HUMIDITY, toTrueString(items.get(i).getHumidity()));
            contentValues.put(PRESSURE, toTrueString(items.get(i).getPressure()));
            contentValues.put(PICTURE_TYPE, toTrueString(items.get(i).getPictureType()));
            contentValues.put(BIG_PICTURE_TYPE, toTrueString(items.get(i).getBigPictureType()));

            myDataBase.insert("table" + trueIndex, null, contentValues);
        }

        dataBaseHelper.close();
        myDataBase.close();
    }

    public class DataBaseHelper extends SQLiteOpenHelper implements BaseColumns {

        private final String CREATE_TABLE = "CREATE TABLE "
                + CITES + " (" + DataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CITY_NAME + " TEXT,"
                + TABLE_NAME + " TEXT);";

        /*private final String DELETE_TABLE = "DROP TABLE IF EXISTS "
                + TABLE_NAME;*/

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }
}
