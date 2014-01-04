package com.example.Weather;

public class Weather {

    String date;
    int lowTemp;
    int maxTemp;
    int windSpeed;
    String windDirect;
    int weatherCode;
    String weatherDescription;

    Weather(String date, int lowTemp, int maxTemp, int windSpeed, String windDirect, int weatherCode, String weatherDescription) {
        this.date = date;
        this.lowTemp = lowTemp;
        this.maxTemp = maxTemp;
        this.windSpeed = windSpeed;
        this.windDirect = windDirect;
        this.weatherCode = weatherCode;
        this.weatherDescription = weatherDescription;
    }

    @Override
    public String toString() {
        return date + "\n" + (lowTemp) + " " + (maxTemp) + "\n" + windSpeed + "\n" + windDirect;
    }
}
