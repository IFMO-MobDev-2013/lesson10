package com.example.weather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SAXParserTown extends MySAXParser {

    static ArrayList<String> types = new ArrayList<String>();

    static {
        types.add("country");
        types.add("admin1");
        types.add("admin2");
        types.add("admin3");
        types.add("locality1");
        types.add("locality2");
    }

    public String currentElement = null;
    public String currentType = null;
    Town town;

    @Override
    public void startDocument() throws SAXException {
        array = new ArrayList<Object>();
        System.out.println("Start document town");
        currentElement = null;
        town = new Town();
    }

    @Override
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {

        if ("place".equals(local_name)) {
            array.add(new Town());
        } else if (types.contains(local_name)) {
            currentType = amap.getValue(amap.getIndex("type"));
        }
        currentElement = local_name;
    }

    @Override
    public void endElement(String uri, String local_name, String qName) throws SAXException {

        if ("place".equals(local_name)) {
            while (town.name.charAt(town.name.length() - 1) == '\n' ||
                    town.name.charAt(town.name.length() - 1) == '\r' ||
                    town.name.charAt(town.name.length() - 1) == ' ')
                town.name = town.name.substring(0, town.name.length() - 1);
            array.add(town);
            town = new Town();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        //TODO   maybe good default encoding

        String valueOld = new String(ch, start, length);
        String value = null;
        try {
            value = new String(valueOld.getBytes(defaultEncoding), encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if ("".equals(value) || value == null)
            return;
        if ("woeid".equals(currentElement))
            town.woeid = value;
        else if (types.contains(currentElement))
            town.name += currentType + " - " + value + " \r\n";
    }

}
