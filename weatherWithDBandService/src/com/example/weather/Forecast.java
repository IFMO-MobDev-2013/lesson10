package com.example.weather;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 28.11.13
 * Time: 2:07
 * To change this template use File | Settings | File Templates.
 */
public class Forecast {
    public String tempMin;
    public String tempMax;
    public String wind;
    public String descr;
    //public String humidity;
    public String date;
    public int image;

    public Forecast(
            String tempMin,
            String tempMax,
            String wind,
            String descr,
            //String humidity,
            String date,
            int image_id
    ) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.wind = wind;
        this.descr = descr;
       // this.humidity = humidity;
        this.date = date;
        this.image = Mapper.map(image_id);
    }
}
