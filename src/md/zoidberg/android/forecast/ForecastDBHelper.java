package md.zoidberg.android.forecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gfv on 05.02.14.
 */
public class ForecastDBHelper extends SQLiteOpenHelper {


    public static final String FIELD_ID = "id";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_TEMPERATURE = "temperature";
    public static final String FIELD_CONDITION = "condition";
    public static final String TABLE_CITIES = "cities";
    public static final String FIELD_FORECAST_ID = "forecast";
    private static final String FORECASTS_DB_NAME = "forecasts.db";
    private static final int FORECAST_DB_VERSION = 1;
    private static final String FIELD_DATE = "date";

    public ForecastDBHelper(Context context) {
        super(context, FORECASTS_DB_NAME, null, FORECAST_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CITIES + " (" +
                FIELD_ID + " integer primary key autoincrement, " +
                FIELD_FORECAST_ID + " integer, " +
                FIELD_CITY + " varchar(255) not null, " +
                FIELD_TEMPERATURE + " varchar(16), " +
                FIELD_CONDITION + " integer, " +
                FIELD_DATE + " varchar(255), " +
                "unique (" + FIELD_CITY + ", " + FIELD_FORECAST_ID + ") on conflict replace);"
        );
    }

    public List<String> getCities() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(true, TABLE_CITIES, new String[]{FIELD_CITY}, null, null, null, null, null, null);
        List<String> cities = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                cities.add(cur.getString(0));
            } while (cur.moveToNext());
        }

        return cities;
    }

    public void updateCity(String city, WeatherInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_CITY, city);
        values.put(FIELD_TEMPERATURE, Integer.toString(info.getCurrentTempC()));
        values.put(FIELD_CONDITION, info.getCurrentCode());
        values.put(FIELD_FORECAST_ID, 0);
        values.put(FIELD_DATE, info.getCurrentConditionDate());
        db.insertWithOnConflict(TABLE_CITIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        insertForecast(1, city, info.getForecastInfo1());
        insertForecast(2, city, info.getForecastInfo2());
        insertForecast(3, city, info.getForecastInfo3());
        db.close();
    }

    public List<Forecast> getForecastsForCity(String city) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.query(TABLE_CITIES, new String[]{FIELD_FORECAST_ID, FIELD_DATE, FIELD_CONDITION, FIELD_TEMPERATURE}, FIELD_CITY + "= ?", new String[]{city}, null, null, FIELD_FORECAST_ID + " ASC");
        List<Forecast> forecasts = new ArrayList<>();
        if (cur.getCount() == 0) return forecasts;
        cur.moveToFirst();
        do {
            Forecast forecast = new Forecast();
            forecast.setDate(cur.getString(1));
            forecast.setConditionId(cur.getInt(2));
            forecast.setTemperature(cur.getString(3));
            forecasts.add(forecast);
        } while (cur.moveToNext());
        cur.close();
        return forecasts;
    }

    private void insertForecast(int forecastId, String city, WeatherInfo.ForecastInfo info) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_CITY, city);
        values.put(FIELD_TEMPERATURE, Integer.toString(info.getForecastTempHighC()));
        values.put(FIELD_CONDITION, info.getForecastCode());
        values.put(FIELD_FORECAST_ID, forecastId);
        values.put(FIELD_DATE, info.getForecastDate());
        db.insertWithOnConflict(TABLE_CITIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CITIES + ";");
        onCreate(db);
    }
}
