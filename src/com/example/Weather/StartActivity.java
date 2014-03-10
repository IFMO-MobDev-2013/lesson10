package com.example.Weather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class StartActivity extends Activity {
	ListView listView;
	ArrayList<String> citys = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);
		Intent intent = new Intent(this, Refresher.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.cancel(pendingIntent);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 30 * 60 * 1000, 30 * 60 * 1000, pendingIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateListView(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add:
				startActivity(new Intent(this,AddActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listView) {
			menu.add("Удалить").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
					WeatherSQLiteOpenHelper openHelper = null;
					SQLiteDatabase database = null;
					Cursor cursor = null;
					try {
						openHelper = new WeatherSQLiteOpenHelper(StartActivity.this);
						database = openHelper.getWritableDatabase();
						assert database != null;
						cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
						cursor.moveToPosition(acmi.position);

						String city =  cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY));
						database.delete(
								WeatherSQLiteOpenHelper.TABLE_NAME,
								WeatherSQLiteOpenHelper.CITY + "='" + city + "'",
								null
						);

						updateListView(StartActivity.this);
					} finally {
						database.close();
						cursor.close();
						openHelper.close();
					}

					return true;
				}
			});

		}
	}

	public void updateListView(final Context context) {

		ArrayAdapter<String> adapter;
		Cursor cursor;
		ArrayList<String> citys = new ArrayList<String>();
		WeatherSQLiteOpenHelper helper = new WeatherSQLiteOpenHelper(context);

		SQLiteDatabase database = helper.getWritableDatabase();
		assert database != null;
		cursor = database.query(WeatherSQLiteOpenHelper.TABLE_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			citys.add(cursor.getString(cursor.getColumnIndex(WeatherSQLiteOpenHelper.CITY)));
		}
		cursor.close();
		helper.close();
		database.close();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, citys);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(StartActivity.this,WeatherCityActivity.class)
						.putExtra("id", position)
				);
			}
		});
	}


}
