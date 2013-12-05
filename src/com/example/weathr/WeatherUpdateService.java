package com.example.weathr;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import be.hcpl.android.forecast.model.ForecastResponse;

/**
 * Created by Sergey on 11/29/13.
 */
public class WeatherUpdateService extends IntentService {

    WeatherUpdateService() {
        super("WeatherUpdateService");
    }

    WeatherDBAdapter mDb;

    public static final String CITY_ID    = "city_id";
    public static final String ALL_CITIES = "all_cities";

    @Override
    protected void onHandleIntent(Intent intent) {
        mDb = new WeatherDBAdapter(this);
        mDb.open();
        if (intent.hasExtra(CITY_ID))
            updateCityById(intent.getLongExtra(CITY_ID, -1));
        else {
            Cursor c = mDb.fetchAllCities();
            if (c.moveToFirst())
                do {
                    updateCityById(c.getLong(c.getColumnIndex(WeatherDBAdapter.KEY_ID)));
                } while (c.moveToNext());
        }
    }

    private void updateCityById(final long id) {
        City c = mDb.getCityById(id);
        WeatherGetter w = new WeatherGetter() {
            @Override
            protected void onProgressUpdate(City... values) {
                super.onProgressUpdate(values[0]);
                City c = values[0];
                broadcastAfterUpdate(mDb.putCity(c));
            }
        };
        w.execute(c);
    }

    public static final String BROADCAST_SUCCESS = "com.ifmomd.igushkin.weathr.WEATHER_UPDATED";

    private void broadcastAfterUpdate(long id) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_SUCCESS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(CITY_ID, id);
        sendBroadcast(broadcastIntent);
    }

    public static final String BROADCAST_FAIL = "com.ifmomd.igushkin.weathr.UPDATE_FAILED";

    private void broadcastAfterFailed(long id) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_FAIL);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(CITY_ID, id);
        sendBroadcast(broadcastIntent);
    }
}
