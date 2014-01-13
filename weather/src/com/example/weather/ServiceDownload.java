package com.example.weather;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServiceDownload extends IntentService {

    ArrayList<String> result;
    ArrayList<DetailsDay> days;
    byte[] byteArray;
    String cityId;
    InputStream input;

    public ServiceDownload() {
        super("MyService");
    }

    public ServiceDownload(String link) {
        super(link);
    }

    public void onCreate() {
        super.onCreate();
    }

    private void updateDataBase() {
        DataBase sqh = new DataBase(this);
        SQLiteDatabase sqdb = sqh.getWritableDatabase();
        sqdb.delete(DataBase.TABLE_NAME, DataBase.CITYID + "=?", new String[]{String.valueOf(cityId)});
        ContentValues newValues = new ContentValues();
        newValues.put(DataBase.CITYID, result.get(0));
        newValues.put(DataBase.CITYNAME, result.get(1));
        newValues.put(DataBase.TEMPERATURE, result.get(2));
        newValues.put(DataBase.DATE, result.get(3));
        newValues.put(DataBase.SKY, result.get(4));
        newValues.put(DataBase.SUNRISE, result.get(5));
        newValues.put(DataBase.SUNSET, result.get(6));
        newValues.put(DataBase.SPEED, result.get(7));
        newValues.put(DataBase.DIRECTION, result.get(8));
        newValues.put(DataBase.HUMIDITY, result.get(9));
        newValues.put(DataBase.DAY1, days.get(0).getAll());
        newValues.put(DataBase.DAY2, days.get(1).getAll());
        newValues.put(DataBase.DAY3, days.get(2).getAll());


        newValues.put(DataBase.IMAGE, byteArray);
        sqdb.insert(DataBase.TABLE_NAME, null, newValues);
    }

    protected void onHandleIntent(Intent intent) {
        String one = intent.getStringExtra("one");
        cityId = intent.getStringExtra("cityid");
        String cityName = intent.getStringExtra("city");
        String link = "http://weather.yahooapis.com/forecastrss?w=" + (cityId) + "&u=c";
        Bitmap imageBitmap = null;
        result = new ArrayList<String>(10);
        days = new ArrayList<DetailsDay>();
        if (link != null) {
            try {
                URL url = new URL(link);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    input = connect.getInputStream();
                    DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dB = dBF.newDocumentBuilder();
                    Document document = dB.parse(input);
                    Element element = document.getDocumentElement();

                    NodeList nodelist = element.getElementsByTagName("channel");
                    Element entry = (Element) nodelist.item(0);
                    Element date = (Element) entry.getElementsByTagName("lastBuildDate").item(0);
                    Element wind = (Element) entry.getElementsByTagName("yweather:wind").item(0);
                    Element atmosphere = (Element) entry.getElementsByTagName("yweather:atmosphere").item(0);
                    Element astronomy = (Element) entry.getElementsByTagName("yweather:astronomy").item(0);


                    nodelist = element.getElementsByTagName("item");
                    entry = (Element) nodelist.item(0);
                    Element description = (Element) entry.getElementsByTagName("description").item(0);
                    String description_ = description.getFirstChild().getNodeValue();

                    String tmp = ((description_.replaceAll("<br />", "")).replaceAll("<b>", "")).replaceAll("<BR>", "").replaceAll("</b>", "").replaceAll("<BR />", "");

                    String[] weather = tmp.split("\n");
                    int index = weather[3].indexOf(",");

                    result.add(cityId);
                    result.add(cityName);


                    result.add(weather[3].substring(index + 2, weather[3].length() - 2));      //temperature

                    result.add(date.getFirstChild().getNodeValue());       //date
                    result.add(weather[3].substring(0, index));   //sky
                    result.add(astronomy.getAttribute("sunrise"));
                    result.add(astronomy.getAttribute("sunset"));
                    result.add(wind.getAttribute("speed"));
                    result.add(wind.getAttribute("direction"));
                    result.add(atmosphere.getAttribute("humidity"));

                    try {
                        url = new URL(description_.substring(description_.indexOf("\"") + 1, description_.indexOf("\"", description_.indexOf("\"") + 1)));
                        connect = (HttpURLConnection) url.openConnection();
                        if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            input = connect.getInputStream();
                            imageBitmap = BitmapFactory.decodeStream(input);
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Element forecast = (Element) entry.getElementsByTagName("yweather:forecast").item(0);
                    entry.removeChild(forecast);
                    for (int i = 0; i < 3; i++) {
                        forecast = (Element) entry.getElementsByTagName("yweather:forecast").item(0);
                        entry.removeChild(forecast);
                        DetailsDay temp = new DetailsDay();
                        temp.setDay(forecast.getAttribute("day"));
                        temp.setDate(forecast.getAttribute("date"));
                        temp.setLow(forecast.getAttribute("low"));
                        temp.setHigh(forecast.getAttribute("high"));
                        temp.setSky(forecast.getAttribute("text"));
                        days.add(temp);
                    }
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            Intent intentResponse = new Intent("1");
            intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponse.putStringArrayListExtra("result", result);
            intentResponse.putParcelableArrayListExtra("days", days);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            intentResponse.putExtra("image", byteArray);

            if ("1".equals(one)) {
                sendBroadcast(intentResponse);
            } else {
                updateDataBase();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
