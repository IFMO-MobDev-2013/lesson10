package ru.ifmo.Weather;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 20.11.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class WeatherCond {
    final static String[] nowTags = new String[]{"temp_C", "pressure", "weatherIconUrl"};
    final static String[] forecastTags = new String[]{"tempMinC", "tempMaxC", "weatherIconUrl"};


    final boolean now;
    final String[] tags;
    String[] param;

    final static int TEMP_NOW = 0;
    final static int PRESURE_NOW = 1;

    final static int TEMP_MIN = 0;
    final static int TEMP_MAX = 1;

    final int ICON_URL;

    WeatherCond(boolean cur){
        now = cur;
        if (cur){
            tags = nowTags;
            ICON_URL = 2;
        } else {
            tags = forecastTags;
            ICON_URL = 2;
        }

        param = new String[tags.length];
    }

    WeatherCond makeCopy(){
        WeatherCond a = new WeatherCond(now);
        for (int i = 0; i < param.length; i++){
            a.param[i] = this.param[i];
        }
        return a;
    }
    void clear(){
        param = new String[tags.length];
    }
}
