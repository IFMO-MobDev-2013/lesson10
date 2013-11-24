package com.polarnick.day09.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
@DatabaseTable(tableName = "cities")
public class City implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private transient ForecastForCity forecast;

    @DatabaseField()
    private String name;
    @DatabaseField()
    private String latitude;
    @DatabaseField()
    private String longitude;

    public City() {
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public ForecastForCity getForecast() {
        return forecast;
    }

    public int getId() {
        return id;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setForecast(ForecastForCity forecast) {
        this.forecast = forecast;
    }

    public void setId(int id) {
        this.id = id;
    }
}
