package ru.georgeee.android.singingintherain.model;

import android.util.Log;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class Forecast implements Serializable{
    DataBlock daily;
    DataBlock hourly;
    DataBlock minutely;
    DataPoint currently;


    public void setDaily(JsonObject dailyObject)  {
        this.daily = new DataBlock(dailyObject);
    }
    public void setHourly(JsonObject hourlyObject)  {
        this.hourly = new DataBlock(hourlyObject);
    }
    public void setMinutely(JsonObject minutelyObject)  {
        this.minutely = new DataBlock(minutelyObject);
    }
    public void setCurrently(JsonObject currentlyObject){
        this.currently = new DataPoint(currentlyObject);
    }

    public DataBlock getDaily() {
        return daily;
    }

    public DataBlock getHourly() {
        return hourly;
    }

    public DataBlock getMinutely() {
        return minutely;
    }

    public DataPoint getCurrently() {
        return currently;
    }

    static final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    protected static Date getDate(JsonObject jsonObject, String key){
        try {
            String dateStr = jsonObject.get(key).asString();
            return timeFormat.parse(dateStr);
        } catch (NullPointerException e) {
            Log.e(DataPoint.class.getCanonicalName(), e.toString());
        } catch (ParseException e) {
            Log.e(DataPoint.class.getCanonicalName(), e.toString());
        }
        return null;
    }


    protected static String getString(JsonObject jsonObject, String key){
        try {
            return jsonObject.get(key).asString();
        } catch (NullPointerException e) {
            Log.e(DataPoint.class.getCanonicalName(), e.toString());
        }
        return null;
    }
    protected static Double getDouble(JsonObject jsonObject, String key){
        try {
            return jsonObject.get(key).asDouble();
        } catch (NullPointerException e) {
            Log.e(DataPoint.class.getCanonicalName(), e.toString());
        }
        return null;
    }


    public static class DataPoint{

        protected Date time;
        protected String summary;
        protected String iconId;
        protected Date sunriseTime;
        protected Date sunsetTime;
        protected Double precipIntensity;
        protected Double precipIntensityMax;
        protected Double precipIntensityMaxTime;
        protected Double precipProbability;
        protected String precipType;
        protected Double precipAccumulation;
        protected Double temperature;
        protected Double temperatureMin;
        protected Double temperatureMinTime;
        protected Double temperatureMax;
        protected Double temperatureMaxTime;
        protected Double apparentTemperature;
        protected Double apparentTemperatureMin;
        protected Double apparentTemperatureMinTime;
        protected Double apparentTemperatureMax;
        protected Double apparentTemperatureMaxTime;
        protected Double dewPoint;
        protected Double windSpeed;
        protected Double windBearing;
        protected Double cloudCover;
        protected Double humidity;
        protected Double pressure;
        protected Double visibility;
        protected Double ozone;

        public DataPoint(JsonObject jsonObject) {
            time = getDate(jsonObject, "time");
            summary = getString(jsonObject, "summary");
            iconId = getString(jsonObject, "icon");
            sunriseTime = getDate(jsonObject, "sunriseTime");
            sunsetTime = getDate(jsonObject, "sunsetTime");
            precipIntensity = getDouble(jsonObject, "precipIntensity");
            precipIntensityMax = getDouble(jsonObject, "precipIntensityMax");
            precipIntensityMaxTime = getDouble(jsonObject, "precipIntensityMaxTime");
            precipProbability = getDouble(jsonObject, "precipProbability");
            precipType = getString(jsonObject, "precipType");
            precipAccumulation = getDouble(jsonObject, "precipAccumulation");
            temperature = getDouble(jsonObject, "temperature");
            temperatureMin = getDouble(jsonObject, "temperatureMin");
            temperatureMinTime = getDouble(jsonObject, "temperatureMinTime");
            temperatureMax = getDouble(jsonObject, "temperatureMax");
            temperatureMaxTime = getDouble(jsonObject, "temperatureMaxTime");
            apparentTemperature = getDouble(jsonObject, "apparentTemperature");
            apparentTemperatureMin = getDouble(jsonObject, "apparentTemperatureMin");
            apparentTemperatureMinTime = getDouble(jsonObject, "apparentTemperatureMinTime");
            apparentTemperatureMax = getDouble(jsonObject, "apparentTemperatureMax");
            apparentTemperatureMaxTime = getDouble(jsonObject, "apparentTemperatureMaxTime");
            dewPoint = getDouble(jsonObject, "dewPoint");
            windSpeed = getDouble(jsonObject, "windSpeed");
            windBearing = getDouble(jsonObject, "windBearing");
            cloudCover = getDouble(jsonObject, "cloudCover");
            humidity = getDouble(jsonObject, "humidity");
            pressure = getDouble(jsonObject, "pressure");
            visibility = getDouble(jsonObject, "visibility");
            ozone = getDouble(jsonObject, "ozone");
        }


    }

    public static class DataBlock{

        public DataBlock(JsonObject jsonObject) {
            summary = getString(jsonObject, "summary");
            iconId = getString(jsonObject, "icon");
            JsonValue _dataPointsValue = jsonObject.get("data");
            if(!_dataPointsValue.isArray()) dataPoints = new DataPoint[]{};
            else{
                JsonArray _dataPoints = _dataPointsValue.asArray();
                dataPoints = new DataPoint[_dataPoints.size()];
                for(int i=0; i<_dataPoints.size(); ++i){
                    dataPoints[i] = new DataPoint(_dataPoints.get(i).asObject());
                }

            }
        }

        protected String iconId;
        protected String summary;
        protected DataPoint[] dataPoints;

        public String getIconId() {
            return iconId;
        }

        public String getSummary() {
            return summary;
        }

        public DataPoint[] getDataPoints() {
            return dataPoints;
        }

        public DataPoint get(int i){
            return dataPoints[i];
        }
    }

}
