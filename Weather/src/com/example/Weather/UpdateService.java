package com.example.Weather;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UpdateService extends IntentService {
    public static final String RESULT = "result";
    public static final String ACTION = "update";

    ArrayList<City> arrayList;

    public UpdateService() {
        super("");
    }

    public UpdateService(String name) {
        super(name);
    }

    private void initArray() {
        CityDataBaseHelper cityDataBaseHelper = new CityDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = cityDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(CityDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int nameColumn = cursor.getColumnIndex(CityDataBaseHelper.NAME);
        int idColumn = cursor.getColumnIndex(CityDataBaseHelper._ID);
        arrayList = new ArrayList<City>();
        while (cursor.moveToNext()) {
            arrayList.add(new City(cursor.getString(nameColumn), cursor.getInt(idColumn)));
        }
        cursor.close();
        sqLiteDatabase.close();
        cityDataBaseHelper.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initArray();
        boolean flag = intent.getBooleanExtra(MyActivity.FLAG, true);
        int id = intent.getIntExtra(MyActivity.ID, 0);
        boolean done = true;
        if (flag) {
            for (int i = 0; i < arrayList.size(); i++) {
                DownloadWeather downloadWeather = new DownloadWeather(arrayList.get(i), getApplicationContext());
                if (!downloadWeather.execute()) {
                    done = false;
                }
            }
        } else {
            DownloadWeather downloadWeather = new DownloadWeather(arrayList.get(id), getApplicationContext());
            if (!downloadWeather.execute()) {
                done = false;
            }
        }
        Intent intentResp = new Intent();
        intentResp.putExtra(RESULT, done);
        intentResp.setAction(ACTION);
        sendBroadcast(intentResp);

    }


}
