package com.example.weather;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 24.10.13
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class SAXXMLParser {
    public static ArrayList<HashMap<String, String> > parse(String xml) {
        ArrayList< HashMap<String, String> > weather = null;

        try {

            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

            SAXXMLHandler saxHandler = new SAXXMLHandler();

            xmlReader.setContentHandler(saxHandler);

            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(xml));
            xmlReader.parse(inputSource);
            weather = saxHandler.get();


        } catch (Exception ex) {
            //Log.w("XML", "SAXXMLParser: parse() failed");   // TODO: unhandled exception
        }

        return weather;
    }
}
