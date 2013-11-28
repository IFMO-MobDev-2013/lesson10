package com.example.MaWeather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Александр
 * Date: 28.11.13
 * Time: 20:08
 */
public class CityGetter extends IntentService {

    private ArrayList<CityItem> items;

    public CityGetter() {
        super("MyName");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        URL url;
        HttpURLConnection connect;

        Log.d("TestTest", "start");

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://weather.yandex.ru/static/cities.xml");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            Reader reader;
            if (EntityUtils.getContentCharSet(entity) != null) {
                reader = new InputStreamReader(entity.getContent(), EntityUtils.getContentCharSet(entity));
            }
            else {
                reader = new InputStreamReader(entity.getContent(), "UTF-8");
            }
            InputSource is;
            is = new InputSource(reader);

            SAXParserFactory factory = SAXParserFactory.newInstance();

            factory.setNamespaceAware(false);
            SAXParser parser;

            parser = factory.newSAXParser();

            CityParser cityParser = new CityParser();

            parser.parse(is, cityParser);

            items = cityParser.getResult();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        WeatherDataBase weatherDataBase = new WeatherDataBase(this);
        weatherDataBase.addCites(items);

        Intent intentResponse = new Intent("com.example.MaWeather.RESPONSE_CITY");
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(intentResponse);

    }

    private class CityParser extends DefaultHandler {

        private ArrayList<CityItem> result;
        private boolean isCity;
        private CityItem cityNode;
        private String ans;
        private String cityId;
        private String cityName;

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            result = new ArrayList<CityItem>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            ans = "";
            //Log.d("TestTest", new Integer(attributes.getLength()).toString());

            if (qName.equalsIgnoreCase("city")) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getQName(i);
                    if (name.equalsIgnoreCase("id")) {
                        cityId = attributes.getValue(name);
                    }
                }
                isCity = true;
            }

            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] c, int start, int length) throws SAXException {
            super.characters(c, start,  length);
            String s = new String(c, start, length);

            if (isCity) {
                ans += s;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("city")) {
                isCity = false;
                result.add(new CityItem(ans, cityId));
            }
            super.endElement(uri,localName, qName);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        public ArrayList<CityItem> getResult() {
            return this.result;
        }
    }
}
