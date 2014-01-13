package com.example.GoodWeather;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import java.io.StringReader;


public class CityService extends IntentService {
    private final String URL = "url";
    private final String CITY = "city";
    private final String RESPONSE = "cityResponse";
    private final String ERROR = "error";
    private final String LONGTITUDE = "longtitude";
    private final String LATITUDE = "latitude";
    private final String COUNTRY = "country";

    String[] longtitudes, latitudes, countries;

    public CityService() {
        super("city");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(URL);
        String error = ERROR;
        try {
            longtitudes = new String[10]; latitudes = new String[10]; countries = new String[10];
            SAXParserFactory factory = SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser parser = factory.newSAXParser();
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity httpEntity = httpResponse.getEntity();
            String xml = EntityUtils.toString(httpEntity, "UTF-8");
            InputSource input = new InputSource(new StringReader(xml));
            parser.parse(input, new CityHandler(longtitudes, latitudes, countries));
        } catch (Exception e) {
            error = e.getMessage();
        }
        Intent response = new Intent();
        response.setAction(RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra(LONGTITUDE, longtitudes);
        response.putExtra(LATITUDE, latitudes);
        response.putExtra(COUNTRY, countries);
        sendBroadcast(response);
    }
}

