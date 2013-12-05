package ru.ifmo.mobdev.weather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.ifmo.mobdev.weather.R;
import ru.ifmo.mobdev.weather.database.CityInformation;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.weather.Forecast;

/**
 * Created by Nick on 22.11.13.
 */
public class ForecastAdapter extends BaseAdapter{

    DataBaseTable table;
    private Context context;
    private CityInformation currentState;
    long id;

    public ForecastAdapter(Context context, DataBaseTable table, long id) {
        this.context = context;
        this.table = table;
        this.id = id;
        this.currentState = table.getForecast(id);
    }

    public void refresh() {
        currentState = table.getForecast(id);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentState.getForecast().getDays();
    }

    @Override
    public Object getItem(int position) {
        return currentState.getForecast().getDay(position);
    }

    @Override
    public long getItemId(int position) {
        return currentState.getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.forecast, parent, false);
        TextView date = (TextView) view.findViewById(R.id.Date);
        TextView weather = (TextView) view.findViewById(R.id.Condition);
        ImageView weatherPicture = (ImageView) view.findViewById(R.id.ConditionImage);
        TextView maxTemp = (TextView) view.findViewById(R.id.MaxTemp);
        TextView minTemp = (TextView) view.findViewById(R.id.MinTemp);

        Forecast forecast = currentState.getForecast();


        setWeek(date, forecast, position);
        maxTemp.setText(Integer.toString(forecast.getDay(position).getMaxTemp()) + "°C");
        minTemp.setText(Integer.toString(forecast.getDay(position).getMinTemp()) + "°C");
        setWeather(weather, weatherPicture, forecast, position);



        return view;
    }

    private void setWeather(TextView weather, ImageView weatherPicture, Forecast forecast, int position) {
        String description = forecast.getDay(position).getDescription();
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
    private void setWeek(TextView date, Forecast forecast, int position) {
        String d = forecast.getDay(position).getDate().toString();
        switch (d.substring(0, 3)) {
            case "Sun" :
                date.setText(context.getString(R.string.sun));
                break;
            case "Mon" :
                date.setText(context.getString(R.string.mon));
                break;
            case "Tue" :
                date.setText(context.getString(R.string.tue));
                break;
            case "Wed" :
                date.setText(context.getString(R.string.wed));
                break;
            case "Thu" :
                date.setText(context.getString(R.string.thu));
                break;
            case "Fri" :
                date.setText(context.getString(R.string.fri));
                break;
            case "Sat" :
                date.setText(context.getString(R.string.sat));
                break;
        }
    }
}