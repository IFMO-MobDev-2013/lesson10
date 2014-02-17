package com.ifmo.android.OdincovaAnn.MyWeather;

import android.app.IntentService;
import android.content.Intent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class OneCityUpdateService extends IntentService {

    public static final String finishUpdateKey = "finishUpdateKey.com.ifmo.android.OdincovaAnn.MyWeather.OneCityUpdateService";
    public static final String finishCityKey = "finishCityKey.ifmo.android.OdincovaAnn.MyWeather.OneCityUpdateService";
    private final String key = "key=5p9rzvq7q738557j4v4aq3sg";
    private final String link = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
    private final String settings = "&format=xml&num_of_days=5&cc=yes&extra=localObsTime&";
    String name;
    String city;

    public OneCityUpdateService() {
        super("OneCityUpdateService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        city = new String();
        name = intent.getExtras().getString("nameCity");
        int result = 0;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(link + name.replace("_", "+") + settings + key);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            String inform = EntityUtils.toString(httpResponse.getEntity());
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(inform.getBytes()), new WeatherHandler(city, getApplicationContext(), name));
            result = 1;
        } catch (CityException e) {
            result = 3;
        } catch (SAXException e) {
            result = 2;
        } catch (IOException e) {
            result = 2;
        } catch (ParserConfigurationException e) {
            result = 2;
        } catch (IllegalStateException e) {
            result = 2;
        }
        Intent response = new Intent();
        response.putExtra("result", result);
        response.putExtra("name", name);

        if ("add".equals(intent.getExtras().getString("screen"))) {
            response.setAction(finishCityKey);
        } else {
            response.setAction(finishUpdateKey);
        }
        sendBroadcast(response);
    }
}
