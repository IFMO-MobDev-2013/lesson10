package com.android;

/**
 * Created with IntelliJ IDEA.
 * User: Руслан
 * Date: 21.11.13
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class City {
    int id;
    String name;
    String country;
    double tempNow;
    String weatherNow;
    int weatherNowCode;
    int[] weatherCode;
    double[] tempLow;
    double[] tempHigh;
    String[] weather;
    String[] dates;
    String date;

    public City(String name, String country, int id) {
        this.name = name;
        this.country = country;
        this.id = id;
        weatherCode = new int[3];
        tempHigh = new double[3];
        tempLow = new double[3];
        weather = new String[3];
        dates = new String[3];
        date = "";
    }

}
