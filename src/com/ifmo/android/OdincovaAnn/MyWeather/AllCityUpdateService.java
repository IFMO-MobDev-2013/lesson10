package com.ifmo.android.OdincovaAnn.MyWeather;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.ArrayList;

public class AllCityUpdateService extends IntentService {
    public static final String finishKey = "finish.com.ifmo.android.OdincovaAnn.MyWeather.AllCityUpdateService";
    public static final String startKey = "start.com.ifmo.android.OdincovaAnn.MyWeather.AllCityUpdateService";
    private final String key = "key=5p9rzvq7q738557j4v4aq3sg";
    private final String link = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
    private final String settings = "&format=xml&num_of_days=5&cc=yes&extra=localObsTime&";
    String name;
    String city;
    WeatherDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;
    int result;

    public AllCityUpdateService() {
        super("AllCityUpdateService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Intent newIntent = new Intent();
        newIntent.setAction(startKey);
        sendBroadcast(newIntent);

        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

        final ArrayList<String> cities = new ArrayList<String>();
        while (cursor.moveToNext()) {
            cities.add(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY)));
        }
        cursor.close();
        if (cities.size() != 0) {
            result = 0;
            for (int i = 0; i < cities.size(); i++) {
                name = cities.get(i);
                city = new String();
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
            }
        } else {
            result = 4;
        }
        database.close();
        dataBaseHelper.close();

        Intent response = new Intent();
        response.putExtra("result", result);
        response.setAction(finishKey);
        sendBroadcast(response);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 600000, 600000, pendingIntent);
    }
}
