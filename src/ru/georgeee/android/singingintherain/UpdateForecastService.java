package ru.georgeee.android.singingintherain;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import dme.forecastiolib.ForecastIO;
import ru.georgeee.android.singingintherain.model.City;
import ru.georgeee.android.singingintherain.model.Forecast;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class UpdateForecastService extends IntentService {

    public static final String BROADCAST_KEY = UpdateForecastService.class.getCanonicalName();

    public static final String ARGUMENT_CITY = "city";

    public static final String RESPONSE_VARIABLE_FORECAST = "forecast";

    public UpdateForecastService() {
        super(UpdateForecastService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        City city = (City) intent.getSerializableExtra(ARGUMENT_CITY);

        ForecastIO fio = new ForecastIO(getResources().getString(R.string.forecast_io_api_key));
        fio.setUnits(ForecastIO.UNITS_SI);
        fio.getForecast(city.getLatitude(), city.getLongitude());

        Forecast forecast = new Forecast();
        forecast.setCurrently(fio.getCurrently());
        forecast.setDaily(fio.getDaily());
        forecast.setMinutely(fio.getMinutely());
        forecast.setHourly(fio.getHourly());


        Intent newIntent = new Intent();
        newIntent.setAction(BROADCAST_KEY);
        newIntent.addCategory(Intent.CATEGORY_DEFAULT);
        newIntent.putExtra(RESPONSE_VARIABLE_FORECAST, forecast);
        sendBroadcast(newIntent);
    }

}