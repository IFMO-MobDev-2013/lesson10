package com.example.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: Charm
 * Date: 21.11.13
 * Time: 9:24
 * To change this template use File | Settings | File Templates.
 */
public class DetailsDay implements Parcelable{
    private String day,date,low,high,sky;

    DetailsDay() {
        day = date = low = high = sky = null;
    }

    private DetailsDay(Parcel parcel) {
        this.day = parcel.readString();
        this.date = parcel.readString();
        this.low = parcel.readString();
        this.high = parcel.readString();
        this.sky = parcel.readString();
    }

    void setDay(String day) {
        this.day = day;
    }
    void setDate(String date) {
        this.date = date;
    }
    void setLow(String low) {
        this.low = low;
    }
    void setHigh(String high) {
        this.high = high;
    }
    void setSky(String sky) {
        this.sky = sky;
    }

    String getDay() {
        return day;
    }

    String getDate() {
        return date;
    }

    String getLow() {
        return low;
    }
    String getHigh() {
        return high;
    }
    String getSky() {
        return sky;
    }
    String getAll() {
        return "\n\n" + day+ "\n" + date+ "\n" + low + "°C.." + high + "°C\n" + sky;
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //To change body of implemented methods use File | Settings | File Templates.
        dest.writeString(day);
        dest.writeString(date);
        dest.writeString(low);
        dest.writeString(high);
        dest.writeString(sky);
    }

    public static final Parcelable.Creator<DetailsDay> CREATOR = new Parcelable.Creator<DetailsDay>() {
        public DetailsDay createFromParcel(Parcel in) {
            return new DetailsDay(in);
        }

        public DetailsDay[] newArray(int size) {
            return new DetailsDay[size];
        }
    };
}
