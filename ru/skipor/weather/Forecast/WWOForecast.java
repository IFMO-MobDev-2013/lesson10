package ru.skipor.weather.Forecast;

import android.util.Log;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import ru.skipor.Utils.HTTPUtils;

/**
 * Created by Vladimir Skipor on 1/8/14.
 * Email: vladimirskipor@gmail.com
 */
public class WWOForecast implements ForecastProvider {
    private static int requestsUsed = 0;
    private final static String TAG = "WWOForecast";

    private static final String DATA_TAG = "data";
    private static final String CURRENT_CONDITION_TAG = "current_condition";
    private static final String REQUEST_TAG = "request";
    private static final String WEATHER_TAG = "weather";
//    private static final String REQUEST_URL_PATTERN = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=" // free key
//            + "%s" + "&format=json&num_of_days=5&key=vbm2ugj6z6mqf3e46cvutk2a";
//    private boolean premiumAPI = false;

    private static final String REQUEST_URL_PATTERN = "http://api.worldweatheronline.com/premium/v1/weather.ashx?q=" + // premium key for 60 days from 12.01.14
            "%s" + "&format=json&num_of_days=14&tp=24&key=q7j32nb4fcj6zkaactwzumns";
    private boolean premiumAPI = true;

    public static final String VALUE_TAG = "value";

    private JSONObject getJSONResponse(String cityName) throws HttpException, JSONException {
        String requestURL = null;
        try {
            requestURL = String.format(REQUEST_URL_PATTERN, java.net.URLEncoder.encode(cityName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error", e);
        }
        Log.d(TAG, "Requests Used: " + String.valueOf(++requestsUsed));
        final String content = HTTPUtils.getContent(requestURL);
        Log.d(TAG, content);
        return new JSONObject(content).getJSONObject(DATA_TAG);

    }


    // null means no CityName is Suitable
    public String getSuitableCityName(String cityName) throws ForecastException {
        try {
            JSONObject jsonObject = getJSONResponse(cityName);
            if (!jsonObject.has(REQUEST_TAG)) {
                return null;
            } else {
                return jsonObject.getJSONArray(REQUEST_TAG).getJSONObject(0).getString(WeatherForecast.CITY_NAME_TAG);
            }
        } catch (HttpException e) {
            throw new ForecastException(e);
        } catch (JSONException e) {
            throw new ForecastException(e);
        }


    }

    @Override
    public WeatherForecast getForecast(String cityName) throws ForecastException {

        try {
            WeatherForecast result = new WeatherForecast();
            JSONObject response = getJSONResponse(cityName);
            if (!response.has(REQUEST_TAG)) {
                throw new ForecastException("Invalid response, may be city name \"" + cityName + "\" is invalid ");
            }

            result.cityName = response.getJSONArray(REQUEST_TAG).getJSONObject(0).getString(WeatherForecast.CITY_NAME_TAG);
            JSONObject currentCondition = response.getJSONArray(CURRENT_CONDITION_TAG).getJSONObject(0);
            result.cloudCoverPercent = currentCondition.getInt(WeatherForecast.CLOUD_COVER_TAG);
            result.humidityPercent = currentCondition.getInt(WeatherForecast.HUMIDITY_TAG);
            result.setObservingTime(currentCondition.getString(WeatherForecast.OBSERVATION_TIME_TAG));
            result.precipitationMM = currentCondition.getDouble(WeatherForecast.PRECIPITATION_TAG);
            result.pressureMillibars = currentCondition.getInt(WeatherForecast.PRESSURE_TAG);
            result.tempC = currentCondition.getInt(WeatherForecast.TEMP_C_TAG);
            result.visibilityKm = currentCondition.getInt(WeatherForecast.VISIBILITY_TAG);
            result.weatherDescription = currentCondition.getJSONArray(WeatherForecast.WEATHER_DESCRIPTION_TAG).getJSONObject(0).getString(VALUE_TAG);
            result.weatherIconURL = currentCondition.getJSONArray(WeatherForecast.WEATHER_ICON_URL_TAG).getJSONObject(0).getString(VALUE_TAG);
            result.windDirection = currentCondition.getString(WeatherForecast.WIND_DIRECTION_TAG);
            result.windSpeedKmph = currentCondition.getInt(WeatherForecast.WIND_SPEED_TAG);

            JSONArray weather = response.getJSONArray(WEATHER_TAG);
            if (premiumAPI) {
                for (int i = 0; i < weather.length(); i++) {
                    WeatherForecast.DayForecast dayForecast = new WeatherForecast.DayForecast();
                    JSONObject dayResponse = weather.getJSONObject(i);
                    dayForecast.setDate(dayResponse.getString(WeatherForecast.DATE_TAG));
                    dayForecast.tempMaxC = dayResponse.getInt("maxtempC");
                    dayForecast.tempMinC = dayResponse.getInt("mintempC");
                    dayResponse = dayResponse.getJSONArray("hourly").getJSONObject(0);
                    dayForecast.precipitationMM = dayResponse.getDouble(WeatherForecast.PRECIPITATION_TAG);
                    dayForecast.weatherIconURL = dayResponse.getJSONArray(WeatherForecast.WEATHER_ICON_URL_TAG).getJSONObject(0).getString(VALUE_TAG);
                    dayForecast.weatherDescription = dayResponse.getJSONArray(WeatherForecast.WEATHER_DESCRIPTION_TAG).getJSONObject(0).getString(VALUE_TAG);
                    dayForecast.windDirection = dayResponse.getString(WeatherForecast.WIND_DIRECTION_TAG);
                    dayForecast.windSpeedKmph = dayResponse.getInt(WeatherForecast.WIND_SPEED_TAG);
                    result.dayForecasts.add(dayForecast);
                }

            } else {
                for (int i = 0; i < weather.length(); i++) {
                    WeatherForecast.DayForecast dayForecast = new WeatherForecast.DayForecast();
                    JSONObject dayResponse = weather.getJSONObject(i);
                    dayForecast.setDate(dayResponse.getString(WeatherForecast.DATE_TAG));
                    dayForecast.precipitationMM = dayResponse.getDouble(WeatherForecast.PRECIPITATION_TAG);
                    dayForecast.tempMaxC = dayResponse.getInt(WeatherForecast.TEMP_MAX_C_TAG);
                    dayForecast.tempMinC = dayResponse.getInt(WeatherForecast.TEMP_MIN_C_TAG);
                    dayForecast.weatherIconURL = dayResponse.getJSONArray(WeatherForecast.WEATHER_ICON_URL_TAG).getJSONObject(0).getString(VALUE_TAG);
                    dayForecast.weatherDescription = dayResponse.getJSONArray(WeatherForecast.WEATHER_DESCRIPTION_TAG).getJSONObject(0).getString(VALUE_TAG);
                    dayForecast.windDirection = dayResponse.getString(WeatherForecast.WIND_DIRECTION_TAG);
                    dayForecast.windSpeedKmph = dayResponse.getInt(WeatherForecast.WIND_SPEED_TAG);
                    result.dayForecasts.add(dayForecast);
                }
            }

            return result;

        } catch (HttpException e) {
            throw new ForecastException(e);
        } catch (JSONException e) {
            throw new ForecastException(e);
        } catch (ParseException e) {
            throw new ForecastException(e);
        }

    }

}
