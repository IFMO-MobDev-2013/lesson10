package com.mikhov.Weather;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import com.mikhov.Weather.Async.ExampleHandler;
import com.mikhov.Weather.Async.ParsedExampleDataSet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Updater extends IntentService {

    public static final String UPDATE_TAG = "updated";
    String[] towns;
    String num, town;

    public Updater() {
        super("Updater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Database database = new Database(this);
        database.open();
        if (true) {
            try {
                if (database.isNotEmpty()) {
                    Cursor codes = database.getAllData();
                    String str_codes = "";
                    while (codes.moveToNext()) {
                        str_codes += codes.getString(codes.getColumnIndex(Database.COL_CODE)) + "#";
                    }
                    str_codes = str_codes.substring(0, str_codes.length() - 1);
                    towns = str_codes.split("#");
                    String str_url = "http://export.yandex.ru/weather-ng/forecasts/";

                    try {
                        for (int i = 0; i < towns.length; i++) {
                            num = towns[i];
                            URL url = new URL(str_url + towns[i] + ".xml");
                            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                            SAXParser saxParser = saxParserFactory.newSAXParser();

                            XMLReader xmlReader = saxParser.getXMLReader();

                            ExampleHandler exampleHandler = new ExampleHandler();
                            xmlReader.setContentHandler(exampleHandler);

                            xmlReader.parse(new InputSource(url.openStream()));

                            ParsedExampleDataSet parsedExampleDataSet = exampleHandler.getParsedData();
                            String[] d = parsedExampleDataSet.toString().split("#");

                            town = database.getTownById(num);

                            Bitmap[] bitmap = new Bitmap[3];
                            for (int j = 0; j < 3; j++) {
                                bitmap[j] = null;
                                HttpURLConnection conn = null;
                                BufferedInputStream buf_stream = null;
                                try {
                                    int k = 0;
                                    if (j == 0) k = 1;
                                    if (j == 1) k = 9;
                                    if (j == 2) k = 15;
                                    conn = (HttpURLConnection) new URL("http://yandex.st/weather/1.2.1/i/icons/48x48/" + d[k] + ".png").openConnection();
                                    conn.setDoInput(true);
                                    conn.setRequestProperty("Connection", "Keep-Alive");
                                    conn.connect();
                                    buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
                                    bitmap[j] = BitmapFactory.decodeStream(buf_stream);
                                    buf_stream.close();
                                    conn.disconnect();
                                } catch (MalformedURLException ex) {
                                    d[0] = "MUException";
                                } catch (IOException ex) {
                                    d[0] = "IOException";
                                }
                            }
                            database.updateTown(town, d[0] + "˚C", d[2], d[3], d[4], d[5], R.drawable.ic_launcher, bitmap[0],
                                    d[8] + "˚C", d[10], d[11], d[6], d[7], R.drawable.ic_launcher, bitmap[1],
                                    d[14] + "˚C", d[16], d[17], d[12], d[13], R.drawable.ic_launcher, bitmap[2]);
                        }
                    } catch (Exception e) {
                    }
                }
            } catch(Exception e) {
            }
        }
        update(intent);
        intent = new Intent(UPDATE_TAG);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void update(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000 * 3600, pendingIntent);
    }
}
