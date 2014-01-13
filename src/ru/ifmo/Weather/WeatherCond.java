package ru.ifmo.Weather;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 20.11.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class WeatherCond {
    final static String[] nowTags = new String[]{null, null, "temp_C", "pressure", "weatherIconUrl", null};
    final static String[] forecastTags = new String[]{null, null, "tempMinC", "tempMaxC", "weatherIconUrl", null};

    final static String[] SqlTags = new String[]{"ROW_ID", "TOWN_ID", "TEMP", "ALTER_INFO", "ICON_URL", "now"};

    String[] param = new String[SqlTags.length];
    Bitmap pic;

    final static int ID = 0;
    final static int TOWN_ID = 1;

    final static int TEMP_NOW = 2;
    final static int PRESURE_NOW = 3;

    final static int TEMP_MIN = 2;
    final static int TEMP_MAX = 3;

    final static int ICON_URL = 4;
    final static int NOW = 5;

    WeatherCond(){
    }

    WeatherCond makeCopy(){
        WeatherCond a = new WeatherCond();
        for (int i = 0; i < param.length; i++){
            a.param[i] = this.param[i];
        }
        a.pic = this.pic;
        return a;
    }
    void clear(){
        param = new String[SqlTags.length];
        pic = null;
    }
}
