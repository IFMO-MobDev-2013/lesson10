package com.example.weather;

/**
 * Created by Charm on 05.01.14.
 */
public class IdCity {
    private String city;
    private String id;
    private String country;
    private String region;

    IdCity(String city,String id,String country,String region) {
        this.id = id;
        this.city = city;
        this.country = country;
        this.region = region;
    }

    public String get() {
        return city + "\n" + country + "\n" + region;
    }

    public String getId(){
        return id;
    }
    public String getName() {return city;}
}
