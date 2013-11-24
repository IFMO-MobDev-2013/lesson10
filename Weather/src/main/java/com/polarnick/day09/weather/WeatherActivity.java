package com.polarnick.day09.weather;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.polarnick.day09.R;
import com.polarnick.day09.Utils;
import com.polarnick.day09.cities.CitiesManagementActivity;
import com.polarnick.day09.cities.SimpleCityListAdapter;
import com.polarnick.day09.dao.DatabaseHelperFactory;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastData;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;
import java.util.List;

/**
 * Date: 17.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class WeatherActivity extends Activity {

    private static final String WEATHER_PREFERENCES = "weatherPreferences";
    private static final String SELECTED_CITY_INDEX = "selectedCity";

    private Toast feedWasUpdatedToast;
    private List<City> cities;
    private City selectedCity;
    private int selectedCityIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedWasUpdatedToast = Toast.makeText(WeatherActivity.this, "Forecast was updated!", Toast.LENGTH_SHORT);
        setContentView(R.layout.forecast_main);

        SharedPreferences settings = getSharedPreferences(WEATHER_PREFERENCES, 0);
        selectedCityIndex = settings.getInt(SELECTED_CITY_INDEX, -1);

        ImageButton configureCities = (ImageButton) findViewById(R.id.configureCities);
        configureCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeatherActivity.this, CitiesManagementActivity.class));
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                feedWasUpdatedToast.show();
                for (City city : cities) {
                    try {
                        DatabaseHelperFactory.getHelper().getCityDAO().refresh(city);
                    } catch (SQLException e) {
                        Log.e(WeatherActivity.class.getName(), "Updating city with name=" + city.getName() + " and id=" + city.getId() + "!");
                        throw new RuntimeException(e);
                    }
                }
                showForecastForCity(selectedCity);
            }
        }, new IntentFilter(WeatherUpdaterService.FORECAST_UPDATE_TAG));
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelperFactory.setHelper(getApplicationContext());
        loadCities();
        loadCitiesToSpinner();
        startWeatherUpdaterService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(WEATHER_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SELECTED_CITY_INDEX, selectedCityIndex);
        editor.commit();
    }

    private void startWeatherUpdaterService() {
        Intent intent = new Intent(WeatherActivity.this, WeatherUpdaterService.class);
        startService(intent);
    }

    private void loadCities() {
        cities = DatabaseHelperFactory.getHelper().getCityDAO().getAllCities();
    }

    private void loadCitiesToSpinner() {
        Spinner citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        final SimpleCityListAdapter adapter = new SimpleCityListAdapter(this, cities, 16);
        citiesSpinner.setAdapter(adapter);
        if (cities.size() > selectedCityIndex && selectedCityIndex != -1) {
            citiesSpinner.setSelection(selectedCityIndex);
        }
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityIndex = position;
                selectedCity = cities.get(position);
                showForecastForCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        citiesSpinner.setEnabled(cities.size() > 0);
        if (cities.size() == 0) {
            selectedCityIndex = -1;
            selectedCity = null;
            showForecastForCity(selectedCity);
        }
    }

    private void showForecastForCity(City city) {
        try {
            if (city == null) {
                showMessage("Please add city!").setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(WeatherActivity.this, CitiesManagementActivity.class));
                    }
                });
                return;
            }
            DatabaseHelperFactory.getHelper().getCityDAO().refresh(city);
            ForecastForCity forecast = city.getForecast();
            if (forecast == null) {
                if (Utils.isOnline(this)) {
                    showRefreshingProgress("Forecast is loading...");
                } else {
                    showMessage("There are no internet and no forecast was downloaded in past!\nPlease enable internet.");
                }
                return;
            }
            if (forecast.getCurrent() == null) {
                DatabaseHelperFactory.getHelper().getForecastForCityDAO().refresh(forecast);
                DatabaseHelperFactory.getHelper().getForecastDataDAO().refresh(forecast.getCurrent());
            }
            showForecast(forecast);
        } catch (SQLException e) {
            Log.e(WeatherActivity.class.getName(), "Updating city with name=" + city.getName() + " and id=" + city.getId() + "!");
            throw new RuntimeException(e);
        }
    }

    private void showForecast(ForecastForCity forecast) {
        LinearLayout layoutForForecast = (LinearLayout) findViewById(R.id.forecastLayout);
        layoutForForecast.removeAllViews();

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutForForecast.addView(scrollView);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.addView(layout);

        LinearLayout.LayoutParams params;

        if (System.currentTimeMillis() - forecast.getCurrent().getTime() < 1L * 60 * 60 * 1000) {
            TextView todayHeader = new TextView(this);
            todayHeader.setText("Now:");
            todayHeader.setTextSize(getResources().getDimension(R.dimen.todayHeader));
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            todayHeader.setLayoutParams(params);
            layout.addView(todayHeader);
        }

        final TodayView todayView = new TodayView(this, forecast.getCurrent());
        todayView.init();
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        todayView.setLayoutParams(params);
        layout.addView(todayView);

        Utils.addDivider(this, layout);

        TextView hoursHeader = new TextView(this);
        hoursHeader.setText("Hourly:");
        hoursHeader.setTextSize(getResources().getDimension(R.dimen.hoursHeader));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        hoursHeader.setLayoutParams(params);
        layout.addView(hoursHeader);

        TextView hoursSummary = new TextView(this);
        hoursSummary.setText(forecast.getHoursSummary());
        hoursSummary.setTextSize(getResources().getDimension(R.dimen.hoursSummary));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        hoursSummary.setLayoutParams(params);
        layout.addView(hoursSummary);

        Utils.addDivider(this, layout);

        final HorizontalScrollView hoursScroll = new HorizontalScrollView(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        hoursScroll.setLayoutParams(params);
        layout.addView(hoursScroll);

        LinearLayout layoutForHours = new LinearLayout(this);
        layoutForHours.setOrientation(LinearLayout.HORIZONTAL);
        layoutForHours.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hoursScroll.addView(layoutForHours);

        List<ForecastData> hours = forecast.getHours();
        for (int i = 0; i < hours.size(); i++) {
            ForecastData data = hours.get(i);
            final HourView hour = new HourView(this, ForecastForCity.HOURS_DIFF[i], data);
            layoutForHours.addView(hour);
            hour.init();
            if (i != hours.size() - 1) {
                Utils.addVerticalDivider(this, layoutForHours);
            }
        }

        Utils.addDivider(this, layout);

        TextView daysHeader = new TextView(this);
        daysHeader.setText("Daily:");
        daysHeader.setTextSize(getResources().getDimension(R.dimen.daysHeader));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        daysHeader.setLayoutParams(params);
        layout.addView(daysHeader);

        TextView daysSummary = new TextView(this);
        daysSummary.setText(forecast.getDaysSummary());
        daysSummary.setTextSize(getResources().getDimension(R.dimen.daysSummary));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        daysSummary.setLayoutParams(params);
        layout.addView(daysSummary);

        Utils.addDivider(this, layout);

        final HorizontalScrollView daysScroll = new HorizontalScrollView(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        daysScroll.setLayoutParams(params);
        layout.addView(daysScroll);

        LinearLayout layoutForDays = new LinearLayout(this);
        layoutForDays.setOrientation(LinearLayout.HORIZONTAL);
        layoutForDays.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        daysScroll.addView(layoutForDays);

        List<ForecastData> days = forecast.getDays();
        for (int i = 0; i < days.size(); i++) {
            ForecastData data = days.get(i);
            final DayView day = new DayView(this, data);
            layoutForDays.addView(day);
            day.init();
            if (i != days.size() - 1) {
                Utils.addVerticalDivider(this, layoutForDays);
            }
        }

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                daysScroll.getParent().requestDisallowInterceptTouchEvent(false);
                hoursScroll.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        daysScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        hoursScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void showRefreshingProgress(String message) {
        LinearLayout layoutForForecast = (LinearLayout) findViewById(R.id.forecastLayout);
        layoutForForecast.removeAllViews();

        final TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(getResources().getDimension(R.dimen.labelsTextSize));
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
        params.gravity = Gravity.CENTER;
        messageView.setLayoutParams(params);
        messageView.setGravity(Gravity.BOTTOM);
        layoutForForecast.addView(messageView);

        final ProgressBar progressBar = new ProgressBar(this);

        LinearLayout layout2 = new LinearLayout(this);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout2.setGravity(Gravity.CENTER_HORIZONTAL);
        layout2.addView(progressBar);

        LinearLayout layout1 = new LinearLayout(this);
        layout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        layout1.addView(layout2);

        layoutForForecast.addView(layout1);
    }

    private TextView showMessage(String message) {
        LinearLayout layoutForForecast = (LinearLayout) findViewById(R.id.forecastLayout);
        layoutForForecast.removeAllViews();

        final TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(getResources().getDimension(R.dimen.labelsTextSize));
        messageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        messageView.setGravity(Gravity.CENTER);
        layoutForForecast.addView(messageView);
        return messageView;
    }
}
