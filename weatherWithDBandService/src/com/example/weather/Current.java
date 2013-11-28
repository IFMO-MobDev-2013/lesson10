package com.example.weather;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 28.11.13
 * Time: 1:57
 * To change this template use File | Settings | File Templates.
 */
public class Current {
    public String tempC;
    public String wind;
    public String descr;
    public String observed;
    public int image;

    public Current(String tempC, String wind, String descr, String observed, int image_code) {
        this.tempC = tempC;
        this.wind = wind;
        this.descr = descr;
        this.observed = observed;
        this.image = Mapper.map(image_code);
    }
}
