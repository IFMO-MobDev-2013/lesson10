package com.example.Nice_Weather_App;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainIntentWorkForWeather extends IntentService {
    public static final String ACTION_MyIntentService = "com.example.Nice_Weather_App.RESPONSE";
    public static String city = "";
    public static String country = "";
    public static String yandex_id = "";

    public MainIntentWorkForWeather() throws Exception {
        super("Pavel");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String URLAdress = intent.getStringExtra(MyActivity.KEY_FOR_WEATHER_INTENT);
        city = intent.getStringExtra(MyActivity.KEY_CITY);
        country = intent.getStringExtra(MyActivity.KEY_COUNTRY);
        yandex_id = intent.getStringExtra(MyActivity.KEY_YANDEX_ID);

        try {
            URL url = new URL(URLAdress);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(15000);
            InputStream inputStream = httpConnection.getInputStream();
            byte[] b = new byte[50];
            inputStream.read(b);
            String encoding = "";
            for (int i = 0; i < 50; i++) encoding = encoding + (char)b[i];

            // utf-8 or windows-1251 or ...
            encoding = encoding.substring(encoding.indexOf("encoding=\"") + 10 , encoding.indexOf("\"?>"));
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);

            // bonding inputStreams
            inputStream = new SequenceInputStream(byteArrayInputStream, inputStream);

            // next work
            Reader reader = new InputStreamReader(inputStream, encoding);
            InputSource is = new InputSource(reader);
            is.setEncoding(encoding);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            SAXParser parser = factory.newSAXParser();
            SAXXMLParserForWeather saxXMLParser = new SAXXMLParserForWeather();
            parser.parse(is, saxXMLParser);
        } catch(Exception e) {
            e.printStackTrace();
            Log.d("Handle Intent", "Warning");
        }
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(intentResponse);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
