package ru.ifmo.mobdev.weather.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.ifmo.mobdev.weather.R;
import ru.ifmo.mobdev.weather.service.UpdateWeatherService;
import ru.ifmo.mobdev.weather.database.CityInformation;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;
import ru.ifmo.mobdev.weather.weather.Forecast;

/**
 * Created by Nick Smelik on 26.11.13.
 */
public class CurrentWeatherFragment extends Fragment {

    private static final IntentFilter UPDATE_FILTER = new IntentFilter(UpdateWeatherService.UPDATE_DONE);
    private dbOpenHelper helper;
    DataBaseTable table;
    Context context;
    CityInformation cityInformation;

    ImageView weatherImage;
    TextView description;
    TextView temp;
    TextView wind;
    TextView lastUpdate;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshWeather();
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_weather_activity, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        helper = new dbOpenHelper(getActivity());
        table = new DataBaseTable(helper.getWritableDatabase());
        weatherImage = (ImageView) getActivity().findViewById(R.id.WeatherImage);
        description = (TextView) getActivity().findViewById(R.id.Description);
        temp = (TextView) getActivity().findViewById(R.id.CurrentTemp);
        wind = (TextView) getActivity().findViewById(R.id.Wind);
        lastUpdate = (TextView) getActivity().findViewById(R.id.LastUpdate);

        refreshWeather();
    }

    private void refreshWeather() {
        if (table.getSelected() != null) {
            cityInformation = table.getForecast(table.getSelected().getId());
            if (cityInformation.getForecast() != null) {
                setWeather(description, weatherImage, cityInformation.getForecast());
                temp.setText(Integer.toString(cityInformation.getForecast().getToday().getMaxTemp()) + "°C");
                setWindDir(wind, cityInformation.getForecast());
                long time = System.currentTimeMillis();

                if (time - cityInformation.getLastUpdate() <= 300000) {
                    lastUpdate.setText(context.getString(R.string.update) + " " + context.getString(R.string.just_now));
                } else if (time - cityInformation.getLastUpdate() <= 1800000) {
                    lastUpdate.setText(context.getString(R.string.update) + " " + context.getString(R.string.half_hour_ago));
                } else if (time - cityInformation.getLastUpdate() <= 3600000) {
                    lastUpdate.setText(context.getString(R.string.update) + " " + context.getString(R.string.hour_ago));
                } else if (time - cityInformation.getLastUpdate() > 3600000) {
                    lastUpdate.setText(context.getString(R.string.update) + " " + ((time - cityInformation.getLastUpdate()) / 3600000) + context.getString(R.string.n_hour_ago));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, UPDATE_FILTER);
        refreshWeather();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private void setWindDir(TextView wind, Forecast forecast) {
        switch (forecast.getToday().getWindDir()) {
            case "N" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.N));
                break;
            case "NNE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.NNE));
                break;
            case "NE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.NE));
                break;
            case "ENE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.ENE));
                break;
            case "E" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.E));
                break;
            case "ESE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.ESE));
                break;
            case "SE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.SE));
                break;
            case "SSE" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.SSE));
                break;
            case "S" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.S));
                break;
            case "SSW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.SSW));
                break;
            case "SW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.SW));
                break;
            case "WSW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.WSW));
                break;
            case "W" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.W));
                break;
            case "WNW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.WNW));
                break;
            case "NW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.NW));
                break;
            case "NNW" : wind.setText(Integer.toString(cityInformation.getForecast().getToday().getWindSpeed()) + context.getString(R.string.km_per_hour) + " " +
                    context.getString(R.string.NNW));
                break;

        }
    }

    private void setWeather(TextView weather, ImageView weatherPicture, Forecast forecast) {
        String description = forecast.getToday().getDescription();
        switch(description) {
            case "Moderate or heavy snow in area with thunder" :
                weatherPicture.setImageResource(R.drawable.heavy_snow);
                weather.setText(context.getString(R.string.moderate_snow_with_thunder));
                break;
            case "Patchy light snow in area with thunder" :
                weatherPicture.setImageResource(R.drawable.light_snow);
                weather.setText(context.getString(R.string.patchy_snow_with_thunder));
                break;
            case "Moderate or heavy rain in area with thunder" :
                weatherPicture.setImageResource(R.drawable.heavy_rain_with_thunder);
                weather.setText(context.getString(R.string.moderate_rain_with_thunder));
                break;
            case "Patchy light rain in area with thunder" :
                weatherPicture.setImageResource(R.drawable.light_rain_with_thunder);
                weather.setText(context.getString(R.string.patchy_rain_with_thunder));
                break;
            case "Moderate or heavy showers of ice pellets" :
                weatherPicture.setImageResource(R.drawable.heavy_rain_ice_pallets);
                weather.setText(context.getString(R.string.moderate_showers_with_ice_pellets));
                break;
            case "Light showers of ice pellets" :
                weatherPicture.setImageResource(R.drawable.light_rain_ice_pallets);
                weather.setText(context.getString(R.string.light_showers_with_ice_pellets));
                break;
            case "Moderate or heavy snow showers" :
                weatherPicture.setImageResource(R.drawable.heavy_snow);
                weather.setText(context.getString(R.string.moderate_now_showers));
                break;
            case "Light snow showers" :
                weatherPicture.setImageResource(R.drawable.light_snow);
                weather.setText(context.getString(R.string.light_snow_showers));
                break;
            case "Moderate or heavy sleet showers" :
                weatherPicture.setImageResource(R.drawable.sleet_showers);
                weather.setText(context.getString(R.string.moderate_sleet_showers));
                break;
            case "Light sleet showers" :
                weatherPicture.setImageResource(R.drawable.sleet_showers);
                weather.setText(context.getString(R.string.light_sleet_showers));
                break;
            case "Torrential rain shower" :
                weatherPicture.setImageResource(R.drawable.rain_showers);
                weather.setText(context.getString(R.string.rain_showers));
                break;
            case "Moderate or heavy rain shower" :
                weatherPicture.setImageResource(R.drawable.rain_showers);
                weather.setText(context.getString(R.string.moderate_rain_showers));
                break;
            case "Light rain shower" :
                weatherPicture.setImageResource(R.drawable.light_rain_showers);
                weather.setText(context.getString(R.string.light_rain_showers));
                break;
            case "Ice pellets" :
                weatherPicture.setImageResource(R.drawable.sleet_showers);
                weather.setText(context.getString(R.string.ice_pellets));
                break;
            case "Heavy snow" :
                weatherPicture.setImageResource(R.drawable.heavy_snow);
                weather.setText(context.getString(R.string.heavy_snow));
                break;
            case "Patchy heavy snow" :
                weatherPicture.setImageResource(R.drawable.heavy_snow);
                weather.setText(context.getString(R.string.patchy_heavy_snow));
                break;
            case "Moderate snow" :
                weatherPicture.setImageResource(R.drawable.snow);
                weather.setText(context.getString(R.string.moderate_snow));
                break;
            case "Patchy moderate snow" :
                weatherPicture.setImageResource(R.drawable.patchy_snow);
                weather.setText(context.getString(R.string.patchy_moderate_snow));
                break;
            case "Light snow" :
                weatherPicture.setImageResource(R.drawable.light_snow);
                weather.setText(context.getString(R.string.light_snow));
                break;
            case "Patchy light snow" :
                weatherPicture.setImageResource(R.drawable.patchy_snow);
                weather.setText(context.getString(R.string.patchy_light_snow));
                break;
            case "Moderate or heavy sleet" :
                weatherPicture.setImageResource(R.drawable.heavy_rain_ice_pallets);
                weather.setText(context.getString(R.string.moderate_sleet));
                break;
            case "Light sleet" :
                weatherPicture.setImageResource(R.drawable.light_rain_ice_pallets);
                weather.setText(context.getString(R.string.light_sleet));
                break;
            case "Moderate or Heavy freezing rain" :
                weatherPicture.setImageResource(R.drawable.heavy_frrezing_drizzle);
                weather.setText(context.getString(R.string.moderate_freezing_rain));
                break;
            case "Light freezing rain" :
                weatherPicture.setImageResource(R.drawable.light_freezing_drizzle);
                weather.setText(context.getString(R.string.light_freezing_rain));
                break;
            case "Heavy rain" :
                weatherPicture.setImageResource(R.drawable.rain_showers);
                weather.setText(context.getString(R.string.heavy_rain));
                break;
            case "Heavy rain at times" :
                weatherPicture.setImageResource(R.drawable.patchy_rain);
                weather.setText(context.getString(R.string.heavy_rain_at_times));
                break;
            case "Moderate rain" :
                weatherPicture.setImageResource(R.drawable.rain);
                weather.setText(context.getString(R.string.moderate_rain));
                break;
            case "Moderate rain at times" :
                weatherPicture.setImageResource(R.drawable.patchy_rain);
                weather.setText(context.getString(R.string.moderate_rain_at_times));
                break;
            case "Light rain" :
                weatherPicture.setImageResource(R.drawable.light_rain);
                weather.setText(context.getString(R.string.light_rain));
                break;
            case "Patchy light rain" :
                weatherPicture.setImageResource(R.drawable.patchy_rain);
                weather.setText(context.getString(R.string.patchy_light_rain));
                break;
            case "Heavy freezing drizzle" :
                weatherPicture.setImageResource(R.drawable.heavy_frrezing_drizzle);
                weather.setText(context.getString(R.string.heavy_freezing_drizzle));
                break;
            case "Freezing drizzle" :
                weatherPicture.setImageResource(R.drawable.freezing_drizzle);
                weather.setText(context.getString(R.string.freezing_drizzle));
                break;
            case "Light drizzle" :
                weatherPicture.setImageResource(R.drawable.light_freezing_drizzle);
                weather.setText(context.getString(R.string.light_drizzle));
                break;
            case "Patchy light drizzle" :
                weatherPicture.setImageResource(R.drawable.light_freezing_drizzle);
                weather.setText(context.getString(R.string.patchy_light_drizzle));
                break;
            case "Freezing fog" :
                weatherPicture.setImageResource(R.drawable.fog);
                weather.setText(context.getString(R.string.freezing_fog));
                break;
            case "Fog" :
                weatherPicture.setImageResource(R.drawable.fog);
                weather.setText(context.getString(R.string.fog));
                break;
            case "Blizzard" :
                weatherPicture.setImageResource(R.drawable.blizzard);
                weather.setText(context.getString(R.string.blizzard));
                break;
            case "Blowing snow" :
                weatherPicture.setImageResource(R.drawable.blizzard);
                weather.setText(context.getString(R.string.blowing_snow));
                break;
            case "Thundery outbreaks in nearby" :
                weatherPicture.setImageResource(R.drawable.patchy_storm);
                weather.setText(context.getString(R.string.thundery_outbreaks_in_nearby));
                break;
            case "Patchy freezing drizzle nearby" :
                weatherPicture.setImageResource(R.drawable.patchy_rain);
                weather.setText(context.getString(R.string.patchy_freezing_drizzle_nearby));
                break;
            case "Patchy sleet nearby" :
                weatherPicture.setImageResource(R.drawable.patchy_snow);
                weather.setText(context.getString(R.string.patchy_sleet_nearby));
                break;
            case "Patchy snow nearby" :
                weatherPicture.setImageResource(R.drawable.patchy_snow);
                weather.setText(context.getString(R.string.patchy_snow_nearby));
                break;
            case "Patchy rain nearby" :
                weatherPicture.setImageResource(R.drawable.patchy_rain);
                weather.setText(context.getString(R.string.patchy_rain_nearby));
                break;
            case "Mist" :
                weatherPicture.setImageResource(R.drawable.mist);
                weather.setText(context.getString(R.string.mist));
                break;
            case "Overcast" :
                weatherPicture.setImageResource(R.drawable.cloudy);
                weather.setText(context.getString(R.string.overcast));
                break;
            case "Cloudy" :
                weatherPicture.setImageResource(R.drawable.cloudy);
                weather.setText(context.getString(R.string.cloudy));
                break;
            case "Partly Cloudy" :
                weatherPicture.setImageResource(R.drawable.partly_cloudy);
                weather.setText(context.getString(R.string.partly_cloudy));
                break;
            case "Sunny" :
                weatherPicture.setImageResource(R.drawable.sunny);
                weather.setText(context.getString(R.string.sunny));
                break;
            case "Clear" :
                weatherPicture.setImageResource(R.drawable.clear);
                weather.setText(context.getString(R.string.clear));
                break;
        }
    }
}
