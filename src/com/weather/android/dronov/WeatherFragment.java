package com.weather.android.dronov;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.weather.android.dronov.Forecast.Forecast;
import com.weather.android.dronov.Forecast.ForecastWeather;
import com.weather.android.dronov.database.WeatherDB;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 21.11.13
 * Time: 1:14
 * To change this template use File | Settings | File Templates.
 */

public class WeatherFragment extends Fragment {
    private View view;
    private WeatherDB weatherDB = null;
    private Forecast forecast;
    private int imageId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_fragment, container, false);
        String name = getArguments().getString(WeatherDB.COLUMN_CITY);
        weatherDB = new WeatherDB(getActivity());
        weatherDB.open();

        forecast = weatherDB.selectForecast(name);
        if (forecast == null)
            new RuntimeException("Bad in WEATHER FRAGMENT");

        if (forecast.getTemperature().equals("")) {
            new DownloadWeatherTask() {
                @Override
                protected void onPostExecute(Forecast forecast) {
                    update(forecast);
                    weatherDB.updateCity(forecast.getCity(), forecast);
                }
            }.execute();
        } else
            update(forecast);

        return view;
    }

    public void FragmentUpdate() {
        new DownloadWeatherTask() {
            @Override
            protected void onPostExecute(Forecast forecast) {
                update(forecast);
                weatherDB.updateCity(forecast.getCity(), forecast);
            }
        }.execute();
    }

    private void update(Forecast forecast) {
        TextView temperature = (TextView) view.findViewById(R.id.temperature);
        TextView weather = (TextView) view.findViewById(R.id.weather);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageWeather);
        imageView.setImageResource(getImageId(forecast.getWeather()));
        temperature.setText(forecast.getTemperature() + "°С");
        weather.setText(forecast.getWeather());

        ArrayList<ForecastWeather> nextWeather = forecast.getForecastWeathers();
        for (int i = 0; i < nextWeather.size(); i++) {
            ForecastWeather current = nextWeather.get(i);
            TextView dateView = (TextView) view.findViewById(this.getResources().getIdentifier("date" + (i + 1), "id", this.getClass().getPackage().getName()));
            TextView temperatureView = (TextView) view.findViewById(this.getResources().getIdentifier("temperature" + (i + 1), "id", this.getClass().getPackage().getName()));
            ImageView imageView1 = (ImageView) view.findViewById(this.getResources().getIdentifier("imageWeather" + (i + 1), "id", this.getClass().getPackage().getName()));

            dateView.setText(current.getDate());
            temperatureView.setText(current.getTemp() + "°С");
            imageView1.setImageResource(getImageId(current.getWeather()));
        }

    }

    public int getImageId(String weather) {
        weather = weather.toLowerCase();
        if (weather.contains("thunder"))
            return R.drawable.thunder;
        if (weather.contains("sunny"))
            return R.drawable.sunny;
        if (weather.contains("clear"))
            return R.drawable.sunny;
        if (weather.contains("cloudy"))
            return R.drawable.cloudy;
        if (weather.contains("overcast"))
            return R.drawable.overcast;
        if (weather.contains("mist") || weather.contains("fod"))
            return R.drawable.mist;
        if (weather.contains("snow"))
            return R.drawable.snow_cloud;
        if (weather.contains("drizzle"))
            return R.drawable.cloud_rain;
        if (weather.contains("rain"))
            return R.drawable.rain;
        return R.drawable.overcast;
    }


    private class DownloadWeatherTask extends AsyncTask<Void, Void, Forecast> {
        @Override
        protected Forecast doInBackground(Void... voids) {
            WeatherDownload weatherDownload = new WeatherDownload();
            weatherDownload.updateWeather(forecast.getCity(), forecast.getCountry());
            return weatherDownload.getResult();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        if (weatherDB != null)
            weatherDB.close();
    }
}
