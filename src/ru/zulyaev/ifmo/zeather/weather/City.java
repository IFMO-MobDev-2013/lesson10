package ru.zulyaev.ifmo.zeather.weather;

/**
 * @author seidhe
 */
public class City {
    private final String title;
    private final String country;
    private final double latitude;
    private final double longitude;
    private final int population;

    City(SearchResponse.ResultElement result) {
        this.title = result.areaName[0].value;
        this.country = result.country[0].value;
        this.latitude = Double.parseDouble(result.latitude);
        this.longitude = Double.parseDouble(result.longitude);
        this.population = Integer.parseInt(result.population);
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getPopulation() {
        return population;
    }
}
