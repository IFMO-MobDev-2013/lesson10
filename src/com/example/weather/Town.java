package com.example.weather;

public class Town {
    int id;
    String woeid;
    String name;
    public static final String NAME = "name";
    public static final String WOEID = "woeid";
    public static final String _ID = "_id";

    Town() {
        id = 0;
        name = "";
        woeid = "";
    }

    Town(int _id, String _name, String _woeid) {
        id = _id;
        name = _name;
        woeid = _woeid;
    }

    public boolean fakeTown() {
        return ("".equals(woeid) || "".equals(name) || (null == woeid) || (null == name));
    }

}