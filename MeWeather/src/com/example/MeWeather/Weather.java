package com.example.MeWeather;

import java.util.Date;

/**
 * Created by ViruZ on 08.01.14.
 */
public class Weather {
    private Date date;
    private int minTemp;
    private int maxTemp;
    private int windSpeed;
    private String windDirection;
    private int code;

    public Weather(Date date, int minTemp, int maxTemp, int windSpeed, String windDirection, int code) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.code = code;
    }

    Weather(WeatherPars.WeatherElement element) {
        this(
                element.date,
                element.tempMinC,
                element.tempMaxC,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherCode
        );
    }

    Weather(WeatherPars.CurrentElement element) {
        this(
                new Date(),
                element.temp_C,
                element.temp_C,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherCode
        );
    }

    public Date getDate() {





        return date;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public String getWindDir() {
        return windDirection;
    }

    public int getCode() {
        return code;
    }

}
