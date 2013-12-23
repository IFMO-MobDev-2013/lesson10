package ru.ifmo.ctddev.isaev.WeatherForecast.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * User: Xottab
 * Date: 05.12.13
 */
@DatabaseTable(tableName = "city")
public class City implements Serializable{

    public final static String CITY_NAME = "name";
    public final static String LAT_NAME = "latitude";
    public final static String LONG_NAME = "longitude";
    private static final String COUNTRY = "country";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = CITY_NAME)
    private String name;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COUNTRY)
    private String country;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = LAT_NAME)
    private double latitude;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = LONG_NAME)
    private double longitude;

    public City(String name, String country, Double latitude, Double longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public City() {
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
