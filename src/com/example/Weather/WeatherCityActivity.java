package com.example.Weather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class WeatherCityActivity extends Activity {
	static int id;
	static TextView Temperature,Temperature_1,Temperature_2,Temperature_3,
			City,PrecipProbability,Humidity,Date_1,Date_2,Date_3,Pressure;
	static ImageView Icon,Icon_1,Icon_2,Icon_3;
	static BroadcastReceiver receiver;
	ViewSwitcher viewSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page);
		viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
		Icon = (ImageView) findViewById(R.id.imageView);
		City =  (TextView) findViewById(R.id.textView);
		Temperature =  (TextView) findViewById(R.id.textView1);
		 Pressure  =  (TextView) findViewById(R.id.textView2) ;
		 PrecipProbability = (TextView) findViewById(R.id.textView3) ;
		 Humidity  = (TextView) findViewById(R.id.textView4) ;
		 Temperature_1 =  (TextView) findViewById(R.id.textView8);
		 Temperature_2 =  (TextView) findViewById(R.id.textView9);
		 Temperature_3 =  (TextView) findViewById(R.id.textView10);
		 Date_1 =  (TextView) findViewById(R.id.textView5);
		 Date_2 =  (TextView) findViewById(R.id.textView6);
		 Date_3 =  (TextView) findViewById(R.id.textView7);
		 Icon_1 = (ImageView) findViewById(R.id.imageView1);
		 Icon_2 = (ImageView) findViewById(R.id.imageView2);
		 Icon_3 = (ImageView) findViewById(R.id.imageView3);
		id = getIntent().getIntExtra("id",-1);
		WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(this);
		SQLiteDatabase database = openHelper.getWritableDatabase();
		Cursor cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
		cursor.moveToPosition(id);
		String s = cursor.getString(
				cursor.getColumnIndex(WeatherSQLiteOpenHelper.temperatures[0]));

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateWeather(WeatherCityActivity.this);
				viewSwitcher.showNext();
			}
		};
		IntentFilter intentFilter = new IntentFilter("com.example.Weather.RESPONSE");
		registerReceiver(receiver, intentFilter);

		if (s == null){
			Intent intentRSS = new Intent(WeatherCityActivity.this, WeatherIntentService.class);
			startService(intentRSS.putExtra("city", cursor.getString(
					cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY))).putExtra("latitude", cursor.getString(
					cursor.getColumnIndex(WeatherSQLiteOpenHelper.LATITUDE))).putExtra("longitude", cursor.getString(
					cursor.getColumnIndex(WeatherSQLiteOpenHelper.LONGITUDE))));
		} else {
			updateWeather(this);
			viewSwitcher.showNext();
		}

		cursor.close();
		openHelper.close();
		database.close();
	}

	public static void updateWeather(Context context) {
		WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(context);
		SQLiteDatabase database = openHelper.getWritableDatabase();
		Cursor cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
		cursor.moveToPosition(id);
		City.setText(cursor.getString(
				cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY)));
		Icon.setImageResource(context.getResources().
				getIdentifier("drawable/" + cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.icon[0])), null, context.getPackageName()));
		PrecipProbability.setText("Вероятность осадков: " +
				cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.precipitation)) + " %");
		Humidity.setText("Отн. влажность: " +
				cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.humidity)) + " %");
		Temperature.setText(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.temperatures[0])) + " °C");
		Pressure.setText(cursor.getString(
				cursor.getColumnIndex(WeatherSQLiteOpenHelper.precipitation)) + " мм.  рт. стлб");
		Temperature_1.setText(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.temperatures[1])) + " °C");
		Temperature_2.setText(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.temperatures[2])) + " °C");
		Temperature_3.setText(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.temperatures[3])) + " °C");
		Date_1.setText("+ 1 день\n");
		Date_2.setText("+ 2 дня\n");
		Date_3.setText("+ 3 дня\n");
		Icon_1.setImageResource(context.getResources().
				getIdentifier("drawable/" + cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.icon[1])), null, context.getPackageName()));
		Icon_2.setImageResource(context.getResources().
				getIdentifier("drawable/" + cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.icon[2])), null, context.getPackageName()));
		Icon_3.setImageResource(context.getResources().
				getIdentifier("drawable/" + cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.icon[3])), null, context.getPackageName()));


		cursor.close();
		openHelper.close();
		database.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.refresh:
				viewSwitcher.showPrevious();
				WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(this);
				SQLiteDatabase database = openHelper.getWritableDatabase();
				Cursor cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
				Intent intentRSS = new Intent(WeatherCityActivity.this, WeatherIntentService.class);
				cursor.moveToPosition(id);
				startService(intentRSS.putExtra("city", cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY))).putExtra("latitude", cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.LATITUDE))).putExtra("longitude", cursor.getString(
						cursor.getColumnIndex(WeatherSQLiteOpenHelper.LONGITUDE))));
				cursor.close();
				openHelper.close();
				database.close();

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

}
