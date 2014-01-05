package com.example.weather;


/**
 * Created with IntelliJ IDEA.
 * User: elena
 * Date: 02.01.14
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class Weather {
    int id;
    int city_id;
    int cloud_cover;
    float humidity;
    int pressure;
    String date;
    int temp_C;
    int temp_F;
    String weather_desc;
    String icon_url;
    String wind_dir;
    int wind_speed;
    int temp_min_C;
    int temp_max_C;
    byte[] image;
    boolean current;

    public Weather() {
        current = false;
    }

    boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean c) {
        current = c;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setImage(byte[] i) {
        image = i;
    }

    public void setCityID(int t) {
        city_id = t;
    }

    public void setCloudCover(int t) {
        cloud_cover = t;
    }

    public void setHumidity(float t) {
        humidity = t;
    }

    public void setPressure(int t) {
        pressure = t;
    }

    public void setDate(String t) {
        date = t;
    }

    public void setTempC(int t) {
        temp_C  = t;
    }

    public void setTempF(int t) {
        temp_F = t;
    }

    public void setWeatherDesc(String t) {
        weather_desc = t;
    }

    public void setIconURL(String t) {
        icon_url = t;
    }

    public void setWindDir(String t) {
        wind_dir = t;
    }

    public void setWindSpeed(int t) {
        wind_speed = t;
    }

    public void setTempMinC(int t) {
        temp_min_C = t;
    }

    public void setTempMaxC(int t) {
        temp_max_C = t;
    }

    public int getCityID() {
        return city_id;
    }

    public int getCloudCover() {
        return cloud_cover;
    }

    public float getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public String getDate() {
        return date;
    }

    public int getTempC() {
        return temp_C;
    }

    public int getTempF() {
        return temp_F;
    }

    public String getWeatherDesc() {
        return weather_desc;
    }

    public String getIconURL() {
        return icon_url;
    }

    public String getWindDir() {
        return wind_dir;
    }

    public int getWindSpeed() {
        return wind_speed;
    }

    public int getTempMinC() {
        return temp_min_C;
    }

    public int getTempMaxC() {
        return temp_max_C;
    }

    public byte[] getImage() {
        return image;
    }
}
