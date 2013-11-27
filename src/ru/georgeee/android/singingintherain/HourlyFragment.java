package ru.georgeee.android.singingintherain;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import ru.georgeee.android.singingintherain.model.Forecast;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
public class HourlyFragment extends ForecastFragment {
    ListView hourList;
    HourlyListAdapter listAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.hourly_fragment;
    }

    @Override
    protected void onUpdateViewImpl() {
        if (rootView != null) {
            Forecast forecast = city==null?null:city.getForecast();
            Forecast.DataBlock hourly = forecast == null ? null : forecast.getHourly();
            if (hourly != null) {
                listAdapter.clear();
                listAdapter.addAll(hourly.getDataPoints());
            }
        }
    }

    @Override
    protected void onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        if (rootView != null) {
            hourList = (ListView) rootView.findViewById(R.id.hourList);
            listAdapter = new HourlyListAdapter(inflater);
            hourList.setAdapter(listAdapter);
            onUpdateViewImpl();
        }
    }

    protected class HourlyListAdapter extends ArrayAdapter<Forecast.DataPoint>{
        protected LayoutInflater inflator;
        public HourlyListAdapter(LayoutInflater inflator) {
            super(inflator.getContext(), R.layout.hourly_list_item);
            this.inflator = inflator;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = inflator.inflate(R.layout.hourly_list_item, null, true);
            TextView apparentTemp;
            TextView realTemp;
            TextView summary;
            TextView windSpeed;
            TextView pressure;
            ImageView imageView;
            apparentTemp = (TextView) rowView.findViewById(R.id.apparentTemp);
            realTemp = (TextView) rowView.findViewById(R.id.realTemp);
            windSpeed = (TextView) rowView.findViewById(R.id.windSpeed);
            pressure = (TextView) rowView.findViewById(R.id.pressure);
            summary = (TextView) rowView.findViewById(R.id.summary);
            imageView = (ImageView) rowView.findViewById(R.id.imageView);
            Forecast.DataPoint dataPoint = getItem(position);
            if (dataPoint != null) {
                apparentTemp.setText(getString(R.string.apparentTempLabelShort, dataPoint.getApparentTemperature()));
                realTemp.setText(getString(R.string.realTempLabelShort, dataPoint.getTemperature()));
                windSpeed.setText(getString(R.string.windSpeedLabelShort, dataPoint.getWindSpeed()));
                pressure.setText(getString(R.string.pressureLabelShort, dataPoint.getPressure()));
                summary.setText(dataPoint.getSummary());
            }   else{
                apparentTemp.setText("");
                realTemp.setText("");
                windSpeed.setText("");
                pressure.setText("");
                summary.setText("");
            }
            int imageResourceId = MainActivity.getDrawable(dataPoint == null? null : dataPoint.getIconId(), true);
            imageView.setImageResource(imageResourceId);
            return rowView;
        }
    }
}