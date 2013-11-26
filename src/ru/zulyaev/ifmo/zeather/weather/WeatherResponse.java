package ru.zulyaev.ifmo.zeather.weather;

import java.util.Date;

/**
 * @author seidhe
 */
class WeatherResponse {
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
        ValueElement<String>[] weatherIconUrl;
    }

    static class WeatherElement {
        Date date;
        int tempMaxC;
        int tempMinC;
        int windspeedKmph;
        String winddir16Point;
        ValueElement<String>[] weatherDesc;
        ValueElement<String>[] weatherIconUrl;
    }
}
