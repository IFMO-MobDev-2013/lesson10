package com.example.weather;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 19.11.13
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
public class CityGeocoder {

    private Geocoder geocoder = null;

    public ArrayList<City> getCities(String query) {
        ArrayList<City> cities = new ArrayList<City>();
        geocoder = new Geocoder();
        try {
            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(query).getGeocoderRequest();

            GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

            for (GeocoderResult geocoderResult : geocoderResponse.getResults()) {
                cities.add(new City(
                        geocoderResult.getFormattedAddress(),
                        geocoderResult.getGeometry().getLocation().getLat().toString(),
                        geocoderResult.getGeometry().getLocation().getLng().toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cities;
    }
}

