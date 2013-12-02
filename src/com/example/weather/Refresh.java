package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Refresh {

    ArrayList<WeatherAll> Records = new ArrayList<WeatherAll>();
    Context context;
    String TownName = "";
    boolean f = false;

    class MySAXParser extends DefaultHandler {
        String element = null;
        boolean flag = false;
        String day = "";
        String part = "";

        @Override
        public void startDocument() {
            Records = new ArrayList<WeatherAll>();
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            element = localName;
            if (localName.equals("day")) {
                day = atts.getValue(atts.getIndex("date"));
            }
            if (localName.equals("day_part")) {
                part = atts.getValue(atts.getIndex("type"));
                Records.add(new WeatherAll());
                Records.get(Records.size() - 1).date = day;
                Records.get(Records.size() - 1).part_type = part;
                flag = true;
            }
            if (localName.equals("fact")) {
                flag = true;
                Records.add(new WeatherAll());
                Records.get(Records.size() - 1).date = "full";
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            if (localName.equals("day_part") || localName.equals("fact")) {
                flag = false;
            }

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String s = new String(ch, start, length);
            try {
                if (Records.size() > 0 && flag) {
                    if (element.equals("image")) {
                        Records.get(Records.size() - 1).image += s;
                    } else if (element.equals("temperature_from") || element.equals("temperature")) {
                        Records.get(Records.size() - 1).temperature_from += Integer.parseInt(s);
                    } else if (element.equals("temperature_to")) {
                        Records.get(Records.size() - 1).temperature_to += Integer.parseInt(s);
                    } else if (element.equals("weather_type")) {
                        Records.get(Records.size() - 1).weather_type += s;
                    } else if (element.equals("wind_speed")) {
                        Records.get(Records.size() - 1).wind += Double.parseDouble(s);
                    } else if (element.equals("humidity")) {
                        Records.get(Records.size() - 1).humidity += Integer.parseInt(s);
                    } else if (element.equals("pressure")) {
                        Records.get(Records.size() - 1).pressure += Integer.parseInt(s);
                    }

                }
            } catch (Exception e) {
            }
        }
    }

    void download(String url_S) throws ParserConfigurationException, SAXException, IOException {
        URL url;
        URLConnection conn = null;
        InputStream inputStream = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(new MySAXParser());
        boolean f = false;
        try {
            url = new URL(url_S);
            conn = url.openConnection();
            conn.connect();
            inputStream = conn.getInputStream();
            if (inputStream != null) {
                InputSource inputSource = new InputSource(inputStream);
                xmlReader.parse(inputSource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f == true) {
                if (inputStream != null)
                    inputStream.close();

            }

        }
    }

    public Refresh(Context c, String name) {
        context = c;
        TownName = name;
    }

    public void update_channel(String url) {
        DBHelper DB = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = DB.getWritableDatabase();
        try {
            download(url);
            Log.d("size1", new Integer(Records.size()).toString());
            for (int i = 0; i < Records.size(); i++) {
                if (Records.get(i).date.equals("full")) {
                    Log.d("asd", Records.get(i).weather_type);
                }
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.TOWN_NAME, TownName);
                cv.put(DBHelper.DAY, Records.get(i).date);
                cv.put(DBHelper.PART, Records.get(i).part_type);
                cv.put(DBHelper.TEMPERATURE_FROM, Records.get(i).temperature_from);
                cv.put(DBHelper.TEMPERATURE_TO, Records.get(i).temperature_to);
                cv.put(DBHelper.IMAGE, Records.get(i).image);
                cv.put(DBHelper.WEATHER_TYPE, Records.get(i).weather_type);
                cv.put(DBHelper.WIND, Records.get(i).wind);
                cv.put(DBHelper.HUMIDITY, Records.get(i).humidity);
                cv.put(DBHelper.PRESSURE, Records.get(i).pressure);
                sqLiteDatabase.insert(DBHelper.DATABASE_NAME, null, cv);
            }
        } catch (ParserConfigurationException e) {

        } catch (SAXException e) {

        } catch (IOException e) {

        } finally {
            sqLiteDatabase.close();
            DB.close();
        }
    }

}
