package com.example.MeWeather;

/**
 * Created by ViruZ on 08.01.14.
 */
public class Forecast {
private final Weather today;
private final Weather[] forecast;

 /*       public Forecast(Weather today, Weather[] forecast) {
            this.today = today;
            this.forecast = forecast;
        }
*/
        public Forecast(WeatherPars pars) {
            this.today = new Weather(pars.data.current_condition[0]);
            int days = pars.data.weather.length;
            this.forecast = new Weather[days];
            for (int i = 0; i < days; i++) {
                this.forecast[i] = new Weather(pars.data.weather[i]);
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
