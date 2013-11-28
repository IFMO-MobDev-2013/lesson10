package ru.georgeee.android.singingintherain;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.georgeee.android.singingintherain.model.Forecast;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
public class NowSectionFragment extends ForecastFragment {
    TextView apparentTemp;
    TextView realTemp;
    TextView summary;
    TextView windSpeed;
    TextView pressure;
    ImageView imageView;

    @Override
    protected int getLayoutId() {
        return R.layout.now_section_fragment;
    }

    @Override
    protected void onUpdateViewImpl() {
        if (rootView != null) {
            Forecast forecast = city==null?null:city.getForecast();
            Forecast.DataPoint currently = forecast == null ? null : forecast.getCurrently();
            if (currently != null) {
                apparentTemp.setText(getString(R.string.apparentTempLabel, currently.getApparentTemperature()));
                realTemp.setText(getString(R.string.realTempLabel, currently.getTemperature()));
                windSpeed.setText(getString(R.string.windSpeedLabel, currently.getWindSpeed()));
                pressure.setText(getString(R.string.pressureLabel, currently.getPressure()));
                summary.setText(currently.getSummary());
            }   else{
                apparentTemp.setText("");
                realTemp.setText("");
                windSpeed.setText("");
                pressure.setText("");
                summary.setText("");
            }
            int imageResourceId = MainActivity.getDrawable(currently == null? null : currently.getIconId());
            imageView.setImageResource(imageResourceId == -1 ? R.drawable.na : imageResourceId);
        }
    }

    @Override
    protected void onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        if (rootView != null) {
            apparentTemp = (TextView) rootView.findViewById(R.id.ns_apparentTemp);
            realTemp = (TextView) rootView.findViewById(R.id.ns_realTemp);
            windSpeed = (TextView) rootView.findViewById(R.id.ns_windSpeed);
            pressure = (TextView) rootView.findViewById(R.id.ns_pressure);
            summary = (TextView) rootView.findViewById(R.id.ns_summary);
            imageView = (ImageView) rootView.findViewById(R.id.ns_imageView);
            onUpdateViewImpl();
        }
    }
}