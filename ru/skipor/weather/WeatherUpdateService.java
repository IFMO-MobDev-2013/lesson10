package ru.skipor.weather;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import ru.skipor.Utils.InternalSrorageUtils;
import ru.skipor.weather.Forecast.ForecastProvider;
import ru.skipor.weather.Forecast.WWOForecast;
import ru.skipor.weather.Forecast.WeatherForecast;


/**
 * Created by Vladimir Skipor on 11/9/13.
 * Email: vladimirskipor@gmail.com
 */
public class WeatherUpdateService extends IntentService {
    private static final String TAG = "WeatherUpdateService";

    public static final String ACTION_UPDATE_ALL = "Update all";
    public static final String ACTION_UPDATE_ONE = "Update one";
    public static final String EXTRA_CITY_NAME = "city name";
    public static final String EXTRA_INFORM_ABOUT_UPDATE = "Inform about update";
    public static final String EXTRA_RECEIVER = "Receiver";
    public static final int RESULT_CODE_UPTODATE = 0xdeadbeaf;
    public static final int RESULT_CODE_ERROR = 0xbeafdead;

    private static final String TODAY_ADDITION = "Today";
    //    RSSFeedReader feedReader;
    WeatherDatabaseHelper myDatabaseHelper = WeatherDatabaseHelper.getInstance(this);
    ForecastProvider forecastProvider = new WWOForecast();


    Handler uiHandler = new Handler(Looper.getMainLooper());

    private static final String UPDATE_MESSAGE = "All weather are up to date";



    public WeatherUpdateService() {
        super("ru.skipor.RssReader.FeedsDatabase.WeatherUpdateService");
//        feedReader = new SAXRSSReader();
    }

    @Override
    public void onCreate() {
        myDatabaseHelper.open();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        myDatabaseHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String actionType = intent.getAction();

        Log.i(TAG, "service intent handle");
        Log.i(TAG, "Action type is: " + actionType);


        if (ACTION_UPDATE_ALL.equals(actionType)) {
            updateAll();
            Log.i(TAG, "All feeds updated ");
            if (intent.getBooleanExtra(EXTRA_INFORM_ABOUT_UPDATE, false)) {
                makeToast(UPDATE_MESSAGE);

            }

        } else if (ACTION_UPDATE_ONE.equals(actionType)) {

            final String cityName = intent.getStringExtra(EXTRA_CITY_NAME);
            updateOne(cityName);
            if (intent.getBooleanExtra(EXTRA_INFORM_ABOUT_UPDATE, false)) { // use resultReceiver
                ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                assert resultReceiver != null;

                Cursor cursor = null;
                try {
                cursor = myDatabaseHelper.fetchCurrentCondition(cityName);
                if (cursor != null && cursor.getCount() > 0) {
                    resultReceiver.send(RESULT_CODE_UPTODATE, Bundle.EMPTY);
                } else {
                    resultReceiver.send(RESULT_CODE_ERROR, Bundle.EMPTY);

                }
                } finally {
                    if(cursor != null) {
                        cursor.close();

                    }
                }


            }
        }


    }

    private void updateAll() {
        Cursor cursor = null;
        try {
            cursor = myDatabaseHelper.fetchAllCities();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()){

                String coulumnName = cursor.getColumnName(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_CITY_NAME));
                Log.d(TAG, coulumnName + " " + WeatherDatabaseHelper.KEY_CITY_NAME);
                String cityName = cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.KEY_CITY_NAME));
                updateOne(cityName);

                cursor.moveToNext();

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void updateOne(String cityName) {
        try {
            WeatherForecast uptodateForecast = forecastProvider.getForecast(cityName);
            myDatabaseHelper.createOrRecreateCityTable(cityName);

            WeatherForecast.DayForecast todayForecast = uptodateForecast.dayForecasts.get(0);

            Log.d(TAG, "Forecast uploaded: " + todayForecast.toString());

            myDatabaseHelper.createCurrentCondition(cityName, uptodateForecast.tempC, uptodateForecast.weatherDescription, InternalSrorageUtils.downloadAndSaveBitmap(this, uptodateForecast.weatherIconURL));



            myDatabaseHelper.createDayForecast(cityName,  TODAY_ADDITION +  ", " + todayForecast.getDayOfWeek(), todayForecast.tempMaxC,
                    todayForecast.tempMinC, InternalSrorageUtils.downloadAndSaveBitmap(this, todayForecast.weatherIconURL));
            for (int i = 1; i < uptodateForecast.dayForecasts.size(); i++) {
                WeatherForecast.DayForecast dayForecast = uptodateForecast.dayForecasts.get(i);
                myDatabaseHelper.createDayForecast(cityName, dayForecast.getDayAndMonth() + ", " + dayForecast.getDayOfWeek(), dayForecast.tempMaxC,
                        dayForecast.tempMinC, InternalSrorageUtils.downloadAndSaveBitmap(this, dayForecast.weatherIconURL));

            }



//            makeToast("Feed " + feedURL + " is up to date");
        } catch (Exception e) {
            Log.e(TAG, "Error " + cityName, e);
            Cursor cursor = null;
            try {
                cursor = myDatabaseHelper.fetchDayForecasts(cityName);
                if (cursor.getCount() == 0) {

                }

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }



    }

    private boolean makeToast(String message) {
        return uiHandler.post(new ToastRunnable(message, Toast.LENGTH_LONG));
    }

    class ToastRunnable implements Runnable {
        private final String toastText;
        private final int toastDuration;

        ToastRunnable(String toastText, int toastDuration) {
            this.toastDuration = toastDuration;
            this.toastText = toastText;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), toastText, toastDuration).show();

        }
    }
}
