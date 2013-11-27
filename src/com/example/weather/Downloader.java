package com.example.weather;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Downloader {

    String encode = "utf-8";
    public boolean successfulDownload = false;
    String defaultEncoding = "ISO-8859-1";

    public static String weatherRequest(String woeid) {
        return "http://weather.yahooapis.com/forecastrss?w=" + woeid + "&u=c";
    }

    private boolean downloadPage(String urlString, MySAXParser saxParser) throws XmlPullParserException, IOException, ParserConfigurationException, SAXException {

        boolean res = false;
        InputStream stream = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(saxParser);

        URL url;
        HttpURLConnection conn;
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(7000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            stream = conn.getInputStream();
            if (stream == null) {
                res = false;
                System.out.println("error stream");
            } else {
                System.out.println("going to parse_" + urlString);
                Scanner scanner = new Scanner(stream, defaultEncoding);
                String tempString = scanner.nextLine();
                encode = defaultEncoding;
                if (tempString.contains("encoding=")) {
                    encode = "";
                    int i = tempString.indexOf("encoding=") + "encoding=".length() + 1;
                    while (tempString.charAt(i) != '"') {
                        encode += tempString.charAt(i);
                        i++;
                    }
                }
                scanner.close();
                stream.close();

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(7000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                stream = conn.getInputStream();

                if (stream == null) {
                    res = false;
                    System.out.println("error stream");
                } else {
                    InputSource inputSource = new InputSource(stream);

                    System.out.println("encode " + encode);
                    inputSource.setEncoding(defaultEncoding);
                    xmlReader.parse(inputSource);
                    System.out.println("count " + saxParser.array.size());
                    res = true;
                }
            }
        } catch (Exception e) {
            res = false;
            System.out.println("count_bad_" + saxParser.array.size());
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }

        }
        return res;
    }

    public Downloader() {
        successfulDownload = false;
    }

    public Downloader(String url, MySAXParser saxParser) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {

        try {
            successfulDownload = downloadPage(url, saxParser);
        } catch (Exception e) {
            successfulDownload = false;
        }
    }
}
