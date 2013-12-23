package ru.ifmo.ctddev.isaev.WeatherForecast;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import dme.forecastiolib.FIOCurrently;
import dme.forecastiolib.FIODaily;
import dme.forecastiolib.FIODataPoint;
import dme.forecastiolib.ForecastIO;
import ru.ifmo.ctddev.isaev.WeatherForecast.orm.City;
import ru.ifmo.ctddev.isaev.WeatherForecast.translate.Translate;

/**
 * User: Xottab
 * Date: 12.12.13
 */
public class ForecastActivity extends Activity {
    static {
        Translate.setKey(GlobalConst.YANDEX_API_KEY);
    }

    static String PACKAGE_NAME;
    TextView currentDescription;
    TextView dailyDate;
    TextView dailyTemperatureFrom;
    TextView dailyTemperatureTo;
    TextView currentTemperature;
    TextView currentWind;
    TextView cityTitle;
    ImageView currentWeatherIcon;
    ImageView weatherIcon;
    LayoutInflater inflater;
    AlarmManager am;
    PendingIntent reloadIntent;
    Toast toast;

    class UpdateReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("shit","shit");
            toast.setText(context.getResources().getString(R.string.weather_reloaded));
            toast.show();
        }
    }

    UpdateReciever receiver;

    public static final String DATE_FORMAT = "dd.MM";

    public String getTemperaturePresentation(long temperature) {
        return (temperature > 0 ? "+" : "") + temperature;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(getApplicationContext(), "", 5);
        setContentView(R.layout.forecast);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout parent = (LinearLayout) findViewById(R.id.scrollLayout);


        receiver = new UpdateReciever();
        reloadIntent = PendingIntent.getService(this, 0, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 5000, reloadIntent);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        currentDescription = (TextView) findViewById(R.id.weatherDescription);
        cityTitle = (TextView) findViewById(R.id.cityTitle);
        currentTemperature = (TextView) findViewById(R.id.temperature);
        currentWind = (TextView) findViewById(R.id.wind);
        currentWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        final City city = (City) getIntent().getSerializableExtra("city");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ForecastIO fio = new ForecastIO(GlobalConst.API_KEY);
                    fio.setUnits(ForecastIO.UNITS_SI);
                    fio.setExcludeURL("hourly,minutely");
                    fio.getForecast(String.valueOf(city.getLatitude()), String.valueOf(city.getLongitude()));
                    cityTitle.setText(city.getName() + ", " + city.getCountry());
                    FIODataPoint currently = new FIOCurrently(fio).get();
                    String temp = currently.summary();
                    String currentDescr = GlobalConst.translates.containsKey(temp) ? GlobalConst.translates.get(temp) : temp;
                    currentDescription.setText(currentDescr);
                    currentTemperature.setText(getTemperaturePresentation(Math.round(currently.temperature())));
                    currentWind.setText(String.valueOf(Math.round(currently.windSpeed())));
                    currentWeatherIcon.setImageResource(GlobalConst.images.get(currently.icon()));


                    FIODaily daily = new dme.forecastiolib.FIODaily(fio);
                    for (int i = 1; i < daily.days(); i++) {
                        FIODataPoint json = daily.getDay(i);
                        LinearLayout day = (LinearLayout) inflater.inflate(R.layout.daily, null);
                        weatherIcon = (ImageView) day.findViewById(R.id.tomorrowWeather);
                        dailyTemperatureFrom = (TextView) day.findViewById(R.id.dailyTemperatureFrom);
                        dailyTemperatureTo = (TextView) day.findViewById(R.id.dailyTemperatureTo);
                        if (i != 1) {
                            dailyDate = (TextView) day.findViewById(R.id.day);
                            dailyDate.setText(json.time(DATE_FORMAT));
                        }

                        dailyTemperatureFrom.setText(getTemperaturePresentation(Math.round(json.temperatureMin())));
                        dailyTemperatureTo.setText(getTemperaturePresentation(Math.round(json.temperatureMax())));
                        weatherIcon.setImageResource(GlobalConst.images.get(json.icon()));
                        parent.addView(day);
                    }
                } catch (Exception e) {
                    Log.e("loh", "loh", e);
                }
            }
        }).run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        am.cancel(reloadIntent);
        unregisterReceiver(receiver);
    }
}
