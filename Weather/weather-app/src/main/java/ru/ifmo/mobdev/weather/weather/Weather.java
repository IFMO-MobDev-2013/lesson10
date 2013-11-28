package ru.ifmo.mobdev.weather.weather;

import java.util.Date;

/**
 * Created by Nick Smelik on 20.11.13.
 */
public class Weather {

    private Date date;
    private int minTemp;
    private int maxTemp;
    private int windSpeed;
    private String windDirection;
    private String description;

    public Weather(Date date, int minTemp, int maxTemp, int windSpeed, String windDirection, String description) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.description = description;
    }

    Weather(WeatherResponse.WeatherElement element) {
        this(
                element.date,
                element.tempMinC,
                element.tempMaxC,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherDesc[0].value
        );
    }

    Weather(WeatherResponse.CurrentElement element) {
        this(
                new Date(),
                element.temp_C,
                element.temp_C,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherDesc[0].value
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

    public String getDescription() {
        return description;
    }
}
