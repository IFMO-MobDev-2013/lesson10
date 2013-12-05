package ru.ifmo.mobdev.weather.weather;

/**
 * Created by Nick Smelik on 20.11.13.
 */
public class Forecast {

    private final Weather today;
    private final Weather[] forecast;

    public Forecast(Weather today, Weather[] forecast) {
        this.today = today;
        this.forecast = forecast;
    }

    public Forecast(WeatherResponse response) {
        this.today = new Weather(response.data.current_condition[0]);
        int days = response.data.weather.length;
        this.forecast = new Weather[days];
        for (int i = 0; i < days; ++i) {
            this.forecast[i] = new Weather(response.data.weather[i]);
        }
    }

    public Weather getToday() {
        return today;
    }

    public int getDays() {
        return forecast.length;
    }

    public Weather getDay(int day) {
        return forecast[day];
    }
}
