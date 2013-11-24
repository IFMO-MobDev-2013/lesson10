package com.polarnick.day09.weather;

import android.util.Log;
import com.forecastiolib.FIOCurrently;
import com.forecastiolib.FIODaily;
import com.forecastiolib.FIOHourly;
import com.forecastiolib.ForecastIO;
import com.polarnick.day09.dao.DatabaseHelperFactory;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastData;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class WeatherProvider {
    private static final String API_KEY = "a4e9018570320442da83bf48ade940d8";
    private static final ForecastIO FIO = new ForecastIO(API_KEY);

    static {
        FIO.setUnits(ForecastIO.UNITS_SI);
        FIO.setExcludeURL("minutely");
    }

    public static synchronized ForecastForCity getForecastForCity(final City city) {
        try {
            ForecastForCity newForecastForCity = new ForecastForCity();
            DatabaseHelperFactory.getHelper().getForecastForCityDAO().create(newForecastForCity);

            newForecastForCity.setDownloadedAt(System.currentTimeMillis());

            FIO.getForecast(city.getLatitude(), city.getLongitude());
            final ForecastData current = new ForecastData(new FIOCurrently(FIO).get());
            DatabaseHelperFactory.getHelper().getForecastDataDAO().create(current);
            newForecastForCity.setCurrent(current);
            current.setForecastForCity(newForecastForCity);

            final FIODaily fioDaily = new FIODaily(FIO);
            final FIOHourly fioHourly = new FIOHourly(FIO);

            newForecastForCity.setDaysSummary(fioDaily.getDaily().summary());
            newForecastForCity.setHoursSummary(fioHourly.getHourly().summary());

            for (int i = 0; i < ForecastForCity.HOURS_COUNT; i++) {
                ForecastData[] hoursData = new ForecastData[ForecastForCity.HOURS_END_OFFSET[i] - ForecastForCity.HOURS_START_OFFSET[i]];
                for (int j = ForecastForCity.HOURS_START_OFFSET[i]; j < ForecastForCity.HOURS_END_OFFSET[i]; j++) {
                    hoursData[j - ForecastForCity.HOURS_START_OFFSET[i]] = new ForecastData(fioHourly.getHour(j));
                }
                final ForecastData forecastData = new ForecastData(hoursData);
                forecastData.setForecastForCity(newForecastForCity);
                forecastData.setHour(true);
                DatabaseHelperFactory.getHelper().getForecastDataDAO().create(forecastData);
            }

            for (int i = 0; i < ForecastForCity.DAYS_COUNT; i++) {
                final ForecastData forecastData = new ForecastData(fioDaily.getDay(i));
                forecastData.setForecastForCity(newForecastForCity);
                DatabaseHelperFactory.getHelper().getForecastDataDAO().create(forecastData);
            }

            DatabaseHelperFactory.getHelper().getForecastForCityDAO().update(newForecastForCity);
            DatabaseHelperFactory.getHelper().getForecastForCityDAO().refresh(newForecastForCity);
            return newForecastForCity;
        } catch (SQLException e) {
            Log.e(WeatherProvider.class.getName(), "Failed while loading forecast!");
            throw new RuntimeException(e);
        }
    }

}
