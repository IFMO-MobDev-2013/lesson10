package ru.ifmo.mobdev.weather.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import ru.ifmo.mobdev.weather.database.CityInformation;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;
import ru.ifmo.mobdev.weather.weather.Forecast;
import ru.ifmo.mobdev.weather.weather.WeatherProvider;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class UpdateWeatherService extends IntentService {
    public static final String TAG = UpdateWeatherService.class.toString();
    public static final String UPDATE_DONE = TAG.concat(":UPDATE_DONE");
    private static final String FORCED_ACTION = TAG.concat(":FORCED");
    private static final String PLANNED_ACTION = TAG.concat(":PLANNED");

    private static final long DELAY = 300000;

    private static final int HOUR = 3600000;
    private static final int DAYS = 5;

    private final WeatherProvider api = new WeatherProvider();
    private dbOpenHelper dbHelper;
    private DataBaseTable table;

    public UpdateWeatherService() {
        super("Weather updater");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new dbOpenHelper(this);
        table = new DataBaseTable(dbHelper.getWritableDatabase());
    }

    public static void ensureUpdating(Context context, boolean now) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now ? 0 : DELAY,
                DELAY,
                PendingIntent.getService(
                        context,
                        0,
                        new Intent(context, UpdateWeatherService.class)
                                .setAction(PLANNED_ACTION),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
    }

    public static void requestUpdate(Context context, boolean force) {
        context.startService(
                new Intent(context, UpdateWeatherService.class).setAction(force ? FORCED_ACTION : PLANNED_ACTION)
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Log.w(TAG, "Start Intent");
        if (FORCED_ACTION.equals(action)) {
            update(true);
        } else if (PLANNED_ACTION.equals(action)) {
            update(false);
        } else {
            Log.w(TAG, "Unexpected action " + action);
        }
    }

    private void update(boolean force) {
        long now = System.currentTimeMillis();
        Forecast data;
        for (CityInformation forecast : table.getAll()) {
            if (force || now - forecast.getLastUpdate() >= HOUR) {
                try {
                    data = api.getForecast(forecast.getCityName(), DAYS);
                    if (data != null) {
                        table.updateForecast(forecast.getId(), data, now);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Unable to update " + forecast.getCityName(), e);
                }
            }
        }
        sendBroadcast(new Intent(UPDATE_DONE));
    }
}
