package ru.ifmo.ctddev.skripnikov.Weather2;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public abstract class FindLocationActivity extends Activity {

    protected boolean locationIsFound = false;
    protected float lat;
    protected float lon;

    protected void findLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location == null) {
            locationIsFound = false;
            return;
        }
        lat = (float) location.getLatitude();
        lon = (float) location.getLongitude();
        locationIsFound = true;
    }
}
