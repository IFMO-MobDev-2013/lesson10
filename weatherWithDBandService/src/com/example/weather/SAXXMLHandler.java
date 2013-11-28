package com.example.weather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
* Created with IntelliJ IDEA.
* User: slavian
* Date: 24.10.13
* Time: 17:09
* To change this template use File | Settings | File Templates.
*/
public class SAXXMLHandler extends DefaultHandler implements ParsingConstants{

    private ArrayList<HashMap<String, String> > weather;
    private String tempVal;
    private HashMap<String, String> tempItem;
    private boolean cc = false;

    private StringBuffer buffer;

    public SAXXMLHandler() {
        weather = new ArrayList<HashMap<String, String> >();
    }

    public ArrayList<HashMap<String, String> > get() {
        return weather;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        buffer = new StringBuffer();
        tempVal = "";
        if (qName.equalsIgnoreCase(KEY_WEATHER)) {

            tempItem = new HashMap<String, String>();
            cc  = false;
        }
        else if(qName.equalsIgnoreCase(KEY_CC))
        {
            tempItem = new HashMap<String, String>();
            cc  = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        String readChars = new String(ch,start,length);
        if(buffer != null) buffer.append(readChars);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        tempVal  = buffer.toString();

        if (qName.equalsIgnoreCase(KEY_CC) || qName.equalsIgnoreCase(KEY_WEATHER)) {
            if(!cc)
                tempItem.put(KEY_TYPE, KEY_WEATHER);
            else
                tempItem.put(KEY_TYPE, KEY_CC);
            weather.add(tempItem);
            cc = false;

        } else if (qName.equalsIgnoreCase(KEY_CC) ){
            cc = true;
        } else if (qName.equalsIgnoreCase(KEY_OBSTIME)) {
            tempItem.put(KEY_OBSTIME, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPC)) {
            tempItem.put(KEY_TEMPC, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPF)) {
            tempItem.put(KEY_TEMPF, tempVal);

        } else if (qName.equalsIgnoreCase(KEY_DATE)) {
            tempItem.put(KEY_DATE, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPMAXC)) {
            tempItem.put(KEY_TEMPMAXC, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPMAXF)) {
            tempItem.put(KEY_TEMPMAXF, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPMINC)) {
            tempItem.put(KEY_TEMPMINC, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_TEMPMINF)) {
            tempItem.put(KEY_TEMPMINF, tempVal);


        } else if (qName.equalsIgnoreCase(KEY_WSM)) {
            tempItem.put(KEY_WSM, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WSK)) {
            tempItem.put(KEY_WSK, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WDD)) {
            tempItem.put(KEY_WDD, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WD16)) {
            tempItem.put(KEY_WD16, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WTHCODE)) {
            tempItem.put(KEY_WTHCODE, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WTHDESC)) {
            tempItem.put(KEY_WTHDESC, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_WTHICON)) {
            tempItem.put(KEY_WTHICON, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_PRCMM)) {
            tempItem.put(KEY_PRCMM, tempVal);

        } else if (qName.equalsIgnoreCase(KEY_HUMID)) {
            tempItem.put(KEY_HUMID, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_VISIBL)) {
            tempItem.put(KEY_VISIBL, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_PRESS)) {
            tempItem.put(KEY_PRESS, tempVal);
        } else if (qName.equalsIgnoreCase(KEY_CLOUD)) {
            tempItem.put(KEY_CLOUD, tempVal);

        }
    }
}
