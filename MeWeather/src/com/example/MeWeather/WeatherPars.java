package com.example.MeWeather;

import java.util.Date;

/**
 * Created by ViruZ on 08.01.14.
 */
public class WeatherPars {
    DataElement data;

    static class DataElement {
        CurrentElement[] current_condition;
        WeatherElement[] weather;
    }

    static class CurrentElement {
        int temp_C;
        int weatherCode;
        int windspeedKmph;
        String winddir16Point;
    }

    static class WeatherElement {
        Date date;
        int tempMaxC;
        int tempMinC;
        int weatherCode;
        int windspeedKmph;
        String winddir16Point;
    }
}
