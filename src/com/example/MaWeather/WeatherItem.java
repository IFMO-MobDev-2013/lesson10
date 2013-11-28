package com.example.MaWeather;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 21.11.13
 * Time: 18:48
 */
public class WeatherItem implements Parcelable, Serializable {

    private String weatherType;
    private String sunrise;
    private String sunset;
    private String temperature;
    private String humidity;
    private String pressure;
    private String pictureType;
    private String bigPictureType;

    public WeatherItem(String weatherType, String sunrise, String sunset, String temperature, String humidity, String pressure, String pictureType, String bigPictureType) {
        this.weatherType = weatherType;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.pictureType = pictureType;
        this.bigPictureType = bigPictureType;
    }

    public String getWeatherType() {
        return this.weatherType;
    }
    public String getSunrise() {
        return this.sunrise;
    }
    public String getSunset() {
        return this.sunset;
    }
    public String getTemperature() {
        return this.temperature;
    }
    public String getHumidity() {
        return this.humidity;
    }
    public String getPressure() {
        return this.pressure;
    }
    public String getPictureType() {
        return this.pictureType;
    }
    public String getBigPictureType() {
        return  this.bigPictureType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(weatherType);
        parcel.writeString(sunrise);
        parcel.writeString(sunset);
        parcel.writeString(temperature);
        parcel.writeString(humidity);
        parcel.writeString(pressure);
        parcel.writeString(pictureType);
        parcel.writeString(bigPictureType);
    }

    public static final Parcelable.Creator<WeatherItem> CREATOR = new Parcelable.Creator<WeatherItem>() {
        public WeatherItem createFromParcel(Parcel in) {
            return new WeatherItem(in);
        }

        public WeatherItem[] newArray(int size) {
            return new WeatherItem[size];
        }
    };

    public WeatherItem(Parcel parcel) {
        this.weatherType = parcel.readString();
        this.sunrise = parcel.readString();
        this.sunset = parcel.readString();
        this.temperature = parcel.readString();
        this.humidity = parcel.readString();
        this.pressure = parcel.readString();
        this.pictureType = parcel.readString();
        this.bigPictureType = parcel.readString();
    }
}
