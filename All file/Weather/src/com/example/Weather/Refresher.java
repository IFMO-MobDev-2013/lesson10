package com.example.Weather;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Refresher extends IntentService {
	WeatherSQLiteOpenHelper openHelper;
	SQLiteDatabase database;
	Cursor cursor;
    public Refresher() {
        super("Refresher");
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		openHelper.close();
		database.close();
		cursor.close();
	}

	public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
		openHelper = new WeatherSQLiteOpenHelper(this);
		database = openHelper.getWritableDatabase();
		assert database != null;
		cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME, null,
				null,
				null,
				null,
				null,
				null
		);
		String city,latitude,longitude;
		while (cursor.moveToNext()) {
			Intent intentRSS = new Intent(this, WeatherIntentService.class);
			city = cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY));
			latitude = cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.LATITUDE));
			longitude = cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.LONGITUDE));
			startService(intentRSS.putExtra("city", city).putExtra("latitude", latitude).putExtra("longitude", longitude));
		}
		openHelper.close();
		database.close();
		cursor.close();
    }
}
