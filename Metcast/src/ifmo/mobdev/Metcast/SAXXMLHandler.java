package ifmo.mobdev.Metcast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class SAXXMLHandler extends DefaultHandler {
    public static final String DATE = "date";
    public static final String CUR_TEMP = "temp_C";
    public static final String CUR_WIND = "windspeedKmph";
    public static final String DESCR = "weatherDesc";
    public static final String CUR_HUM = "humidity";
    public static final String CUR_PRESS = "pressure";
    public static final String MAX_TEMP = "tempMaxC";
    public static final String MIN_TEMP = "tempMinC";
    public static final String ITEM = "weather";
    public static final String CUR_ITEM = "current_condition";
    public static final String IMG_ID = "weatherCode";
    public static final String CUR_TIME = "observation_time";


    private ArrayList<HashMap<String, String>> items;
    private String tempVal;
    private StringBuffer buffer;
    private HashMap<String, String> item;
    private boolean inItem = false;
    private boolean putLink = false;

    public SAXXMLHandler() {
        items = new ArrayList<HashMap<String, String>>();
    }

    public ArrayList<HashMap<String, String>> getItems() {
        return items;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        buffer = new StringBuffer();
        if (qName.equalsIgnoreCase(ITEM) || qName.equalsIgnoreCase(CUR_ITEM)) {
            item = new HashMap<String, String>();
            inItem = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        if (buffer != null) {
            buffer.append(tempVal);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        tempVal = buffer.toString();
        if (qName.equalsIgnoreCase(ITEM) || qName.equalsIgnoreCase(CUR_ITEM)) {
            items.add(item);
        } else if (qName.equalsIgnoreCase(DATE) || qName.equalsIgnoreCase(CUR_TIME)) {
            item.put(DATE, tempVal);
        } else if (qName.equalsIgnoreCase(CUR_TEMP)) {
            item.put(CUR_TEMP, tempVal);
        } else if (qName.equalsIgnoreCase(DESCR)) {
            item.put(DESCR, tempVal);
        } else if (qName.equalsIgnoreCase(CUR_WIND)) {
            item.put(CUR_WIND, tempVal);
        }  else if (qName.equalsIgnoreCase(CUR_HUM)) {
            item.put(CUR_HUM, tempVal);
        }  else if (qName.equalsIgnoreCase(CUR_PRESS)) {
            item.put(CUR_PRESS, tempVal);
        }  else if (qName.equalsIgnoreCase(MAX_TEMP)) {
            if (Integer.parseInt(tempVal) > 0) item.put(MAX_TEMP, "+" + tempVal + "°");
            else item.put(MAX_TEMP, tempVal + "°");
        }  else if (qName.equalsIgnoreCase(MIN_TEMP)) {
            if (Integer.parseInt(tempVal) > 0) item.put(MIN_TEMP, "+" + tempVal + "°");
            else item.put(MIN_TEMP, tempVal + "°");
        }  else if (qName.equalsIgnoreCase(IMG_ID)) {
            item.put(IMG_ID, tempVal);
        }
    }
}
