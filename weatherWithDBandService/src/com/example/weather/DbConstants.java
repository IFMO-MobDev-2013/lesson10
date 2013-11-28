package com.example.weather;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 17.11.13
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */
public interface DbConstants {

    public static final String DATABASE_NAME = "MyWeatherDatabase";

    public static final String TABLE_CITIES = "cities";
    public static final String TABLE_FORECAST = "forecast";
    public static final String TABLE_CURRENT = "current";


    // to see the differents in responces
    public static final String KEY_WEATHER = "weather";
    public static final String KEY_CC = "current_condition";

    // common
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CITYID = "city_id";
    public static final String KEY_DATE = "date";

    public static final String KEY_WTHDESC = "weatherDesc";
    public static final String KEY_WTHCODE = "weatherCode";
    public static final String KEY_WTHICON = "weatherIconUrl";
    public static final String KEY_WSK = "windspeedKmph";



    //cities
    public static final String KEY_NAME = "name";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";



// other are in ParsingConstants.   WARNING!!! COPYPASTE!


    // current
    public static final String KEY_OBSTIME = "observation_time";
    public static final String KEY_TEMPC = "temp_C";


    //forecast
    public static final String KEY_HUMID = "humidity";    // Humidity in percentage, Float
    public static final String KEY_TEMPMAXC = "tempMaxC";
    public static final String KEY_TEMPMINC = "tempMinC";


//    public static final String KEY_IMAGE = "image";
//    public static final String KEY_CODE = "code";
//    public static final String TABLE_IMG = "images";


//-----------------------------------------------------------------------------------------


    public static final String CREATE_TABLE_CITIES = "CREATE TABLE IF NOT EXISTS " + TABLE_CITIES + " ( " +
            KEY_ROWID + " integer primary key autoincrement, " +
            KEY_NAME + " text not null, " +
            KEY_LAT + " text, " +
            KEY_LONG + " text " +
            "); ";

    public static final String CREATE_TABLE_FORECAST = "CREATE TABLE IF NOT EXISTS " + TABLE_FORECAST + " ( " +
            KEY_ROWID + " integer primary key autoincrement, " +
            KEY_CITYID + " integer not null, " +
            KEY_DATE + " text, " +
            KEY_TEMPMAXC + " text, " +
            KEY_TEMPMINC + " text, " +
            KEY_HUMID + " text, " +
            KEY_WSK + " text, " +
            KEY_WTHDESC + " text not null, " +
            KEY_WTHCODE + " text, " +
            KEY_WTHICON + " text " +
            ");";

    public static final String CREATE_TABLE_CURRENT = "CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT + " ( " +
            KEY_ROWID + " integer primary key autoincrement, " +
            KEY_CITYID + " integer not null, " +
            KEY_TEMPC + " text, " +
            KEY_OBSTIME + " text, " +
            KEY_WSK + " text, " +
            KEY_WTHCODE + " text not null, " +
            KEY_WTHDESC + " text not null, " +
            KEY_WTHICON + " text " +
            ");";

//    public static final String CREATE_TABLE_IMG = "CREATE TABLE IF NOT EXISTS " + TABLE_IMG + " ( " +
//            KEY_ROWID + " integer primary key autoincrement, " +
//            KEY_WTHCODE + " integer not null, " +
//            KEY_IMAGE + " blob not null " +
//            ");";
}
