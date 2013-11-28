package ifmo.mobdev.Metcast;

public class WeatherPicture {

    String state;
    int id;

    public WeatherPicture(String s) {
        state = s;
        id = -1;
    }

    public int getID() {
        if (id != -1) return id;
        else {
            switch (Integer.parseInt(state)) {
                case 395 :
                    id = R.drawable.snow;
                    break;
                case 392 :
                    id = R.drawable.snow;
                    break;
                case 389 :
                    id = R.drawable.thunder2;
                    break;
                case 386 :
                    id = R.drawable.thunder3;
                    break;
                case 377 :
                    id = R.drawable.ice_pellets;
                    break;
                case 374 :
                    id = R.drawable.ice_pellets;
                    break;
                case 371 :
                    id = R.drawable.snow_rain_2;
                    break;
                case 368 :
                    id = R.drawable.snow_rain_3;
                    break;
                case 365 :
                    id = R.drawable.snow_rain_2;
                    break;
                case 362 :
                    id = R.drawable.snow_rain_3;
                    break;
                case 359 :
                    id = R.drawable.rain;
                    break;
                case 356 :
                    id = R.drawable.rain;
                    break;
                case 353 :
                    id = R.drawable.rain;
                    break;
                case 350 :
                    id = R.drawable.hail;
                    break;
                case 338 :
                    id = R.drawable.snow;
                    break;
                case 335 :
                    id = R.drawable.snow;
                    break;
                case 332 :
                    id = R.drawable.snow;
                    break;
                case 329 :
                    id = R.drawable.snow;
                    break;
                case 326 :
                    id = R.drawable.snow;
                    break;
                case 323 :
                    id = R.drawable.snow;
                    break;
                case 320 :
                    id = R.drawable.snow_rain_2;
                    break;
                case 317 :
                    id = R.drawable.snow_rain_3;
                    break;
                case 314 :
                    id = R.drawable.hail;
                    break;
                case 311 :
                    id = R.drawable.hail;
                    break;
                case 308 :
                    id = R.drawable.rain;
                    break;
                case 305 :
                    id = R.drawable.rain;
                    break;
                case 302 :
                    id = R.drawable.rain;
                    break;
                case 299 :
                    id = R.drawable.rain;
                    break;
                case 296 :
                    id = R.drawable.rain;
                    break;
                case 293 :
                    id = R.drawable.rain;
                    break;
                case 284 :
                    id = R.drawable.sosulki;
                    break;
                case 281 :
                    id = R.drawable.sosulki;
                    break;
                case 266 :
                    id = R.drawable.sosulki;
                    break;
                case 263 :
                    id = R.drawable.sosulki;
                    break;
                case 260 :
                    id = R.drawable.fog2;
                    break;
                case 248 :
                    id = R.drawable.fog;
                    break;
                case 230 :
                    id = R.drawable.snow4;
                    break;
                case 227 :
                    id = R.drawable.snow;
                    break;
                case 200 :
                    id = R.drawable.lightning;
                    break;
                case 185 :
                    id = R.drawable.sosulki;
                    break;
                case 182 :
                    id = R.drawable.snow_rain;
                    break;
                case 179 :
                    id = R.drawable.snow_sun;
                    break;
                case 176 :
                    id = R.drawable.rain_sun;
                    break;
                case 143 :
                    id = R.drawable.fog3;
                    break;
                case 122 :
                    id = R.drawable.overcast;
                    break;
                case 119 :
                    id = R.drawable.cloudy;
                    break;
                case 116 :
                    id = R.drawable.cloud2;
                    break;
                case 113 :
                    id = R.drawable.sunny;
                    break;
                default:
                    id = R.drawable.na;
                    break;
            }
            return id;
        }
    }
}
