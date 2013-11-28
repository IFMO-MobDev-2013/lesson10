package ru.georgeee.android.singingintherain.misc;

import ru.georgeee.android.singingintherain.R;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 28.11.13
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public class IconManager {
    public static final int SIZE_STANDARD = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_SMALL = 3;
    private static IconManager iconManager;

    public static IconManager instance() {
        return (iconManager == null ? (iconManager = new IconManager()) : iconManager);
    }

    public int getDrawable(String weatherCode) {
        return getDrawable(weatherCode, SIZE_STANDARD);
    }

    public int getDrawable(String weatherCode, int size) {
        if (weatherCode == null) return R.drawable.na;
        if (weatherCode.equals("clear-day"))
            return (size == SIZE_STANDARD ? R.drawable.clear_day : (size == SIZE_MEDIUM ? R.drawable.clear_day_medium : (size == SIZE_SMALL ? R.drawable.clear_day_small : R.drawable.na)));
        if (weatherCode.equals("clear-night"))
            return (size == SIZE_STANDARD ? R.drawable.clear_night : (size == SIZE_MEDIUM ? R.drawable.clear_night_medium : (size == SIZE_SMALL ? R.drawable.clear_night_small : R.drawable.na)));
        if (weatherCode.equals("cloudy"))
            return (size == SIZE_STANDARD ? R.drawable.cloudy : (size == SIZE_MEDIUM ? R.drawable.cloudy_medium : (size == SIZE_SMALL ? R.drawable.cloudy_small : R.drawable.na)));
        if (weatherCode.equals("fog"))
            return (size == SIZE_STANDARD ? R.drawable.fog : (size == SIZE_MEDIUM ? R.drawable.fog_medium : (size == SIZE_SMALL ? R.drawable.fog_small : R.drawable.na)));
        if (weatherCode.equals("partly-cloudy-day"))
            return (size == SIZE_STANDARD ? R.drawable.partly_cloudy_day : (size == SIZE_MEDIUM ? R.drawable.partly_cloudy_day_medium : (size == SIZE_SMALL ? R.drawable.partly_cloudy_day_small : R.drawable.na)));
        if (weatherCode.equals("partly-cloudy-night"))
            return (size == SIZE_STANDARD ? R.drawable.partly_cloudy_night : (size == SIZE_MEDIUM ? R.drawable.partly_cloudy_night_medium : (size == SIZE_SMALL ? R.drawable.partly_cloudy_night_small : R.drawable.na)));
        if (weatherCode.equals("rain"))
            return (size == SIZE_STANDARD ? R.drawable.rain : (size == SIZE_MEDIUM ? R.drawable.rain_medium : (size == SIZE_SMALL ? R.drawable.rain_small : R.drawable.na)));
        if (weatherCode.equals("sleet"))
            return (size == SIZE_STANDARD ? R.drawable.sleet : (size == SIZE_MEDIUM ? R.drawable.sleet_medium : (size == SIZE_SMALL ? R.drawable.sleet_small : R.drawable.na)));
        if (weatherCode.equals("snow"))
            return (size == SIZE_STANDARD ? R.drawable.snow : (size == SIZE_MEDIUM ? R.drawable.snow_medium : (size == SIZE_SMALL ? R.drawable.snow_small : R.drawable.na)));
        if (weatherCode.equals("wind"))
            return (size == SIZE_STANDARD ? R.drawable.wind : (size == SIZE_MEDIUM ? R.drawable.wind_medium : (size == SIZE_SMALL ? R.drawable.wind_small : R.drawable.na)));
        return R.drawable.na;
    }

}
