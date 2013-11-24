package com.polarnick.day09;

import android.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.DateFormat;
import java.util.Date;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Utils {

    public static int BIG_WEATHER_ICONS_WIDTH = 256;
    public static String DEFIS = "—";

    public static String formatTemperature(int temperature) {
        return ((temperature > 0) ? "+" : "") + String.valueOf(temperature) + "°";
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private static final String SUFFIX_OF_TRUNCATION = "...";

    public static String truncateString(String string, int limit) {
        if (string.length() > limit) {
            return string.substring(0, limit - SUFFIX_OF_TRUNCATION.length()) + SUFFIX_OF_TRUNCATION;
        } else {
            return string;
        }
    }

    public static void addDivider(Context context, LinearLayout layout) {
        ImageView divider = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, 8, 0, 8);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(context.getResources().getColor(R.color.darker_gray));
        layout.addView(divider);
    }

    public static void addVerticalDivider(Context context, LinearLayout layout) {
        ImageView divider = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(8, 0, 8, 0);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(context.getResources().getColor(R.color.darker_gray));
        layout.addView(divider);
    }

    public static String formatHoursRange(Date start, int hoursRange, DateFormat formatter) {
        return formatter.format(start) + DEFIS + formatter.format(new Date(start.getTime() + hoursRange * 60L * 60 * 1000));
    }

    public static String roundToSignificantFigures(double num, int n) {
        String result = String.valueOf(num);
        int index = 0;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) != '0' && result.charAt(i) != '-' && result.charAt(i) != '.') {
                index = i;
                break;
            }
        }
        return result.substring(0, Math.min(index + n, result.length()));
    }

}
