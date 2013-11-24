package com.polarnick.day09.weather;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.polarnick.day09.R;
import com.polarnick.day09.Utils;
import com.polarnick.day09.entities.ForecastData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date: 19.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class DayView extends RelativeLayout {
    private static final DateFormat DAYS_FORMATTER = new SimpleDateFormat("EEE dd MMMM", Locale.UK);

    private final Context context;
    private final ForecastData forecast;

    public DayView(Context context, ForecastData forecast) {
        super(context);
        this.context = context;
        this.forecast = forecast;
    }

    public void init() {
        int lastId = 0;

        TextView date = new TextView(context);
        date.setId(++lastId);
        date.setText(DAYS_FORMATTER.format(new Date(forecast.getTime())));
        date.setTextSize(getResources().getDimension(R.dimen.dayDate));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        date.setLayoutParams(params);
        date.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(date);

        ImageView mainImage = new ImageView(context);
        mainImage.setId(++lastId);
        mainImage.setImageResource(getResources().getIdentifier(forecast.getIconType(), "drawable", context.getPackageName()));
        mainImage.setAdjustViewBounds(true);
        params = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dayMainImageWidth), RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, date.getId());
        mainImage.setLayoutParams(params);
        addView(mainImage, params);

        TextView temperature = new TextView(context);
        temperature.setId(++lastId);
        temperature.setText(" " + Utils.formatTemperature(forecast.getMaxTemperature()) + "... " + Utils.formatTemperature(forecast.getMinTemperature()));
        temperature.setTextSize(getResources().getDimension(R.dimen.dayTemperature));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RIGHT_OF, mainImage.getId());
        params.addRule(ALIGN_TOP, mainImage.getId());
        temperature.setLayoutParams(params);
        addView(temperature, params);

        int precipImageId = -1;
        if (forecast.getWindSpeed() > 0) {
            ImageView windImage = new ImageView(context);
            windImage.setId(++lastId);
            windImage.setImageResource(R.drawable.small_wind);
            windImage.setAdjustViewBounds(true);
            params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.dayWindIcon), getResources().getDimensionPixelSize(R.dimen.dayWindIcon));
            params.addRule(BELOW, temperature.getId());
            params.addRule(RIGHT_OF, mainImage.getId());
            windImage.setLayoutParams(params);
            addView(windImage);

            TextView windSpeed = new TextView(context);
            windSpeed.setId(++lastId);
            windSpeed.setText(" " + forecast.getWindSpeed() + " " + getResources().getString(R.string.WIND_SPEED_SUFFIX));
            windSpeed.setTextSize(getResources().getDimension(R.dimen.dayWindSpeed));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RIGHT_OF, windImage.getId());
            params.addRule(ALIGN_TOP, windImage.getId());
            windSpeed.setLayoutParams(params);
            addView(windSpeed);

            ImageView windDirection = new ImageView(context);
            windDirection.setId(++lastId);
            windDirection.setImageResource(R.drawable.small_arrow_up);
            windDirection.setRotation(forecast.getWindBearing());
            windDirection.setAdjustViewBounds(true);
            params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.dayWindVectorIcon), getResources().getDimensionPixelSize(R.dimen.dayWindVectorIcon));
            params.addRule(RIGHT_OF, windSpeed.getId());
            params.addRule(ALIGN_TOP, windSpeed.getId());
            windDirection.setLayoutParams(params);
            addView(windDirection);
        }

        if (forecast.getPrecipIntensity() > 0) {
            params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.precipIcon), getResources().getDimensionPixelSize(R.dimen.precipIcon));
            params.addRule(BELOW, lastId);
            params.addRule(RIGHT_OF, mainImage.getId());
            ImageView smallPrecipImage = new ImageView(context);
            smallPrecipImage.setId(++lastId);
            smallPrecipImage.setImageResource(getResources().getIdentifier("small_" + forecast.getPrecipType(), "drawable", context.getPackageName()));
            smallPrecipImage.setAdjustViewBounds(true);
            smallPrecipImage.setLayoutParams(params);
            addView(smallPrecipImage, params);
            precipImageId = smallPrecipImage.getId();

            TextView precipProbability = new TextView(context);
            precipProbability.setId(++lastId);
            precipProbability.setText(" " + Utils.DEFIS + " " + forecast.getPrecipProbability() + " %");
            precipProbability.setTextSize(getResources().getDimension(R.dimen.precipProbability));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RIGHT_OF, smallPrecipImage.getId());
            params.addRule(ALIGN_TOP, smallPrecipImage.getId());
            precipProbability.setLayoutParams(params);
            addView(precipProbability, params);

            TextView precipIntensity = new TextView(context);
            precipIntensity.setId(++lastId);
            precipIntensity.setText(" " + Utils.DEFIS + " " + Utils.roundToSignificantFigures(forecast.getPrecipIntensity(), 2) + " mm/h");
            precipIntensity.setTextSize(getResources().getDimension(R.dimen.precipIntencity));
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RIGHT_OF, smallPrecipImage.getId());
            params.addRule(BELOW, precipProbability.getId());
            precipIntensity.setLayoutParams(params);
            addView(precipIntensity, params);
        }

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(BELOW, mainImage.getId());
        if (precipImageId != -1) {
            params.addRule(BELOW, precipImageId);
        }
        params.addRule(BELOW, lastId);
        TextView summary = new TextView(context);
        summary.setId(++lastId);
        summary.setText(forecast.getSummary());
        summary.setTextSize(getResources().getDimension(R.dimen.daySummary));
//        summary.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.daySummaryWidth));
        summary.setLayoutParams(params);
        addView(summary, params);
    }
}
