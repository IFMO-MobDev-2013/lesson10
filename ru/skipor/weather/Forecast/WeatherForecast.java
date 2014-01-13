package ru.skipor.weather.Forecast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vladimir Skipor on 1/7/14.
 * Email: vladimirskipor@gmail.com
 */
public class WeatherForecast {
    private final static String TAG = "WeatherForecast";
    private static final String DATE_PATTERN = "HH:mm aa";
    public static final String CLOUD_COVER_TAG = "cloudcover";
    public static final String CITY_NAME_TAG = "query"; //
    public static final String HUMIDITY_TAG = "humidity";
    public static final String OBSERVATION_TIME_TAG = "observation_time";
    public static final String PRECIPITATION_TAG = "precipMM";
    public static final String PRESSURE_TAG = "pressure";
    public static final String TEMP_C_TAG = "temp_C"; //
    public static final String VISIBILITY_TAG = "visibility";
    public static final String WEATHER_DESCRIPTION_TAG = "weatherDesc"; //
    public static final String WEATHER_ICON_URL_TAG = "weatherIconUrl"; //
    public static final String WIND_DIRECTION_TAG = "winddir16Point";
    public static final String WIND_SPEED_TAG = "windspeedKmph";
    public static final String DATE_TAG = "date";
    public static final String TEMP_MIN_C_TAG = "tempMinC";
    public static final String TEMP_MAX_C_TAG = "tempMaxC";
    private static SimpleDateFormat inputCurrentDateFormat = new SimpleDateFormat(DATE_PATTERN);

    public String cityName;
    private Date observingTime;
    public int tempC;
    public int windSpeedKmph;
    public String windDirection;
    public String weatherDescription;
    public String weatherIconURL;
    public double precipitationMM;
    public int humidityPercent;
    public int visibilityKm;
    public int pressureMillibars;
    public int cloudCoverPercent;
    public final ArrayList<DayForecast> dayForecasts;

    public void setObservingTime(String date) throws ParseException {
        this.observingTime = inputCurrentDateFormat.parse(date);
    }

    public Date getObservingTime() {
        return observingTime;
    }


    public WeatherForecast() {
        dayForecasts = new ArrayList<DayForecast>();

    }

    public static class DayForecast {



        private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private static SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd.MM");
        private Date date; //
        public int tempMaxC;//
        public int tempMinC;//
        public int windSpeedKmph;
        public String windDirection;
        public String weatherDescription;
        public String weatherIconURL; //
        public double precipitationMM;

        public String  getDayOfWeek() {
            return dayOfWeekFormat.format(date);
        }

        public String getDayAndMonth() {
            return outputDateFormat.format(date);
        }

        public void setDate(String date) throws ParseException {
            this.date = simpleDateFormat.parse(date);
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return "DayForecast{" +
                    "date=" + date +
                    ", tempMaxC=" + tempMaxC +
                    ", tempMinC=" + tempMinC +
                    ", windSpeedKmph=" + windSpeedKmph +
                    ", windDirection='" + windDirection + '\'' +
                    ", weatherDescription='" + weatherDescription + '\'' +
                    ", weatherIconURL='" + weatherIconURL + '\'' +
                    ", precipitationMM=" + precipitationMM +
                    '}';
        }
    }

    @Override
    public String toString() {
        String current = "WeatherForecast{" +
                "observingTime=" + observingTime +
                ", tempC=" + tempC +
                ", windSpeedKmph=" + windSpeedKmph +
                ", windDirection='" + windDirection + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", weatherIconURL='" + weatherIconURL + '\'' +
                ", precipitationMM=" + precipitationMM +
                ", humidityPercent=" + humidityPercent +
                ", visibilityKm=" + visibilityKm +
                ", pressureMillibars=" + pressureMillibars +
                ", cloudCoverPercent=" + cloudCoverPercent +
                '}';
        for(DayForecast dayForecast : dayForecasts) {
            current += dayForecast.toString();
        }
        return current;
    }

}
