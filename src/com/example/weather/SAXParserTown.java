package com.example.weather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class SAXParserTown extends MySAXParser {

    public class BigTown {
        String woeid;
        HashMap<String, String> map;
        ArrayList<String> states;

        BigTown() {
            map = new HashMap<String, String>();
            states = new ArrayList<String>();
            woeid = "";
        }

        BigTown(BigTown bigTown1) {
            map = new HashMap<String, String>(bigTown1.map);
            states = new ArrayList<String>(bigTown1.states);
            woeid = bigTown1.woeid;
        }
    }

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
    BigTown bigTown;

    @Override
    public void startDocument() throws SAXException {
        array = new ArrayList<Object>();
        System.out.println("Start document town");
        currentElement = null;
        bigTown = new BigTown();
    }

    @Override
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {

        if ("place".equals(local_name)) {
        } else if (types.contains(local_name)) {
            currentType = amap.getValue(amap.getIndex("type"));
        }
        currentElement = local_name;
    }

    @Override
    public void endElement(String uri, String local_name, String qName) throws SAXException {

        if ("place".equals(local_name)) {
            array.add(bigTown);
            bigTown = new BigTown();
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
            bigTown.woeid = value;
        else if (types.contains(currentElement)) {
            bigTown.map.put(currentType, value);
            bigTown.states.add(currentType);
        }
    }

    //TODO     spaces

    @Override
    public void endDocument() throws SAXException {
        ArrayList<Object> array_temp = new ArrayList<Object>(array);
        array = new ArrayList<Object>();
        System.out.println("End document town");
        currentElement = null;
        for (int i = 0; i < array_temp.size(); i++) {
            Town town = new Town();
            bigTown = (BigTown) array_temp.get(i);
            town.woeid = bigTown.woeid;
            if(!bigTown.states.isEmpty())
                town.name = bigTown.map.get(bigTown.states.get(bigTown.states.size() - 1));
            for (int j = 0; j < bigTown.states.size(); j++) {
                boolean unique = true;
                for (int h = 0; h < array_temp.size(); h++)
                    if ((h != i) && ((BigTown) array_temp.get(h)).map.containsKey(bigTown.states.get(j)) &&
                            ((BigTown) array_temp.get(h)).map.get(bigTown.states.get(j)).equals(bigTown.map.get(bigTown.states.get(j)))) {
                        unique = false;
                        break;
                    }

                if (town.longName == null || "".equals(town.longName))
                    town.longName += bigTown.states.get(j) + " - " + bigTown.map.get(bigTown.states.get(j));
                else
                    town.longName += "\r\n" + bigTown.states.get(j) + " - " + bigTown.map.get(bigTown.states.get(j));
                if (unique)
                    break;
            }
            array.add(town);
            System.out.println("new town" + town.name);
        }
    }

}
