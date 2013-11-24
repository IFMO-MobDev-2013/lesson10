package com.polarnick.day09.weather;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.j256.ormlite.misc.TransactionManager;
import com.polarnick.day09.Utils;
import com.polarnick.day09.dao.DatabaseHelperFactory;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastData;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Date: 19.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class WeatherUpdaterService extends IntentService {

    public static final String FORECAST_UPDATE_TAG = "forecast updated notification";

    private static final int PERIOD_OF_REFRESH = 30 * 60 * 1000;//30 minutes

    public WeatherUpdaterService() {
        super("Forecast updater service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i(WeatherUpdaterService.class.getName(), "Updating forecast...");

            List<City> cities = DatabaseHelperFactory.getHelper().getCityDAO().getAllCities();
            Log.i(WeatherUpdaterService.class.getName(), "Updating forecast for " + cities.size() + " cities...");

            boolean somethingWasUpdated = false;
            if (Utils.isOnline(getApplicationContext())) {
                for (City city : cities) {
                    if (Utils.isOnline(getApplicationContext())) {
                        ForecastForCity forecast = WeatherProvider.getForecastForCity(city);
                        if (forecast != null) {
                            Log.i(WeatherUpdaterService.class.getName(), "Forecast downloading was success for city with name=" + city.getName() + " and id=" + city.getId() + "!");
                            somethingWasUpdated = updateForecastForCity(city, forecast);
                        } else {
                            Log.i(WeatherUpdaterService.class.getName(), "Forecast downloading was failed for city with name=" + city.getName() + " and id=" + city.getId() + "!");
                        }
                    }
                }
            }

            if (somethingWasUpdated) {
                notifyActivity();
            }
        } catch (Exception e) {
            Log.e(WeatherUpdaterService.class.getName(), "Exception was caught!", e);
        }
        scheduleNextUpdate(intent);
    }

    private boolean updateForecastForCity(final City city, final ForecastForCity forecast) {
        final ForecastForCity oldForecast = city.getForecast();
        try {
            TransactionManager.callInTransaction(DatabaseHelperFactory.getHelper().getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    city.setForecast(forecast);
                    DatabaseHelperFactory.getHelper().getCityDAO().update(city);
                    if (oldForecast != null) {
                        DatabaseHelperFactory.getHelper().getForecastForCityDAO().refresh(oldForecast);
                        deleteForecast(oldForecast);
                    }
                    return null;
                }
            });
            return true;
        } catch (SQLException e) {
            if (oldForecast != null) {
                Log.e(WeatherUpdaterService.class.getName(), "Updating forecast for city with id=" + city.getId()
                        + " and name=" + city.getName() + " was failed! Old forecast id=" + oldForecast.getId() + ", new forecast id=" + forecast.getId());
            } else {
                Log.e(WeatherUpdaterService.class.getName(), "Updating forecast for city with id=" + city.getId()
                        + " and name=" + city.getName() + " was failed! New forecast id=" + forecast.getId());
            }
            throw new RuntimeException(e);
        }
    }

    private void deleteForecast(final ForecastForCity oldForecast) throws SQLException {
        DatabaseHelperFactory.getHelper().getForecastDataDAO().deleteById(oldForecast.getCurrent().getId());
        for (ForecastData forecastData : oldForecast.getData()) {
            DatabaseHelperFactory.getHelper().getForecastDataDAO().deleteById(forecastData.getId());
        }
        DatabaseHelperFactory.getHelper().getForecastForCityDAO().deleteById(oldForecast.getId());
    }

    private void notifyActivity() {
        Intent intent = new Intent(FORECAST_UPDATE_TAG);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void scheduleNextUpdate(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PERIOD_OF_REFRESH, pendingIntent);
    }
}
