package com.example.MaWeather;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 28.11.13
 * Time: 20:11
 */
public class CityItem {

    private String cityName;
    private String cityId;

    CityItem(String cityName, String cityId) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public  String getCityId() {
        return cityId;
    }
}
