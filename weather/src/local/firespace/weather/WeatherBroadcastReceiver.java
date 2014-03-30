package local.firespace.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.*;

public class WeatherBroadcastReceiver extends BroadcastReceiver {
	private TextView date, temp, wind, press, hum, curCity;
	private String city, country;
	private long city_id;
	ListView lv2, lv3;
	private EditText addCity;
	private Button imgbut;
	private ImageView picture;
	private WeatherDBAdapter mDbHelper;
	Context context;

	public WeatherBroadcastReceiver() {
		super();
	}

	public WeatherBroadcastReceiver(TextView cur, ListView l2, ListView l3, TextView dt, TextView tmp, TextView w, TextView pr, TextView h,
	                                Button mb, ImageView pic, EditText add) {
		curCity = cur;
		date = dt;
		temp = tmp;
		wind = w;
		press = pr;
		hum = h;
		picture = pic;
		lv2 = l2;
		addCity = add;
		imgbut = mb;
		lv3 = l3;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String screen = intent.getStringExtra("screen");
		mDbHelper = new WeatherDBAdapter(context);
		mDbHelper.open();
		this.context = context;
		if (screen.equals("YES")) {
			setToday();
			setWeek();
			WeatherActivity.dialog.dismiss();
			String xml = intent.getStringExtra("xml");
			if (xml.equals(WeatherIntentService.DATA_LOAD_BAD)) {
				picture.setImageResource(R.drawable.na);
				Toast toast = Toast.makeText(context, "No connection!", 1000);
				toast.show();
			}
			String items = intent.getStringExtra("items");
			if (items.equals(WeatherIntentService.DATA_PARSE_BAD)) {
				picture.setImageResource(R.drawable.na);
				Toast toast = Toast.makeText(context, "Wrong city!", 1000);
				toast.show();
			}
		} else {
			Toast toast = Toast.makeText(context, "RELOAD", 1000);
			toast.show();
		}
	}

	private void setToday() {
		city = curCity.getText().toString();
		city_id = mDbHelper.getCityIdByName(city);
		Cursor cursor = mDbHelper.fetchCityToday(city_id);

		int dateind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DATE);
		int tempind = cursor.getColumnIndex(WeatherDBAdapter.KEY_TEMP);
		int descrind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DESCR);
		int windind = cursor.getColumnIndex(WeatherDBAdapter.KEY_WIND);
		int pressind = cursor.getColumnIndex(WeatherDBAdapter.KEY_PRESS);
		int humind = cursor.getColumnIndex(WeatherDBAdapter.KEY_HUM);
		int pic_id = cursor.getColumnIndex(WeatherDBAdapter.KEY_ICON_ID);

		cursor.moveToNext();
		String dt, dr, tm, wn, pr, hm;
		int p_id;

		try {
			dt = cursor.getString(dateind);
			dr = cursor.getString(descrind);
			tm = cursor.getString(tempind);
			wn = cursor.getString(windind);
			pr = cursor.getString(pressind);
			hm = cursor.getString(humind);
			p_id = cursor.getInt(pic_id);
			date.setText("Observation time: " + dt);
			if (Integer.parseInt(tm) > 0) temp.setText("+" + tm + "°C");
			else temp.setText(tm + "°C");
			wind.setText("Wind " + wn + " Km/h");
			press.setText("Pressure " + pr + " mb");
			hum.setText("Humidity " + hm + "%");
			picture.setImageResource(p_id);
		} catch (Exception e) {
		}
		cursor.close();
	}

	private void setWeek() {
		city = curCity.getText().toString();
		city_id = mDbHelper.getCityIdByName(city);
		Cursor artCursor = mDbHelper.fetchCityWeek(city_id);
		String[] from = new String[]{WeatherDBAdapter.KEY_DATE, WeatherDBAdapter.KEY_MIN_TEMP,
				WeatherDBAdapter.KEY_MAX_TEMP, WeatherDBAdapter.KEY_DESCR, WeatherDBAdapter.KEY_ICON_ID};
		int[] to = new int[]{R.id.tv2date, R.id.tv2mintemp, R.id.tv2maxtemp, R.id.tv2descr, R.id.weekPic};
		SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(context, R.layout.week_list_item, artCursor, from, to);
		lv2.setAdapter(artAdapter);
	}
}