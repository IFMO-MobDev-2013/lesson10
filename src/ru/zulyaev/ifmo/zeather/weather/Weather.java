package ru.zulyaev.ifmo.zeather.weather;

import java.util.Date;

/**
 * @author seidhe
 */
public class Weather {
    private final Date date;
    private final int minTemp;
    private final int maxTemp;
    private final int windSpeed;
    private final String windDirection;
    private final String description;
    private final String iconUrl;

    public Weather(Date date, int minTemp, int maxTemp, int windSpeed, String windDirection, String description, String iconUrl) {
        this.date = date;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    Weather(WeatherResponse.WeatherElement element) {
        this(
                element.date,
                element.tempMinC,
                element.tempMaxC,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherDesc[0].value,
                element.weatherIconUrl[0].value
        );
    }

    Weather(WeatherResponse.CurrentElement element) {
        this(
                new Date(),
                element.temp_C,
                element.temp_C,
                element.windspeedKmph,
                element.winddir16Point,
                element.weatherDesc[0].value,
                element.weatherIconUrl[0].value
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

    public String getWindDirection() {
        return windDirection;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
