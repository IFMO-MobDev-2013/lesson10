package com.example.GoodWeather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class CityHandler extends DefaultHandler {
    private final String LONGTITUDE = "longitude";
    private final String LATITUDE = "latitude";
    private final String COUNTRY = "country";
    private final String ENTRY = "result";
    StringBuilder sb = null;

    String[] longtitude, latitude, country;
    int size = -1;

    CityHandler(String[] longs, String[] lats, String[] countries) {
        super();
        this.longtitude = longs;
        this.latitude = lats;
        this.country = countries;
        sb = new StringBuilder();
    }

    boolean tagEntry = false;
    String longt = null;
    String latt = null;
    String coun = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
        super.startElement(uri, localName, qName, attr);
        if (qName.equals(ENTRY)) {
            tagEntry = true;
        } else {
            sb.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals(ENTRY)) {
            size++;
            longtitude[size] = longt;
            latitude[size] = latt;
            country[size] = coun;
            tagEntry = false;

        } else if (qName.equals(LONGTITUDE)) {
            longt = sb.toString();

        } else if (qName.equals(LATITUDE)) {
            latt = sb.toString();

        } else if (qName.equals(COUNTRY)) {
            coun = sb.toString();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        sb.append(ch, start, length);
    }

}

