package ru.zulyaev.ifmo.zeather;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.zulyaev.ifmo.zeather.weather.Forecast;
import ru.zulyaev.ifmo.zeather.weather.WeatherApi;

import java.io.IOException;

/**
 * @author seidhe
 */
public class WeatherUpdateService extends IntentService {
    private static final String TAG = WeatherUpdateService.class.toString();

    public static final String UPDATE_DONE = TAG.concat(":UPDATE_DONE");
    private static final String FORCED_ACTION = TAG.concat(":FORCED");
    private static final String PLANNED_ACTION = TAG.concat(":PLANNED");

    private static final long DELAY = 300000;

    private static final int HOUR = 3600000;
    private static final int DAYS = 5;

    private final WeatherApi api = new WeatherApi();
    private WeatherDbOpenHelper dbHelper;
    private WeatherTable table;


    public WeatherUpdateService() {
        super("Weather updater");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new WeatherDbOpenHelper(this);
        table = new WeatherTable(dbHelper.getWritableDatabase());
    }

    public static void requestUpdate(Context context, boolean force) {
        context.startService(
                new Intent(context, WeatherUpdateService.class).setAction(force ? FORCED_ACTION : PLANNED_ACTION)
        );
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
                        new Intent(context, WeatherUpdateService.class)
                                .setAction(PLANNED_ACTION),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
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
        for (WeatherForecast forecast : table.getAll()) {
            if (force || now - forecast.getLastUpdated() >= HOUR) {
                try {
                    Forecast data = api.getForecast(forecast.getLatitude(), forecast.getLongitude(), DAYS);
                    if (data != null) {
                        table.updateForecast(forecast.getId(), data, now);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "Unable to update " + forecast.getLocation(), e);
                }
            }
        }
        sendBroadcast(new Intent(UPDATE_DONE));
    }

    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
