package com.example.lesson10;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 3:58
 * To change this template use File | Settings | File Templates.
 */
public class City {
    protected String name;
    protected String latitude;
    protected String longitude;

    City(String name, String lat, String lon){
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
    }

    public City() {
        this.name = "";
        this.latitude = "";
        this.longitude = "";
    }
}
