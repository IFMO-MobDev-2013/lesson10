package com.ifmo.android.OdincovaAnn.MyWeather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeatherHandler extends DefaultHandler {
    String city;
    final String[] months = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября",
            "октябяь", "ноября", "декабря"};

    String buffer = "";
    final String CURRENT_CONDITION = "current_condition";
    final String WEATHER = "weather";
    final String TEMP = "temp_C";
    final String WEATHER_CODE = "weatherCode";
    final String WIND_SPEED = "windspeedKmph";
    final String WIND_DIR = "winddir16Point";
    final String PRECIP_MM = "precipMM";
    final String HUMIDITY = "humidity";
    final String PRESSURE = "pressure";
    final String CLOUD_COVER = "cloudcover";
    final String DATE = "date";
    final String TEMP_MAX = "tempMaxC";
    final String TEMP_MIN = "tempMinC";
    final String CITY_NAME = "query";
    final String TIME = "localObsDateTime";
    final String ERROR = "error";

    boolean weather;
    boolean condition;
    boolean temp;
    boolean weatherCode;
    boolean windSpeed;
    boolean windDir;
    boolean precip;
    boolean humidity;
    boolean pressure;
    boolean cloud;
    boolean date;
    boolean tempMax;
    boolean tempMin;
    boolean cityName;
    boolean time;

    NowWeather nowWeather;
    DaysWeather daysWeather;
    WeatherDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;
    int iterator = 0;
    String name;
    final Double CONVERTER = 0.75;

    WeatherHandler(String city, Context context, String name) {
        super();
        this.city = city;
        this.name = name;
        dataBaseHelper = new WeatherDataBaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attrs) throws SAXException {
        buffer = "";

        if (localName.equals(ERROR)) {
            throw new CityException();
        }

        if (localName.equals(WEATHER)) {
            weather = true;
            daysWeather = new DaysWeather();
            iterator++;

        }

        if (localName.equals(CURRENT_CONDITION)) {
            condition = true;
            nowWeather = new NowWeather();
        }

        if (localName.equals(TEMP)) {
            temp = true;
        }

        if (localName.equals(TEMP_MAX)) {
            tempMax = true;
        }

        if (localName.equals(TEMP_MIN)) {
            tempMin = true;
        }

        if (localName.equals(WEATHER_CODE)) {
            weatherCode = true;
        }

        if (localName.equals(WIND_DIR)) {
            windDir = true;
        }

        if (localName.equals(WIND_SPEED)) {
            windSpeed = true;
        }

        if (localName.equals(CLOUD_COVER)) {
            cloud = true;
        }

        if (localName.equals(HUMIDITY)) {
            humidity = true;
        }

        if (localName.equals(DATE)) {
            date = true;
        }

        if (localName.equals(PRECIP_MM)) {
            precip = true;
        }

        if (localName.equals(PRESSURE)) {
            pressure = true;
        }

        if (localName.equals(TIME)) {
            time = true;
        }

        if (localName.equals(CITY_NAME)) {
            cityName = true;
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(CURRENT_CONDITION)) {
            condition = false;
            database.execSQL("DROP TABLE IF EXISTS " + name);
            database.execSQL("CREATE TABLE " + name +
                    " (" + WeatherDataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WeatherDataBaseHelper.DATE
                    + " TEXT, " + WeatherDataBaseHelper.TEMP_MAX + " TEXT, " + WeatherDataBaseHelper.TEMP_MIN
                    + " TEXT, " + WeatherDataBaseHelper.WIND_SPEED + " TEXT, " + WeatherDataBaseHelper.WIND_DIR
                    + " TEXT, " + WeatherDataBaseHelper.WEATHER_CODE + " INTEGER, " + WeatherDataBaseHelper.PRECIP_MM
                    + " TEXT, " + WeatherDataBaseHelper.NIGHT + " INTEGER, " + WeatherDataBaseHelper.TEMP + " TEXT, "
                    + WeatherDataBaseHelper.HUMIDITY + " TEXT, " +
                    WeatherDataBaseHelper.PRESSURE + " TEXT, " + WeatherDataBaseHelper.CLOUD_COVER + " TEXT);");
            ContentValues condition = new ContentValues();
            condition.put(WeatherDataBaseHelper.TEMP, nowWeather.getTemp());
            condition.put(WeatherDataBaseHelper.WEATHER_CODE, nowWeather.getWeatherCode());
            condition.put(WeatherDataBaseHelper.WIND_SPEED, nowWeather.getWindSpeed());
            condition.put(WeatherDataBaseHelper.WIND_DIR, nowWeather.getWindDir());
            condition.put(WeatherDataBaseHelper.PRECIP_MM, nowWeather.getPrecip());
            condition.put(WeatherDataBaseHelper.HUMIDITY, nowWeather.getHumidity());
            condition.put(WeatherDataBaseHelper.PRESSURE, nowWeather.getPressure() * CONVERTER);
            condition.put(WeatherDataBaseHelper.CLOUD_COVER, nowWeather.getCloud());
            condition.put(WeatherDataBaseHelper.NIGHT, nowWeather.getNight());
            database.insert(name, null, condition);

        }

        if (localName.equals(WEATHER)) {
            weather = false;
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherDataBaseHelper.DATE, daysWeather.getDate());
            contentValues.put(WeatherDataBaseHelper.TEMP_MAX, daysWeather.getTempMax());
            contentValues.put(WeatherDataBaseHelper.TEMP_MIN, daysWeather.getTempMin());
            contentValues.put(WeatherDataBaseHelper.WIND_SPEED, daysWeather.getWindSpeed());
            contentValues.put(WeatherDataBaseHelper.WIND_DIR, daysWeather.getWindDir());
            contentValues.put(WeatherDataBaseHelper.WEATHER_CODE, daysWeather.getWeatherCode());
            contentValues.put(WeatherDataBaseHelper.PRECIP_MM, daysWeather.getPrecip());
            contentValues.put(WeatherDataBaseHelper.NIGHT, daysWeather.getNight());
            database.insert(name, null, contentValues);
            if (iterator == 5) {
                database.close();
                dataBaseHelper.close();
            }
        }

        if (localName.equals(TEMP)) {
            temp = false;
            nowWeather.setTemp(buffer);
        }

        if (localName.equals(TEMP_MAX)) {
            tempMax = false;
            daysWeather.setTempMax(buffer);
        }

        if (localName.equals(TEMP_MIN)) {
            tempMin = false;
            daysWeather.setTempMin(buffer);
        }

        if (localName.equals(WEATHER_CODE)) {
            weatherCode = false;
            if (condition) {
                nowWeather.setWeatherCode(Integer.parseInt(buffer));
            } else {
                daysWeather.setWeatherCode(Integer.parseInt(buffer));
            }
        }

        if (localName.equals(WIND_DIR)) {
            windDir = false;
            String wind = "";
            if (buffer.equals("N"))
                wind = "северный";
            if (buffer.equals("S"))
                wind = "южный";
            if (buffer.equals("E"))
                wind = "восточный";
            if (buffer.equals("W"))
                wind = "западный";
            if (buffer.equals("NE") || buffer.substring(1).equals("NE"))
                wind = "северо-восточный";
            if (buffer.equals("SE") || buffer.substring(1).equals("SE"))
                wind = "юго-восточный";
            if (buffer.equals("SW") || buffer.substring(1).equals("SW"))
                wind = "юго-западный";
            if (buffer.equals("NW") || buffer.substring(1).equals("NW"))
                wind = "северо-западный";
            if ("".equals(wind))
                wind = buffer;

            if (condition) {
                nowWeather.setWindDir(wind);
            } else {
                daysWeather.setWindDir(wind);
            }
        }

        if (localName.equals(WIND_SPEED)) {
            windSpeed = false;
            if (condition) {
                nowWeather.setWindSpeed(buffer);
            } else {
                daysWeather.setWindSpeed(buffer);
            }
        }

        if (localName.equals(CLOUD_COVER)) {
            cloud = false;
            nowWeather.setCloud(buffer);
        }

        if (localName.equals(HUMIDITY)) {
            humidity = false;
            nowWeather.setHumidity(buffer);
        }

        if (localName.equals(DATE)) {
            date = false;
            String day = buffer.substring(8);
            String month = buffer.substring(6, 7);
            daysWeather.setDate(day + " " + months[Integer.parseInt(month) - 1]);
        }

        if (localName.equals(PRECIP_MM)) {
            precip = false;
            if (condition) {
                nowWeather.setPrecip(buffer);
            } else {
                daysWeather.setPrecip(buffer);
            }
        }

        if (localName.equals(PRESSURE)) {
            pressure = false;
            nowWeather.setPressure(Double.parseDouble(buffer));
        }

        if (localName.equals(TIME)) {
            time = false;
            if (condition) {
                String amPm = buffer.substring(buffer.length() - 2);
                String hour = buffer.substring(buffer.length() - 8, buffer.length() - 6);
                if (("PM".equals(amPm) && ("10".equals(hour) || "11".equals(hour))) || ("AM".equals(amPm)) &&
                        ("00".equals(hour) || "01".equals(hour) || "02".equals(hour) || "03".equals(hour)
                                || "04".equals(hour) || "05".equals(hour) || "06".equals(hour) || "07".equals(hour)) ||
                        "12".equals(hour)) {
                    nowWeather.setNight(1);
                } else {
                    nowWeather.setNight(0);
                }
            } else {
                daysWeather.setNight(0);
            }
        }

        if (localName.equals(CITY_NAME)) {
            cityName = false;
            city = buffer;
        }
        buffer = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (weather || condition || cityName) {
            buffer += new String(ch, start, length);
        }
    }

}
