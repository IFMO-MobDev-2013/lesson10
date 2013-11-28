package com.example.MaWeather;


import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

public class WeatherGetter extends IntentService {

    private ArrayList<WeatherItem> nodes;

    public WeatherGetter() {
        super("MyName");

        nodes = new ArrayList<WeatherItem>();
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int cityCode = new Integer(intent.getStringExtra("cityId")).intValue();
        String cityName = intent.getStringExtra("cityName");
        String task = intent.getStringExtra("task");

        nodes.clear();

        String request = "http://export.yandex.ru/weather-ng/forecasts/" + cityCode + ".xml";

        InputStream inputStream = null;

        Log.d("ohhhhh", request);

        URL url;
        HttpURLConnection connect;

        try {
            url = new URL(request);
            connect = (HttpURLConnection) url.openConnection();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            inputStream = connect.getInputStream();

            Document document = documentBuilder.parse(inputStream);

            Element element = document.getDocumentElement();

            NodeList nodeList = element.getElementsByTagName("fact");

            //Log.d("myTAG", new Integer(nodeList.getLength()).toString());

            Element main = (Element) nodeList.item(0);

            Element child = (Element) main.getElementsByTagName("weather_type").item(0);
            String  curWeatherType = child.getFirstChild().getNodeValue();

            String curTemperature;
            child = (Element) main.getElementsByTagName("temperature").item(0);
            if (child == null) {
                child = (Element) main.getElementsByTagName("temperature_from").item(0);
                curTemperature = child.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child.getFirstChild().getNodeValue();
            }

            child = (Element) main.getElementsByTagName("humidity").item(0);
            String  curHumidity = child.getFirstChild().getNodeValue();

            child = (Element) main.getElementsByTagName("pressure").item(0);
            String  curPressure = child.getFirstChild().getNodeValue();

            child = (Element) main.getElementsByTagName("image-v3").item(0);
            String  curPictureType = child.getFirstChild().getNodeValue();

            child = (Element) main.getElementsByTagName("image").item(0);
            String  curBigPictureType = child.getFirstChild().getNodeValue();

            if (curBigPictureType.charAt(0) == 'n') {
                curBigPictureType = curBigPictureType.substring(1);
            }

            nodeList = element.getElementsByTagName("day");

            main = (Element) nodeList.item(0);

            child = (Element) main.getElementsByTagName("sunrise").item(0);
            String  curSunrise = child.getFirstChild().getNodeValue();

            child = (Element) main.getElementsByTagName("sunset").item(0);
            String  curSunset = child.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(curWeatherType, curSunrise, curSunset, curTemperature, curHumidity, curPressure, curPictureType, curBigPictureType));

            ////////////////////////

            NodeList nodeList1 = main.getElementsByTagName("day_part");

            Element main1 = (Element) nodeList1.item(0);

            Element child1;
            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("weather_type").item(0);
            curWeatherType = child1.getFirstChild().getNodeValue();

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(curWeatherType, null, null, curTemperature, null, null, curPictureType, null));

            ////////////////////////

            main1 = (Element) nodeList1.item(1);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("weather_type").item(0);
            curWeatherType = child1.getFirstChild().getNodeValue();

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(curWeatherType, null, null, curTemperature, null, null, curPictureType, null));

            ////////////////////////

            main1 = (Element) nodeList1.item(2);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("weather_type").item(0);
            curWeatherType = child1.getFirstChild().getNodeValue();

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(curWeatherType, null, null, curTemperature, null, null, curPictureType, null));

            ////////////////////////

            main = (Element) nodeList.item(1);

            nodeList1 = main.getElementsByTagName("day_part");

            main1 = (Element) nodeList1.item(4);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            main1 = (Element) nodeList1.item(5);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            curHumidity = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(null, null, null, curTemperature, curHumidity, null, curPictureType, null));

            ////////////////////////

            main = (Element) nodeList.item(2);

            nodeList1 = main.getElementsByTagName("day_part");

            main1 = (Element) nodeList1.item(4);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            main1 = (Element) nodeList1.item(5);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            curHumidity = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(null, null, null, curTemperature, curHumidity, null, curPictureType, null));

            ////////////////////////

            main = (Element) nodeList.item(3);

            nodeList1 = main.getElementsByTagName("day_part");

            main1 = (Element) nodeList1.item(4);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            if (child1 == null) {
                child1 = (Element) main1.getElementsByTagName("temperature_from").item(0);
                curTemperature = child1.getFirstChild().getNodeValue();
            }
            else {
                curTemperature = child1.getFirstChild().getNodeValue();
            }

            child1 = (Element) main1.getElementsByTagName("image-v3").item(0);
            curPictureType = child1.getFirstChild().getNodeValue();

            main1 = (Element) nodeList1.item(5);

            child1 = (Element) main1.getElementsByTagName("temperature").item(0);
            curHumidity = child1.getFirstChild().getNodeValue();

            nodes.add(new WeatherItem(null, null, null, curTemperature, curHumidity, null, curPictureType, null));

            ////////////////////////


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {}
        }

        WeatherDataBase weatherDataBase = new WeatherDataBase(this);
        weatherDataBase.addCity(nodes, cityName);

        //Log.d("myTag", new Integer(nodes.size()).toString());
        Intent intentResponse = new Intent("com.example.MaWeather.RESPONSE_FORECAST");
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        if (!"refresh".equals(task)) {
            sendBroadcast(intentResponse);
        }
    }
}
