package com.example.Weather;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import dme.forecastiolib.FIOCurrently;
import dme.forecastiolib.FIODaily;
import dme.forecastiolib.ForecastIO;

import java.io.*;
import java.net.URL;

public class WeatherIntentService extends IntentService {
	private static String link, city;
	public  static  final double k =  0.75008;

	public WeatherIntentService() {
		super(" ");
	}
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		FIOCurrently currently;
		FIODaily daily;

		city = intent.getStringExtra("city");

		String Latitude = intent.getStringExtra("latitude"),Longitude = intent.getStringExtra("longitude");

		ForecastIO fio = null;

		try {
			fio = new ForecastIO("2d8543d8b1e828512669fd42b4492c30");
			fio.setUnits(ForecastIO.UNITS_SI);
			fio.setExcludeURL("hourly,minutely,flags,alerts");
			String url = fio.getUrl(Latitude,Longitude);
			URL weather = new URL(url);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							weather.openStream()));

			String inputLine;
			String r = "";

			while ((inputLine = in.readLine()) != null)
				r += inputLine;

			in.close();
			fio.getForecast(r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		currently = new FIOCurrently(fio);
		daily = new FIODaily(fio);
		int num_day_begin = num_daily_data(daily,currently.get().getByKey("time"));
		WeatherSQLiteOpenHelper openHelper = new WeatherSQLiteOpenHelper(this);
		SQLiteDatabase database = openHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put(WeatherSQLiteOpenHelper.icon[0],icon_weather(currently.get().getByKey("icon")
				.replaceAll("\"", ""),
				currently.get().getByKey("precipType").replaceAll("\"", "")));
		contentValues.put(WeatherSQLiteOpenHelper.icon[1],icon_weather(daily.getDay(num_day_begin).getByKey("icon")
				.replaceAll("\"", ""),
				daily.getDay(num_day_begin).getByKey("precipType").replaceAll("\"", "")));

		contentValues.put(WeatherSQLiteOpenHelper.icon[2],icon_weather(daily.getDay(num_day_begin+1).getByKey("icon")
				.replaceAll("\"", ""),
				daily.getDay(num_day_begin+1).getByKey("precipType").replaceAll("\"","")));
		contentValues.put(WeatherSQLiteOpenHelper.icon[3],icon_weather(daily.getDay(num_day_begin+2).getByKey("icon")
				.replaceAll("\"", ""),
				daily.getDay(num_day_begin+2).getByKey("precipType").replaceAll("\"","")));

		contentValues.put(WeatherSQLiteOpenHelper.temperatures[0],currently.get().getByKey("temperature")
				.substring(0, 4));
		contentValues.put(WeatherSQLiteOpenHelper.temperatures[1],daily.getDay(num_day_begin).getByKey("temperatureMin")
				.substring(0, 4));
		contentValues.put(WeatherSQLiteOpenHelper.temperatures[2],daily.getDay(num_day_begin + 1)
				.getByKey("temperatureMin").substring(0,4));
		contentValues.put(WeatherSQLiteOpenHelper.temperatures[3],daily.getDay(num_day_begin + 2)
				.getByKey("temperatureMin").substring(0,4));
		contentValues.put(WeatherSQLiteOpenHelper.humidity,String.valueOf(Double.parseDouble(currently.get()
				.getByKey("humidity")
				.substring(0, 4)) * 100));
		contentValues.put(WeatherSQLiteOpenHelper.precipitation, String.valueOf(Double.parseDouble(currently
				.get().getByKey("pressure")) * k)
				.substring(0, 6));

		assert database != null;
		database.update(
				WeatherSQLiteOpenHelper.TABLE_NAME,contentValues,WeatherSQLiteOpenHelper.CITY + "='" + city +"'",null
		);
		database.close();
		openHelper.close();
		Intent intentResponse = new Intent("com.example.Weather.RESPONSE");
		sendBroadcast(intentResponse);
	}

	public  String icon_weather(String icon, String precipType)  {
		if (precipType.equals("hail")) {
			return "strong_snow";
		}

		if (precipType.equals("fog")) {
			return "fog";
		}

		if (precipType.equals("null")) {
			switch (icon) {
				case "clear-day":
					return "sun";
				case "clear-night":
					return "night";
				case "rain" :
					return" rain";
				case "snow" :
					return  "show";
				case "sleet" :
					return "strong_rain";
				case "wind" :
					return  "wind";
				case "fog" :
					return  "fog";
				case "cloudy" :
					return  "could";
				case "partly-cloudy-day":
					return  "sun_could";
				case "partly-cloudy-night" :
					return "night_could";
			}

			return  "sun";
		}

		if (icon.equals("rain"))
			return "rain";

		if (icon.equals("cloudy")) {
			if (precipType.equals("null")) {
				return  "could";
			} else if (precipType.equals("rain")) {
				return  "rain";
			} else if (precipType.equals("snow")) {
				return  "show";
			} else if (precipType.equals("sleet")) {
				return  "strong_snow";
			}
		}

		if (icon.equals("partly-cloudy-day")) {
			if (precipType.equals("rain"))
				return  "sun_could_rain";
			else if (precipType.equals("snow"))
				return  "sun_could_show";
			return  "sun_could";

		}

		if (icon.equals("partly-cloudy-night")) {
			if (precipType.equals("rain"))
				return "night_could_rain";
			else if (precipType.equals("snow"))
				return "night_could_show";
			return "night_could";
		}

		if (icon.equals("clear-night")) {
			return  "night";
		}

		if (icon.equals("clear-day")) {
			return "sun";
		}

		return "could";
	}

	public int num_daily_data(FIODaily daily, String time)  {
		String [] a = time.split(" ");
		for(int i = 0; i<daily.days(); i++){
			String [] s =  daily.getDay(i).getByKey("time").split(" ");
			if (!a[0].equals(s[0])) {
				return i;
			}
		}
		return -1;
	}

}
