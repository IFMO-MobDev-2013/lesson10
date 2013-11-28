package com.example.weather;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 21.11.13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class City {

    public City(String name, String latitude, String longitude)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String name;
    public String latitude;
    public String longitude;
}
