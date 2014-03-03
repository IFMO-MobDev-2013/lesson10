package com.example.lesson10;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 1:03
 * To change this template use File | Settings | File Templates.
 */
public class ItemWeather {
    protected String date;
    protected String wind;
    protected String minT;
    protected String maxT;
    protected String desc;
    protected Bitmap icon;

    ItemWeather(String date, String wind, String minT, String maxT, String desc, Bitmap icon){
        this.date = date;
        this.wind = wind;
        this.minT = minT;
        this.maxT = maxT;
        this.desc = desc;
        this.icon = icon;
    }
}
