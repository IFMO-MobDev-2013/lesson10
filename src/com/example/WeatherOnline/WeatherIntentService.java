package com.example.WeatherOnline;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 03.02.14
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class WeatherIntentService extends IntentService {
    public static final String start = "updatingStarted";
    public static final String finish = "updatingComplete";
    private final String key = "key=bsj4q9rwzw23jav9ajtrfq8s";
    private final String queryLink = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
    private final String querySettings = "&format=xml&num_of_days=3&cc=yes&";
    String icon1 = "";
    String[] icons = {"", "" , ""};
    int sup = -1;


    public WeatherIntentService(){
        super("WEATHER");
    }

    @Override
    public void onHandleIntent(Intent intent){
        Intent startUpdate = new Intent();
        startUpdate.setAction(start);
        sendBroadcast(startUpdate);
        WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());
        ArrayList<String> cities = weatherDataBase.getCityList();
        for(int i = 0; i < cities.size(); i++){
            String s = "";
            for(int j = 0; j < cities.get(i).length(); j++){
                if(cities.get(i).toLowerCase().charAt(j) == cities.get(i).charAt(j))
                    s += cities.get(i).charAt(j);
                else
                    s += "+" + cities.get(i).charAt(j);
            }
            String query = queryLink + s + querySettings + key;
            try{
                String weatherDescription = EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(query)).getEntity());
                try{
                    sup = -1;
                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    SAXParser saxParser = saxParserFactory.newSAXParser();
                    saxParser.parse(new ByteArrayInputStream(weatherDescription.getBytes()), new MyHandler());
                } catch (IOException e){

                } catch (SAXException e){

                } catch (ParserConfigurationException e){

                }
                weatherDataBase.addCity(cities.get(i), weatherDescription,
                        BitmapFactory.decodeStream(new URL(icon1).openStream()),
                        BitmapFactory.decodeStream(new URL(icons[0]).openStream()),
                        BitmapFactory.decodeStream(new URL(icons[1]).openStream()),
                        BitmapFactory.decodeStream(new URL(icons[2]).openStream()));
            } catch (IOException e){
                weatherDataBase.addCity(cities.get(i), null, null, null, null, null);
            }
        }
        Intent finishUpdate = new Intent();
        finishUpdate.setAction(finish);
        sendBroadcast(finishUpdate);
    }



    private class MyHandler extends DefaultHandler {
        final String ICON = "weatherIconUrl";
        final String NOW = "current_condition";
        final String WEATHER = "weather";
        String buffer = "";

        boolean current = false, weather = false;


        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs)throws SAXException {
            buffer = "";
            if(NOW.equals(localName)){
                current = true;
            }

            if(WEATHER.equals(localName)){
                weather = true;
            }
        }

        public void endElement(String uri, String localName, String qName)throws SAXException{
            if(NOW.equals(localName)){
                current = false;
            }
            if(ICON.equals(localName)){
                if(current)
                    icon1 = buffer;
                if(weather)
                    icons[++sup] = buffer;
            }
            buffer = "";
        }

        @Override
        public void characters(char[] ch, int start, int length){
            if(current || weather)
                buffer += new String(ch, start, length);
        }

    }
}
