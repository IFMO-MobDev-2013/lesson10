package com.example.weathr;

import android.os.AsyncTask;
import be.hcpl.android.forecast.ForecastCallBuilder;
import be.hcpl.android.forecast.Units;
import be.hcpl.android.forecast.http.HttpServiceOutput;
import be.hcpl.android.forecast.http.ResponseListener;
import be.hcpl.android.forecast.model.ForecastResponse;

/**
 * Created by Sergey on 11/21/13.
 */
class Location {
    Double latitude, longitude;
    String name;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String name) {
        this.name = name;
        findLocationByName();
    }

    public void findLocationByName()
    {
        latitude = longitude = 0.;
        //not implemented yet
    }
}

public class WeatherGetter extends AsyncTask<Location,ForecastResponse, Void> implements ResponseListener {

    @Override
    protected Void doInBackground(Location... params) {
        if (params != null || params.length > 0) {
            Location l = params[0];
            if (l != null) {
                ForecastCallBuilder builder = ForecastCallBuilder.getInstance();
                if (l.latitude == null && l.longitude == null)
                    l.findLocationByName();
                final String API_KEY = "8c6764b3dd349af8c42823da13f41bd6";
                builder.key(API_KEY)
                       .units(Units.SI)
                       .latitude(l.latitude)
                       .longitude(l.longitude)
                       .performCall(this);
            }
        }
        return null;
    }

    @Override
    public void handleResponse(HttpServiceOutput result) {
        if (result == null) return;
        ForecastResponse response = result.getForecastResponse();
        publishProgress(response);
    }

    @Override
    public void preExecution() {

    }

    @Override
    public void postExecution() {

    }
}
