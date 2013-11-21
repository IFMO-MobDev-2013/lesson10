package ru.georgeee.android.singingintherain.model;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 2:30
 * To change this template use File | Settings | File Templates.
 */
public class City {
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    protected String latitude = "30.295783333333333";
    protected String longitude = "59.92545";

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
