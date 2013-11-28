package ru.georgeee.android.singingintherain.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 2:30
 * To change this template use File | Settings | File Templates.
 */
@DatabaseTable(tableName = "cities")
public class City implements Serializable {

    @DatabaseField(generatedId = true)
    protected int id;
    @DatabaseField(unique = true)
    protected String name;
    @DatabaseField
    protected String latitude;
    @DatabaseField
    protected String longitude;
    @DatabaseField (dataType= DataType.SERIALIZABLE)
    protected Forecast forecast;
    @DatabaseField
    protected Date forecastLastUpdated;

    public Date getForecastLastUpdated() {
        return forecastLastUpdated;
    }

    public void setForecastLastUpdated(Date forecastLastUpdated) {
        this.forecastLastUpdated = forecastLastUpdated;
    }

    public City(String name) {
        this.name = name;
    }

    public City() {
    }

    @Override
    public int hashCode() {
        return 0x63abe93f^id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;

        City city = (City) o;

        if (id != city.id) return false;

        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public static List<City> loadAllFromDB() {
        return DatabaseHelperHolder.getHelper().getCitiesDataDao().queryForAll();
    }

    public void save() {
        DatabaseHelperHolder.getHelper().getCitiesDataDao().createOrUpdate(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public void delete() {
        DatabaseHelperHolder.getHelper().getCitiesDataDao().delete(this);
    }

    public City getUpdated(){
        return DatabaseHelperHolder.getHelper().getCitiesDataDao().queryForSameId(this);
    }
}
