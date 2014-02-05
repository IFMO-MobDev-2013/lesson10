package md.zoidberg.android.forecast;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Text;
import zh.wang.android.apis.yweathergetter4a.ConditionDefinition;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gfv on 03.02.14.
 */
public class WeatherFragment extends Fragment implements YahooWeatherInfoListener {
    String city;
    List<Forecast> forecasts;
    View rootView;
    ForecastDBHelper helper;
    ForecastAdapter adapter;

    public WeatherFragment(String city) {
        this.city = city;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new ForecastDBHelper(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.weather_forecast, container, false);
        forecasts = new ArrayList<>();
        adapter = new ForecastAdapter(getActivity(), this.forecasts);
        ((ListView)rootView.findViewById(R.id.listView)).setAdapter(adapter);
        YahooWeather.getInstance().queryYahooWeatherByPlaceName(getActivity(), city, this);

        refreshViewFromDb();
        return rootView;
    }

    private void refreshViewFromDb() {
        forecasts = helper.getForecastsForCity(city);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        helper.updateCity(weatherInfo.getLocationCity(), weatherInfo);
        refreshViewFromDb();
    }


    private class ForecastAdapter extends BaseAdapter {
        LayoutInflater inflater;
        public ForecastAdapter(Context activity, List<Forecast> forecasts) {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return forecasts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(R.layout.forecast_layout, null);
            }
            if (position == 0) {
                ((TextView)v.findViewById(R.id.date)).setText("Right now:");
            } else {
                ((TextView)v.findViewById(R.id.date)).setText("On " + forecasts.get(position).getDate() + ":");
            }

            ((TextView)v.findViewById(R.id.temperature)).setText(forecasts.get(position).getTemperature() + "°C");
            ((TextView)v.findViewById(R.id.forecastText)).setText(ConditionDefinition.getConditionByCode(forecasts.get(position).getConditionId()));
            ((ImageView)v.findViewById(R.id.forecastImage)).setImageResource(ConditionDefinition.getDrawableByCode(forecasts.get(position).getConditionId()));
            return v;
        }
    }
}
