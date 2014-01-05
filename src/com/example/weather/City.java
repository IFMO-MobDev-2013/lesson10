package com.example.weather;


public class City {
    int id;
    String city;
    String created_at;

    public City() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String c) {
        city = c;
    }

    public void setCreatedAt(String date) {
        created_at = date;
    }
    public String getCity() {
        return city;
    }

    public int getId() {
        return id;
    }
}
