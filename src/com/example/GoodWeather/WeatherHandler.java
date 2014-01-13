package com.example.GoodWeather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class WeatherHandler extends DefaultHandler {
    private final String MAXC = "tempMaxC";
    private final String MINC = "tempMinC";
    private final String WINDSPEED = "windspeedKmph";
    private final String WEATHER = "weatherDesc";
    private final String HUMIDITY = "humidity";
    private final String CODE = "weatherCode";
    private final String ENTRY = "weather";
    private final String CURRENT = "current_condition";
    private final String CURRENTTEMP = "temp_C";
    private final String DATE = "date";
    StringBuilder sb = null;

    String[] maxC, minC, windspeed, weather, humidity, date,code;
    int size = -1;

    WeatherHandler(String[] minC, String[] maxC, String[] weather, String[] windspeed, String[] humidity, String[] date, String[] code) {
        super();
        this.date = date;
        this.maxC = maxC;
        this.minC = minC;
        this.code = code;
        this.weather = weather;
        this.windspeed = windspeed;
        this.humidity = humidity;
        sb = new StringBuilder();
    }

    boolean tagEntry = false;
    String max = null, min = null, wind = null, wthr = null, hum = null, time = null, cc = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
        super.startElement(uri, localName, qName, attr);
        if (qName.equals(ENTRY) || qName.equals(CURRENT)) {
            tagEntry = true;
        } else {
            sb.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals(ENTRY) || qName.equals(CURRENT)) {
            size++;
            maxC[size] = max;
            minC[size] = min;
            windspeed[size] = wind;
            weather[size] = wthr;
            humidity[size] = hum;
            code[size] = cc;
            date[size] = time;
            tagEntry = false;

        } else if (qName.equals(MAXC) || qName.equals(CURRENTTEMP)) {
            max = sb.toString();
        } else if (qName.equals(MINC)) {
            min = sb.toString();
        } else if (qName.equals(WINDSPEED)) {
            wind = sb.toString();
        } else if (qName.equals(WEATHER)) {
            wthr = sb.toString();
        } else if (qName.equals(HUMIDITY)) {
            hum = sb.toString();
        } else if (qName.equals(DATE)) {
            time = sb.toString();
        } else if (qName.equals(CODE)) {
            cc = sb.toString();
    }
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        sb.append(ch, start, length);
    }

}

