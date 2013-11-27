package com.example.weather;

import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class MySAXParser extends DefaultHandler {

    ArrayList<Object> array;
    String encode = "utf-8";
    String defaultEncoding = "ISO-8859-1";

    MySAXParser() {
        array = new ArrayList<Object>();
    }
}
