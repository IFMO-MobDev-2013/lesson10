package ru.skipor.weather.Forecast;

/**
 * Created by Vladimir Skipor on 1/9/14.
 * Email: vladimirskipor@gmail.com
 */
public class ForecastException extends Exception {

    private final static String TAG = "ForecastException";

    public ForecastException(String detailMessage) {
        super(detailMessage);
    }

    public ForecastException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ForecastException(Throwable throwable) {
        super(throwable);
    }
}
