package ru.skipor.weather.Forecast;

/**
 * Created by Vladimir Skipor on 1/9/14.
 * Email: vladimirskipor@gmail.com
 */
public interface ForecastProvider {
    String getSuitableCityName(String cityName) throws ForecastException;
    WeatherForecast getForecast(String cityName) throws ForecastException;

}
