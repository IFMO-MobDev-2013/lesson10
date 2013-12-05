package com.example.weathr;

import android.os.AsyncTask;
import be.hcpl.android.forecast.ForecastCallBuilder;
import be.hcpl.android.forecast.Units;
import be.hcpl.android.forecast.http.HttpServiceOutput;
import be.hcpl.android.forecast.http.ResponseListener;
import be.hcpl.android.forecast.model.ForecastResponse;

import java.util.Date;

/**
 * Created by Sergey on 11/21/13.
 */
class City {
    long id = -1;
    String name;

    Double  latitude, longitude;
    ForecastResponse weather;
    Date lastUpdate = new Date(0);

    public City(){}

    public City(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

public class WeatherGetter extends AsyncTask<City, City, Void> implements ResponseListener {

    protected City mCity = null;

    @Override
    protected Void doInBackground(City... params) {
        if (params != null || params.length > 0) {
            mCity = params[0];
            if (mCity != null) {
                ForecastCallBuilder builder = ForecastCallBuilder.getInstance();
                final String API_KEY = "8c6764b3dd349af8c42823da13f41bd6";
                builder.key(API_KEY)
                       .units(Units.SI)
                       .latitude(mCity.latitude)
                       .longitude(mCity.longitude)
                       .performCall(this);
            }
        }
        return null;
    }

    @Override
    public void handleResponse(HttpServiceOutput result) {
        if (result == null) return;
        ForecastResponse response = result.getForecastResponse();
        synchronized (mCity) {
            mCity.weather = response;
            mCity.lastUpdate = new Date(System.currentTimeMillis());
        }
        publishProgress(mCity);
    }

    @Override
    public void preExecution() {

    }

    @Override
    public void postExecution() {

    }
}
