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


public class WeatherService extends IntentService {
    private final String URL = "url";
    private final String ERROR = "Error";
    private final String RESPONSE = "weatherResponse";
    private final String WEATHER = "weather";
    private final String MAXC = "maxc";
    private final String MINC = "minc";
    private final String WINDSPEED = "windspeed";
    private final String HUMIDITY = "humidity";
    private final String CODE = "code";
    private final String DATE = "date";

    String[] minC, maxC, weather, windspeed, humidity, date,code;

    String url = null;
    public WeatherService() {
        super("sax");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        url = intent.getStringExtra(URL);
        String error = ERROR;
        try {
            code = new String[10]; minC = new String[10]; maxC = new String[10]; weather = new String[10]; windspeed = new String[10]; humidity = new String[10]; date = new String[10];
            SAXParserFactory factory = SAXParserFactory.newInstance();
            javax.xml.parsers.SAXParser parser = factory.newSAXParser();
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity httpEntity = httpResponse.getEntity();
            String xml = EntityUtils.toString(httpEntity, "UTF-8");
            InputSource input = new InputSource(new StringReader(xml));
            parser.parse(input, new WeatherHandler(minC, maxC, weather, windspeed, humidity, date,code));
        } catch (Exception e) {
            error = e.getMessage();
        }
        Intent response = new Intent();
        response.setAction(RESPONSE);
        response.addCategory(Intent.CATEGORY_DEFAULT);
        response.putExtra(WEATHER, weather);
        response.putExtra(MINC, minC);
        response.putExtra(MAXC, maxC);
        response.putExtra(WINDSPEED, windspeed);
        response.putExtra(HUMIDITY, humidity);
        response.putExtra(DATE, date);
        response.putExtra(CODE, code);
        sendBroadcast(response);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, System.currentTimeMillis() + 100000, 100000, pi);
    }
}

