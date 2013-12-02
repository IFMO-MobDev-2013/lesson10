package com.example.weather;

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

public class GetIdTown {
    String town_id = "";
    String name = "";

    public GetIdTown(String s) {
        name = s;
    }

    class MySAXParser extends DefaultHandler {
        String id = "";
        String element;

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            element = localName;
            if (element.equals("city")) id = atts.getValue(atts.getIndex("id"));
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String s = new String(ch, start, length);
            if (s.equals(name)) {
                town_id = id;
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
            Log.d("URL", url_S);
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
}
