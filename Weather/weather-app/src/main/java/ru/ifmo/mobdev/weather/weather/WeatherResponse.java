package ru.ifmo.mobdev.weather.weather;

import java.util.Date;

/**
 * Created by Nick Smelik on 20.11.13.
 */
public class WeatherResponse {
    DataElement data;

    static class DataElement {
        CurrentElement[] current_condition;
        WeatherElement[] weather;
    }

    static class CurrentElement {
        int temp_C;
        int windspeedKmph;
        String winddir16Point;
        ValueElement<String>[] weatherDesc;
    }

    static class WeatherElement {
        Date date;
        int tempMaxC;
        int tempMinC;
        int windspeedKmph;
        String winddir16Point;
        ValueElement<String>[] weatherDesc;
    }
}
