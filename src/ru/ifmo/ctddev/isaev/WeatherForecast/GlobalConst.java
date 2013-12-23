package ru.ifmo.ctddev.isaev.WeatherForecast;

import java.util.HashMap;

/**
 * User: Xottab
 * Date: 13.12.13
 */
public class GlobalConst {
    public static final String API_KEY = "709fd07a7a0afe681d653b91e8e82c25";
    public static final String YANDEX_API_KEY = "trnsl.1.1.20130925T205049Z.a92cd36d99706af9.b5a1aaf3c0f791ceb482029c486691897db34357";
    public static HashMap<String, String> translates = new HashMap<>();
    public static HashMap<String, Integer> images = new HashMap<>();

    static {
        translates.put("Overcast", "Облачно");
        translates.put("Mostly Cloudy", "Пасмурно");
        translates.put("Partly Cloudy", "Переменная облачность");
        translates.put("Clear", "Ясно");
        translates.put("Light Rain", "Небольшой дождь");
        translates.put("Drizzle", "Изморось");
        translates.put("Breezy", "Свежо");
        translates.put("Snow", "Снег");
        translates.put("Fog", "Туман");
        translates.put("Foggy", "Туманно");
        translates.put("Windy and Overcast", "Ветрено и облачно");
        translates.put("Windy and Mostly Cloudy", "Ветрено и пасмурно");
        translates.put("Breezy and Mostly Cloudy", "Свежо и пасмурно");
    }

    static {
        images.put("clear-day", R.drawable.clear_day);
        images.put("clear-night", R.drawable.clear_night);
        images.put("rain", R.drawable.rain);
        images.put("snow", R.drawable.snow);
        images.put("sleet", R.drawable.sleet);
        images.put("wind", R.drawable.wind);
        images.put("fog", R.drawable.fog);
        images.put("cloudy", R.drawable.cloudy);
        images.put("partly-cloudy-day", R.drawable.partly_cloudy);
        images.put("partly-cloudy-night", R.drawable.partly_cloudy_night);
    }

}
