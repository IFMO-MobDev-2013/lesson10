/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2012 Zhenghong Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zh.wang.android.apis.yweathergetter4a;

import md.zoidberg.android.forecast.R;

public class ConditionDefinition {
	public static final int[] DRAWABLES = new int[] {
            R.drawable.severe_rain,
			R.drawable.severe_rain,
			R.drawable.thunderstorm,
			R.drawable.thunderstorm,
            R.drawable.thunderstorm,
			R.drawable.wet_rain,
            R.drawable.wet_rain,
            R.drawable.wet_rain,
            R.drawable.wet_rain,
			R.drawable.rain,
            R.drawable.wet_rain,
			R.drawable.severe_rain,
            R.drawable.severe_rain,
			R.drawable.severe_snow,
			R.drawable.snow,
			R.drawable.snow,
            R.drawable.snow,
            R.drawable.hail,
			R.drawable.snow,
			R.drawable.mist,
            R.drawable.mist,
            R.drawable.mist,
            R.drawable.mist,
            R.drawable.mist,
            R.drawable.broken,
            R.drawable.broken,
			R.drawable.cloudy,
            R.drawable.night_overcast,
            R.drawable.overcast,
            R.drawable.night_cloudy,
			R.drawable.cloudy,
			R.drawable.night,
			R.drawable.sunny,
			R.drawable.night,
			R.drawable.sunny,
			R.drawable.hail,
			R.drawable.blazing,
            R.drawable.thunderstorm,
            R.drawable.thunderstorm,
            R.drawable.thunderstorm,
            R.drawable.severe_rain,
            R.drawable.severe_snow,
            R.drawable.severe_snow,
            R.drawable.severe_snow,
            R.drawable.cloudy,
            R.drawable.changing_thunderstorm,
            R.drawable.severe_snow,
			R.drawable.changing_thunderstorm,
			R.drawable.sunny
	};

    public static final String[] CONDITION_LIST = new String[] {
            "tornado",
            "tropical storm",
            "hurricane",
            "severe thunderstorms",
            "thunderstorms",
            "mixed rain and snow",
            "mixed rain and sleet",
            "mixed snow and sleet",
            "freezing drizzle",
            "drizzle",
            "freezing rain",
            "showers",
            "showers",
            "snow flurries",
            "light snow showers",
            "blowing snow",
            "snow",
            "hail",
            "sleet",
            "dust",
            "foggy",
            "haze",
            "smoky",
            "blustery",
            "windy",
            "cold",
            "cloudy",
            "mostly cloudy (night)",
            "mostly cloudy (day)",
            "partly cloudy (night)",
            "partly cloudy (day)",
            "clear (night)",
            "sunny",
            "fair (night)",
            "fair (day)",
            "mixed rain and hail",
            "hot",
            "isolated thunderstorms",
            "scattered thunderstorms",
            "scattered thunderstorms",
            "scattered showers",
            "heavy snow",
            "scattered snow showers",
            "heavy snow",
            "partly cloudy",
            "thundershowers",
            "snow showers",
            "isolated thundershowers",
            "not available",
    };
	
	public static String getConditionByCode(int code) {
		return CONDITION_LIST[code];
	}

    public static int getDrawableByCode(int code) {
        return DRAWABLES[code];
    }

}
