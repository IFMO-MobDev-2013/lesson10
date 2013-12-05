package ru.ifmo.mobdev.weather.location;


/**
 * Created by Nick Smelik on 21.11.13.
 */
public class CityResponse {
    DataElement search_api;

    static class DataElement {
        ResultElement[] result;
    }

    static class ResultElement {
        ValueElement<String>[] areaName;
        ValueElement<String>[] country;
        float latitude;
        float longitude;
    }
}
