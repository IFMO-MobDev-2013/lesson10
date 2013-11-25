package com.polarnick.day09.entities;

import com.forecastiolib.FIODataPoint;
import com.google.common.collect.Lists;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
@DatabaseTable(tableName = "forecastsData")
public class ForecastData {

    public static final int NOT_DEFINED = Integer.MAX_VALUE;
    public static final String DEFAULT_PRECIP_TYPE = "rain";

    private static final List<String> SUPPORTED_ICON_TYPES = Lists.newArrayList("clear-day", "clear-night", "rain", "snow",
            "sleet", "wind", "fog", "cloudy", "partly-cloudy-day", "partly-cloudy-night");
    private static final List<String> SUPPORTED_PRECIP_TYPES = Lists.newArrayList("rain", "snow", "sleet");
    private static final String DEFAULT_ICON_TYPE = "cloudy";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private ForecastForCity forecastForCity;

    @DatabaseField
    private boolean isHour;
    @DatabaseField()
    private String summary;
    @DatabaseField()
    private String iconType;
    @DatabaseField()
    private long time;

    @DatabaseField()
    private int temperature;
    @DatabaseField()
    private int minTemperature;
    @DatabaseField()
    private int maxTemperature;

    @DatabaseField()
    private int precipProbability;
    @DatabaseField()
    private double precipIntensity;
    @DatabaseField()
    private String precipType;

    @DatabaseField()
    private int windSpeed;
    @DatabaseField()
    private int windBearing;

    public ForecastData() {
    }

    public ForecastData(FIODataPoint dataPoint) {
        summary = dataPoint.getByKey("summary");
        iconType = dataPoint.getByKey("icon");
        if (!SUPPORTED_ICON_TYPES.contains(iconType)) {
            iconType = DEFAULT_ICON_TYPE;
        }
        iconType = iconType.replace('-', '_');
        time = Long.parseLong(dataPoint.getByKey("time"));
        if ("null".equals(dataPoint.getByKey("temperature"))) {
            minTemperature = (int) Math.round(Double.parseDouble(dataPoint.getByKey("temperatureMin")));
            maxTemperature = (int) Math.round(Double.parseDouble(dataPoint.getByKey("temperatureMax")));
            temperature = (maxTemperature + minTemperature) / 2;
        } else {
            minTemperature = NOT_DEFINED;
            maxTemperature = NOT_DEFINED;
            temperature = (int) Math.round(Double.parseDouble(dataPoint.getByKey("temperature")));
        }
        precipIntensity = Double.parseDouble(dataPoint.getByKey("precipIntensity"));
        precipProbability = (int) Math.round(Double.parseDouble(dataPoint.getByKey("precipProbability")) * 100);
        if (precipIntensity != 0) {
            precipType = dataPoint.getByKey("precipType");
            if ("null".equals(precipType)) {
                precipType = "rain";
            }
        }
        windSpeed = (int) Math.round(Double.parseDouble(dataPoint.getByKey("windSpeed")));
        if (windSpeed != 0) {
            windBearing = Integer.parseInt(dataPoint.getByKey("windBearing"));
        }
    }

    public ForecastData(ForecastData... toMerge) {
        final ForecastData first = toMerge[0];
        summary = first.getSummary();
        iconType = first.getIconType();
        time = first.getTime();
        minTemperature = Integer.MAX_VALUE;
        maxTemperature = Integer.MIN_VALUE;
        temperature = 0;
        precipIntensity = 0;
        precipProbability = 0;
        windSpeed = first.getWindSpeed();
        windBearing = first.getWindBearing();
        for (ForecastData data : toMerge) {
            minTemperature = Math.min(minTemperature, data.getMinTemperature());
            maxTemperature = Math.max(maxTemperature, data.getMaxTemperature());
            temperature += data.getTemperature();
            precipIntensity += data.getPrecipIntensity();
            precipProbability += data.getPrecipProbability();
        }
        temperature /= toMerge.length;
        precipIntensity /= toMerge.length;
        precipProbability /= toMerge.length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ForecastForCity getForecastForCity() {
        return forecastForCity;
    }

    public void setForecastForCity(ForecastForCity forecastForCity) {
        this.forecastForCity = forecastForCity;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getPrecipProbability() {
        if (precipType == null && !SUPPORTED_PRECIP_TYPES.contains(iconType)) {
            return 0;
        }
        return precipProbability;
    }

    public void setPrecipProbability(int precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getPrecipIntensity() {
        if (precipType == null && !SUPPORTED_PRECIP_TYPES.contains(iconType)) {
            return 0;
        }
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public String getPrecipType() {
        if (precipType == null) {
            if (SUPPORTED_PRECIP_TYPES.contains(iconType)) {
                return iconType;
            } else {
                return "null";
            }
        }
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(int windBearing) {
        this.windBearing = windBearing;
    }

    public boolean isHour() {
        return isHour;
    }

    public void setHour(boolean hour) {
        isHour = hour;
    }
}
