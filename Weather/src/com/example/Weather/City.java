package com.example.Weather;

public class City {
    public String name;
    int id;

    City() {
        this.name = "";
        this.id = 0;
    }

    City(String name, int id) {
        this.name = name;
        this.id = id;
    }

    City(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
