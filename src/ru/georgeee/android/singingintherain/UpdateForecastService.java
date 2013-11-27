package ru.georgeee.android.singingintherain;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import dme.forecastiolib.ForecastIO;
import ru.georgeee.android.singingintherain.model.City;
import ru.georgeee.android.singingintherain.model.DatabaseHelperHolder;
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
    public static final String RESPONSE_VARIABLE_CITY = "city";

    public UpdateForecastService() {
        super(UpdateForecastService.class.getCanonicalName());
    }

    public static void startService(Context context, City city) {
        Intent nextIntent = new Intent(context, UpdateForecastService.class);
        nextIntent.putExtra(UpdateForecastService.ARGUMENT_CITY, city);
        nextIntent.addCategory(Intent.CATEGORY_DEFAULT);
        context.startService(nextIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        City city = (City) intent.getSerializableExtra(ARGUMENT_CITY);

        ForecastIO fio = new ForecastIO(getResources().getString(R.string.forecast_io_api_key));
        fio.setUnits(ForecastIO.UNITS_SI);

        if (fio.getForecast(city.getLatitude(), city.getLongitude())) {
            Forecast forecast = city.getForecast();
            if (forecast == null) forecast = new Forecast();
            if (fio.hasCurrently())
                forecast.setCurrently(fio.getCurrently());
            if (fio.hasDaily())
                forecast.setDaily(fio.getDaily());
            if (fio.hasMinutely())
                forecast.setMinutely(fio.getMinutely());
            if (fio.hasHourly())
                forecast.setHourly(fio.getHourly());
            city.setForecast(forecast);
            DatabaseHelperHolder.getHelper().getCitiesDataDao().update(city);

            Intent newIntent = new Intent();
            newIntent.setAction(BROADCAST_KEY);
            newIntent.addCategory(Intent.CATEGORY_DEFAULT);
            newIntent.putExtra(RESPONSE_VARIABLE_FORECAST, forecast);
            newIntent.putExtra(RESPONSE_VARIABLE_CITY, city);
            sendBroadcast(newIntent);
        }
    }


}