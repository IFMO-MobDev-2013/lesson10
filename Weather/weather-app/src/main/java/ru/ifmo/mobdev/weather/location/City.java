package ru.ifmo.mobdev.weather.location;

/**
 * Created by Nick Smelik on 21.11.13.
 */
public class City {
    private String name;
    private String country;
    private float latitude;
    private float longitude;

    public City(String name, String country, float latitude, float longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    City(CityResponse.ResultElement element) {
        this (
                element.areaName[0].value,
                element.country[0].value,
                element.latitude,
                element.longitude
        );
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
