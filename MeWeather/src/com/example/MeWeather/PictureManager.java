package com.example.MeWeather;

/**
 * Created by ViruZ on 08.01.14.
 */
public class PictureManager {
    static public PictureClass getWeather(int code){
        Integer snow = R.drawable.snow;
        Integer clear = R.drawable.clear;
        Integer fair = R.drawable.fair;
        Integer foggy = R.drawable.foggy;
        Integer hot = R.drawable.hot;
        Integer rain = R.drawable.rain;
        Integer thunder = R.drawable.thunder;
        Integer cloudy = R.drawable.cloudy;
        Integer cloudypart = R.drawable.cloudypart;
        Integer rainyfair = R.drawable.fairrain;
        Integer thunderfair = R.drawable.thunderfair;
        Integer hell = R.drawable.hail;
        Integer heavyrain = R.drawable.heavyrain;
        Integer snowcloud = R.drawable.snowcloudy;
        Integer snowfair = R.drawable.snowfair;
        Integer rainysnow = R.drawable.rainysnow;
        Integer clearnight = R.drawable.nightclear;
        Integer snownight = R.drawable.nightsnow;
        Integer cloudynight = R.drawable.nightcloudy;
        Integer nightrain = R.drawable.nightrain;
        Integer nightsnow = R.drawable.nightsnow;
        Integer nightthunder = R.drawable.nightthunder;
        Integer day = 0;
        Integer night = 0;
        String weath = "";
        switch(code){
            case 395:
                weath = "Moderate or heavy snow in area with thunder";
                day = snow;
                night = snownight;
                break;
            case 392:
                weath = "Patchy light snow in area with thunder";
                day = snow;
                night = snownight;
                break;
            case 389:
                weath = "Moderate or heavy rain in area with thunder";
                day = rain;
                night = nightrain;
                break;
            case 386:
                weath = "Patchy light rain in area with thunder";
                day = thunder;
                night = nightthunder;
                break;
            case 377:
                weath = "Moderate or heavy showers of ice pellets";
                day = hell;
                night = day;
                break;
            case 374:
                weath = "Light showers of ice pellets";
                day = hell;
                night = hell;
                break;
            case 371:
                weath = "Moderate or heavy snow showers";
                day = snow;
                night = snownight;
                break;
            case 368:
                weath = "Light snow showers";
                day = snow;
                night = snownight;
                break;
            case 365:
                weath = "Moderate or heavy sleet showers";
                day = rainysnow;
                night = day;
                break;
            case 362:
                weath = "Light sleet showers";
                day = rainysnow;
                night = day;
                break;
            case 359:
                weath = "Torrential rain shower";
                day = rain;
                night = nightrain;
                break;
            case 356:
                weath = "Moderate or heavy rain shower";
                day = rain;
                night = day;
                break;
            case 353:
                weath = "Light rain shower";
                day = rain;
                night = day;
                break;
            case 350:
                weath = "Ice pellets";
                day = hell;
                night = hell;
                break;
            case 338:
                weath = "Heavy snow";
                day = snow;
                night = snownight;
                break;
            case 335:
                weath = "Patchy heavy snow";
                day = snow;
                night = snownight;
                break;
            case 332:
                weath = "Moderate snow";
                day = snow;
                night = snownight;
                break;
            case 329:
                weath = "Patchy moderate snow";
                day = snow;
                night = nightsnow;
                break;
            case 326:
                weath = "Light snow";
                day = snow;
                night = snownight;
                break;
            case 323:
                weath = "Patchy light snow";
                day = snow;
                night = snownight;
                break;
            case 320:
                weath = "Moderate or heavy sleet";
                day = rainysnow;
                night = rainysnow;
                break;
            case 317:
                weath = "Light sleet";
                day = rainysnow;
                night = day;
                break;
            case 314:
                weath = "Moderate or Heavy freezing rain";
                day = heavyrain;
                night = nightrain;
                break;
            case 311:
                weath = "Light freezing rain";
                day = snow;
                night = nightsnow;
                break;
            case 308:
                weath = "Heavy rain";
                day = heavyrain;
                night = nightrain;
                break;
            case 305:
                weath = "Heavy rain at times";
                day = heavyrain;
                night = nightrain;
                break;
            case 302:
                weath = "Moderate rain";
                day = rain;
                night = nightrain;
                break;
            case 299:
                weath = "Moderate rain at times";
                day = rain;
                night = nightrain;
                break;
            case 296:
                weath = "Light rain";
                day = rain;
                night = nightrain;
                break;
            case 293:
                weath = "Patchy light rain";
                day = rain;
                night = nightrain;
                break;
            case 284:
                weath = "Heavy freezing drizzle";
                day = rain;
                night = nightrain;
                break;
            case 281:
                weath = "Freezing drizzle";
                day = rain;
                night = nightrain;
                break;
            case 266:
                weath = "Light drizzle";
                day = rain;
                night = nightrain;
                break;
            case 263:
                weath = "Patchy light drizzle";
                day = rain;
                night = nightrain;
                break;
            case 260:
                weath = "Freezing fog";
                day = foggy;
                night = foggy;
                break;
            case 248:
                weath = "Fog";
                day = foggy;
                night = day;
                break;
            case 230:
                weath = "Blizzard";
                day = snow;
                night = snownight;
                break;
            case 227:
                weath = "Blowing snow";
                day = snow;
                night = snow;
                break;
            case 200:
                weath = "Thundery outbreaks in nearby";
                day = thunder;
                night = nightthunder;
                break;
            case 185:
                weath = "Patchy freezing drizzle nearby";
                day = rainyfair;
                night = nightrain;
                break;
            case 182:
                weath = "Patchy sleet nearby";
                day = rainysnow;
                night = nightrain;
                break;
            case 179:
                weath = "Patchy snow nearby";
                day = snow;
                night = snownight;
                break;
            case 176:
                weath = "Patchy rain nearby";
                day = rain;
                night = nightrain;
                break;
            case 143:
                weath = "Mist";
                day = cloudy;
                night = cloudynight;
                break;
            case 122:
                weath = "Overcast";
                day = cloudy;
                night = cloudynight;
                break;
            case 119:
                weath = "Cloudy";
                day = cloudy;
                night = cloudy;
                break;
            case 116:
                weath = "Partly Cloudy";
                day = cloudypart;
                night = cloudynight;
                break;
            case 113:
                weath = "Clear/Sunny";
                day = clear;
                night = clearnight;
                break;
        }
        PictureClass pc = new PictureClass(day, night, weath);
        return pc;
    }



}
