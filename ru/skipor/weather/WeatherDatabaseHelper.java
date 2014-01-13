/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.skipor.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.skipor.weather.Forecast.WeatherForecast;

public class WeatherDatabaseHelper {

    public static final String DROP_IF_EXIST_QUERY = "DROP TABLE IF EXISTS ";
    private static WeatherDatabaseHelper instance = null;

    public static WeatherDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherDatabaseHelper(context);
        }
        return instance;
    }

    public static final String KEY_CITY_NAME = WeatherForecast.CITY_NAME_TAG;
    public static final String KEY_ROWID = "_id";


    private static final String TAG = "CitiesDbAdapter";
    private final DatabaseHelper myDatabaseHelper;
    private SQLiteDatabase myDatabase;
    private static final String CURRENT_STATE_ADDITION = "CurrentState";
    private static final String DAY_FORECAST_ADDITION = "DayForecast";
//    private static final String CITIES_TABLE_NAME = "data";
    private static final String CITIES_TABLE_NAME = "cities";
    private static final int DATABASE_VERSION = 1;
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table "+ CITIES_TABLE_NAME +" (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_CITY_NAME + " text not null);";

    public static final String KEY_ICON_NAME = WeatherForecast.WEATHER_ICON_URL_TAG;
    public static final String KEY_TEMP = WeatherForecast.TEMP_C_TAG;
    public static final String KEY_WEATHER_DESCRIPTION = WeatherForecast.WEATHER_DESCRIPTION_TAG;
    private static final String CURRENT_CONDITION_DATABASE_CREATE_PATTERN =
            "create table " + "%s" + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_CITY_NAME + " text not null, "
                    + KEY_TEMP + " integer not null, "
                    + KEY_WEATHER_DESCRIPTION + " text not null, "
                    + KEY_ICON_NAME  +" text not null);";

    public static final String KEY_DAY_OF_WEEK = WeatherForecast.DATE_TAG;
    public static final String KEY_TEMP_MAX = WeatherForecast.TEMP_MAX_C_TAG;
    public static final String KEY_TEMP_MIN = WeatherForecast.TEMP_MIN_C_TAG;
    private static final String DAY_FORECASTS_DATABASE_CREATE_PATTERN =
            "create table " + "%s" + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_DAY_OF_WEEK + " text not null, "
                    + KEY_TEMP_MAX + " integer not null, "
                    + KEY_TEMP_MIN + " integer not null, "
                    + KEY_ICON_NAME  +" text not null);";


    private int databaseUsers;


    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {

            super(context, CITIES_TABLE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS cities");
            onCreate(db);
        }
    }


    private WeatherDatabaseHelper(Context context) {
        myDatabaseHelper = new DatabaseHelper(context);
        databaseUsers = 0;

    }


    synchronized public void open() throws SQLException {

        if (databaseUsers == 0) {
            myDatabase = myDatabaseHelper.getWritableDatabase();
        }
        databaseUsers++;


    }


    synchronized public void close() {
        databaseUsers--;
        if (databaseUsers == 0) {
            myDatabaseHelper.close();
            myDatabase = null;
        }
    }

    private String getCityCurrentStateTableName(String cityName) {
        return "[" + cityName +  CURRENT_STATE_ADDITION + "]"; // valid sqlite table name
    }
    private String getCityDayForecastTableName(String cityName) {
        return "[" + cityName + DAY_FORECAST_ADDITION + "]"; // valid sqlite table name
    }

    private void dropCityTableIfExists(String cityName) {
        myDatabase.execSQL(DROP_IF_EXIST_QUERY + getCityCurrentStateTableName(cityName));
        myDatabase.execSQL(DROP_IF_EXIST_QUERY + getCityDayForecastTableName(cityName));
    }

    synchronized private void dropCityTableIfExists(long rowId) {
        Cursor cursor = fetchCity(rowId);
        dropCityTableIfExists(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY_NAME)));

    }

    public synchronized void createOrRecreateCityTable(String cityName) {
        dropCityTableIfExists(cityName);
        myDatabase.execSQL(String.format(CURRENT_CONDITION_DATABASE_CREATE_PATTERN, getCityCurrentStateTableName(cityName)));
        myDatabase.execSQL(String.format(DAY_FORECASTS_DATABASE_CREATE_PATTERN, getCityDayForecastTableName(cityName)));
    }


    /**
     * Create a new city using the name and url provided. If the city is
     * successfully created return the new rowId for that city, otherwise return
     * a -1 to indicate failure.
     *
     *
     * @param cityName the name of the city
     * @return rowId or -1 if failed
     */
    public long createCity(String cityName) {
        createOrRecreateCityTable(cityName);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CITY_NAME, cityName);

        return myDatabase.insert(CITIES_TABLE_NAME, null, initialValues);
    }

    public long createDayForecast(String cityName, String dayOfWeek, int tempMax, int tempMin, String iconName) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DAY_OF_WEEK, dayOfWeek);
        initialValues.put(KEY_TEMP_MAX, tempMax);
        initialValues.put(KEY_TEMP_MIN, tempMin);
        initialValues.put(KEY_ICON_NAME, iconName);
        return myDatabase.insert(getCityDayForecastTableName(cityName), null, initialValues);
    }

    public void createCurrentCondition(String cityName, int temp, String weatherDescription, String iconName) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CITY_NAME, cityName);
        initialValues.put(KEY_TEMP, temp);
        initialValues.put(KEY_WEATHER_DESCRIPTION, weatherDescription);
        initialValues.put(KEY_ICON_NAME, iconName);
        myDatabase.insert(getCityCurrentStateTableName(cityName), null, initialValues);


    }

    /**
     * Delete the city with the given rowId
     *
     * @param rowId id of city to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCity(long rowId) {
        dropCityTableIfExists(rowId);

        return myDatabase.delete(CITIES_TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all cities in the database
     *
     * @return Cursor over all cities
     */
    public Cursor fetchAllCities() {


        return myDatabase.query(CITIES_TABLE_NAME, new String[]{KEY_ROWID, KEY_CITY_NAME},
                null, null, null, null, null);
    }
    public Cursor fetchDayForecasts(String cityName) {


        return myDatabase.query(getCityDayForecastTableName(cityName),
                new String[]{KEY_ROWID, KEY_DAY_OF_WEEK, KEY_TEMP_MAX, KEY_TEMP_MIN, KEY_ICON_NAME},
                null, null, null, null, KEY_ROWID);
    }

    public Cursor fetchCurrentCondition(String cityName) {


        return myDatabase.query(getCityCurrentStateTableName(cityName),
                new String[]{KEY_ROWID, KEY_CITY_NAME, KEY_TEMP, KEY_WEATHER_DESCRIPTION, KEY_ICON_NAME},
                null, null, null, null, KEY_ROWID);
    }


    /**
     * Return a Cursor positioned at the city that matches the given rowId
     *
     * @param rowId id of city to retrieve
     * @return Cursor positioned to matching city, if found
     * @throws android.database.SQLException if city could not be found/retrieved
     */
    public Cursor fetchCity(long rowId) throws SQLException {

        Cursor mCursor =

                myDatabase.query(true, CITIES_TABLE_NAME, new String[]{KEY_ROWID,
                        KEY_CITY_NAME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;

    }


//
//
//    public Cursor fetchTitle(String cityName, long rowId) throws SQLException {
//
//        Cursor mCursor =
//
//                myDatabase.query(true, getCityCurrentStateTableName(cityName), new String[]{KEY_ROWID,
//                       FEED_KEY_TITLE, FEED_KEY_BODY}, KEY_ROWID + "=" + rowId, null,
//                        null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//
//        return mCursor;
//
//    }




    /**
     * Update the city using the details provided. The city to be updated is
     * specified using the rowId, and it is altered to use the name and url
     * values passed in
     *
     *
     * @param rowId id of city to update
     * @param cityName  value to set city name to
     * @return true if the city was successfully updated, false otherwise
     */
    public boolean updateCity(long rowId, String cityName) {
//        dropCityTableIfExists(rowId);
        createOrRecreateCityTable(cityName);

        ContentValues args = new ContentValues();
        args.put(KEY_CITY_NAME, cityName);

        return myDatabase.update(CITIES_TABLE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
