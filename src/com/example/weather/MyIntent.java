package com.example.weather;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class MyIntent extends IntentService {
    public MyIntent(String name) {
        super(name);
    }

    public MyIntent() {
        super("d");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.getStringExtra("all").equals("full")) {
            String town_name = intent.getStringExtra("name");
            Refresh refresh = new Refresh(getApplicationContext(), town_name);
            GetIdTown getIdTown = new GetIdTown(town_name);
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase wdb = dbHelper.getWritableDatabase();
            Cursor cursor = wdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
            int town_column = cursor.getColumnIndex(DBHelper.TOWN_NAME);
            int id_column = cursor.getColumnIndex(DBHelper._ID);

            while (cursor.moveToNext()) {
                if (cursor.getString(town_column) != null && cursor.getString(town_column).equals(town_name)) {
                    wdb.delete(DBHelper.DATABASE_NAME, DBHelper._ID + "=" + cursor.getString(id_column), null);
                }
            }
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.TOWN_NAME, town_name);
            wdb.insert(DBHelper.DATABASE_NAME, null, cv);
            for (int i = 0; i < 10000000; i++) ;
            try {
                getIdTown.download("http://weather.yandex.ru/static/cities.xml");
            } catch (
                    ParserConfigurationException e) {

            } catch (SAXException e) {

            } catch (IOException e) {

            }
            String url = "http://export.yandex.ru/weather-ng/forecasts/" + getIdTown.town_id + ".xml";
            refresh.update_channel(url);
        } else {
            ArrayList<String> array = new ArrayList<String>();

            DBHelper dbHelper = new DBHelper(getApplicationContext());
            SQLiteDatabase wdb = dbHelper.getWritableDatabase();
            Cursor cursor1 = wdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
            int name_column = cursor1.getColumnIndex(DBHelper.TOWN_NAME);
            int day_column = cursor1.getColumnIndex(DBHelper.DAY);
            while (cursor1.moveToNext()) {
                if (cursor1.getString(name_column) != null && cursor1.getString(day_column) != null && cursor1.getString(day_column).equals("full")) {
                    array.add(cursor1.getString(name_column));
                }
            }


            for (int i = 0; i < array.size(); i++) {
                String town_name = array.get(i);
                Refresh refresh = new Refresh(getApplicationContext(), town_name);
                GetIdTown getIdTown = new GetIdTown(town_name);

                Cursor cursor = wdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
                int town_column = cursor.getColumnIndex(DBHelper.TOWN_NAME);
                int id_column = cursor.getColumnIndex(DBHelper._ID);

                while (cursor.moveToNext()) {
                    if (cursor.getString(town_column).equals(town_name)) {
                        wdb.delete(DBHelper.DATABASE_NAME, DBHelper._ID + "=" + cursor.getString(id_column), null);
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.TOWN_NAME, town_name);
                wdb.insert(DBHelper.DATABASE_NAME, null, cv);
                try {
                    getIdTown.download("http://weather.yandex.ru/static/cities.xml");
                } catch (
                        ParserConfigurationException e) {

                } catch (SAXException e) {

                } catch (IOException e) {

                }
                String url = "http://export.yandex.ru/weather-ng/forecasts/" + getIdTown.town_id + ".xml";
                refresh.update_channel(url);
            }
        }
        Intent intent1 = new Intent();
                   intent1.setAction("show_town");
                    sendBroadcast(intent1);
    }
}
