package com.example.weather;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class TownUpdater {

    String woeid;
    Context context;

    public TownUpdater(String _woeid, Context _context) {
        woeid = _woeid;
        context = _context;
    }

    public boolean update() throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        if (woeid == null || "".equals(woeid))
            return true;

        SAXParserWeather saxParserWeather = new SAXParserWeather();
        Downloader downloader = new Downloader(Downloader.weatherRequest(woeid), saxParserWeather);

        boolean res = false;

        MyDataBaseTownHelper myDataBaseTownHelper = new MyDataBaseTownHelper(context, woeid);
        SQLiteDatabase sqLiteDatabase = myDataBaseTownHelper.getWritableDatabase();

        try {
            sqLiteDatabase.execSQL(MyDataBaseTownHelper.DROP_DATABASE_NO_ID + woeid);
            sqLiteDatabase.execSQL(MyDataBaseTownHelper.CREATE_DATABASE_BEFORE_ID + woeid + MyDataBaseTownHelper.CREATE_DATABASE_AFTER_ID);

            for (int i = 0; i < saxParserWeather.array.size(); i++) {
                ContentValues contentValues = new ContentValues();

                //contentValues.put(MyDataBaseTownHelper._ID, i + 1);                       //   from one!!!!
                Day item = (Day) saxParserWeather.array.get(i);
                contentValues.put(MyDataBaseTownHelper.DATE, item.date);
                contentValues.put(MyDataBaseTownHelper.HUMIDITY, item.humidity);
                contentValues.put(MyDataBaseTownHelper.PRESSURE, item.pressure);
                contentValues.put(MyDataBaseTownHelper.WIND_DIRECTION, item.wind_direction);
                contentValues.put(MyDataBaseTownHelper.WIND_SPEED, item.wind_speed);
                contentValues.put(MyDataBaseTownHelper.LOW_TEMPERATURE, item.low_temperature);
                contentValues.put(MyDataBaseTownHelper.HIGH_TEMPERATURE, item.high_temperature);
                contentValues.put(MyDataBaseTownHelper.CLOUDS, item.clouds);
                contentValues.put(MyDataBaseTownHelper.IMAGE, item.image);

                sqLiteDatabase.insert(MyDataBaseTownHelper.DATABASE_NAME_NO_ID + woeid, null, contentValues);
            }
            res = true;
        } catch (Exception e) {
            res = false;
        } finally {
            sqLiteDatabase.close();
            myDataBaseTownHelper.close();
        }
        return res;
    }

}