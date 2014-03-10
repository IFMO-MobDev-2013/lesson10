package com.example.Weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final int VERSION = 1;
	public static final String DATABASE_NAME = "database.db";
	public static final String TABLE_NAME = "name";
	public static String CITY = "name_city";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String[] temperatures = new String[]{"t0","t1","t2","t3"};
	public static final String[] icon  = new String[]{"i0","i1","i2","i3"};
	public static final String precipitation = "precipitation";
	public static final String humidity = "humidity";



	public WeatherSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CITY + " TEXT, " + LATITUDE + " TEXT, " + LONGITUDE + " TEXT, " +
				temperatures[0] + " TEXT, " +
				temperatures[1] + " TEXT, " +
				temperatures[2] + " TEXT, " +
				temperatures[3] + " TEXT, " +
				icon[0] + " TEXT, " + icon[1] + " TEXT, " +icon[2] + " TEXT, " +icon[3] + " TEXT, " +
				precipitation + " TEXT, " +  humidity + " TEXT " +
				");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
