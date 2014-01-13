package com.example.MeWeather;


import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ViruZ on 09.01.14.
 */
public class GeoCoder {
    private static final Geocoder geocoder = new Geocoder();

    public static synchronized ArrayList<City> getLocate(String address) {
        Preconditions.checkNotNull(address);
        ArrayList<City> cities = new ArrayList<>();
        if (address.isEmpty()) {
            return cities;
        }
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).setLanguage("en").getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        for (GeocoderResult geocoderResult : geocoderResponse.getResults()) {
            List<GeocoderAddressComponent> comp = geocoderResult.getAddressComponents();
            String name = comp.get(0).getLongName(),
                    country = comp.get(comp.size() - 1).getLongName();
            for (GeocoderAddressComponent i : comp) {
                for (String j : i.getTypes()) {
                    if (j.equals("locality")) {
                        name = i.getLongName();
                    } else if (j.equals("country")) {
                        country = i.getLongName();
                    }

                }
            }
            City city = new City(name, country,
                    geocoderResult.getGeometry().getLocation().getLat().floatValue(),
                    geocoderResult.getGeometry().getLocation().getLng().floatValue());
            cities.add(city);
        }
        return cities;
    }
}
