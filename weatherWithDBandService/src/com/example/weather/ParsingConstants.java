package com.example.weather;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 17.11.13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
public interface ParsingConstants {
    public static final String KEY_DATA = "data";
    public static final String KEY_REQUEST =  "request";
    public static final String KEY_QUERY = "query";

    public static final String KEY_TYPE = "type";           // will be used two different ways

    public static final String KEY_WEATHER = "weather";
    public static final String KEY_CC = "current_condition";

    public static final String KEY_OBSTIME = "observation_time";
    public static final String KEY_TEMPC = "temp_C";
    public static final String KEY_TEMPF = "temp_F";
    public static final String KEY_HUMID = "humidity";    // Humidity in percentage, Float
    public static final String KEY_VISIBL = "visibility";  // Visibility in kilometres
    public static final String KEY_PRESS = "pressure";     //Atmospheric pressure in millibars
    public static final String KEY_CLOUD = "cloudcover";   //Cloud cover in percentage

    public static final String KEY_DATE = "date";
    public static final String KEY_TEMPMAXC = "tempMaxC";
    public static final String KEY_TEMPMAXF = "tempMaxF";
    public static final String KEY_TEMPMINC = "tempMinC";
    public static final String KEY_TEMPMINF = "tempMinF";

    public static final String KEY_WSM = "windspeedMiles";
    public static final String KEY_WSK = "windspeedKmph";
    public static final String KEY_WD16 = "winddir16Point";   // wtf?
    public static final String KEY_WDD = "winddirDegree";
    public static final String KEY_WTHCODE = "weatherCode";  // what's the code
    public static final String KEY_WTHICON = "weatherIconUrl";
    public static final String KEY_WTHDESC = "weatherDesc";
    public static final String KEY_PRCMM = "precipMM";      // what is it
}
