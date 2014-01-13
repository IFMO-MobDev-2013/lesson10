package com.example.weather;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Charm on 08.01.14.
 */
public class AlarmService extends IntentService {

    private ArrayList<DetailsDay> days;
    private ArrayList<String> result;
    private byte[] buffer;
    BroadcastReceiver br;

    public AlarmService() {
        super("MyService");
    }

    protected void onHandleIntent(Intent intent) {
        DataBase sqh;
        SQLiteDatabase sqdb;
        sqh = new DataBase(this);
        sqdb = sqh.getWritableDatabase();



        Cursor cursor = sqdb.query(DataBase.TABLE_NAME, new String[]{
                DataBase._ID, DataBase.CITYID, DataBase.CITYNAME, DataBase.TEMPERATURE, DataBase.DATE,
                DataBase.SKY, DataBase.SUNRISE, DataBase.SUNSET, DataBase.SPEED, DataBase.DIRECTION, DataBase.HUMIDITY,
                DataBase.DAY1, DataBase.DAY2, DataBase.DAY3, DataBase.IMAGE},
                null, null,
                null, null, null);




        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(DataBase.CITYID));
                String cityName_ = cursor.getString(cursor.getColumnIndex(DataBase.CITYNAME));
                Intent go = new Intent(this, ServiceDownload.class).putExtra("cityid", String.valueOf(id)).putExtra("city", cityName_)
                        .putExtra("one","0");
                startService(go);


            } while (cursor.moveToNext());
        }
    }

}
