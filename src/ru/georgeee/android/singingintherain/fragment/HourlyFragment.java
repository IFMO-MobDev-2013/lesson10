package ru.georgeee.android.singingintherain.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.georgeee.android.singingintherain.misc.IconManager;
import ru.georgeee.android.singingintherain.R;
import ru.georgeee.android.singingintherain.model.Forecast;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 28.11.13
 * Time: 19:57
 * To change this template use File | Settings | File Templates.
 */
public class HourlyFragment extends DetailedListForecastFragment {
    @Override
    protected List<Forecast.DataPoint> getDataPoints(Forecast forecast) {
        Forecast.DataBlock hourly = forecast.getHourly();
        return hourly == null ? null : hourly.getDataPoints();
    }

    @Override
    protected DetailedListAdapter createListAdapter(LayoutInflater inflater) {
        return new HourlyAdapter(inflater);
    }

    protected class HourlyAdapter extends DetailedListAdapter{
        public HourlyAdapter(LayoutInflater inflater) {
            super(inflater);
        }

        protected final SimpleDateFormat hliTimeFormat = new SimpleDateFormat(getString(R.string.hliTimeFormat));
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.hourly_list_item, null, true);
            TextView time;
            TextView realTemp;
            TextView summary;
            ImageView imageView;
            realTemp = (TextView) rowView.findViewById(R.id.hli_realTemp);
            summary = (TextView) rowView.findViewById(R.id.hli_summary);
            time = (TextView) rowView.findViewById(R.id.hli_time);
            imageView = (ImageView) rowView.findViewById(R.id.hli_imageView);
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
            if (dataPoint != null) {
                realTemp.setText(getString(R.string.realTempLabelShort, dataPoint.getTemperature()));
                summary.setText(dataPoint.getSummary());
                time.setText(hliTimeFormat.format(dataPoint.getTime()));
            }   else{
                realTemp.setText("");
                summary.setText("");
                time.setText("");
            }
            int imageResourceId = IconManager.instance().getDrawable(dataPoint == null? null : dataPoint.getIconId(), IconManager.SIZE_SMALL);
            imageView.setImageResource(imageResourceId);
            return rowView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.hourly_list_item_expanded, null, true);
            TextView apparentTemp;
            TextView realTemp;
            TextView summary;
            TextView windSpeed;
            TextView pressure;
            ImageView imageView;
            apparentTemp = (TextView) rowView.findViewById(R.id.hliex_apparentTemp);
            realTemp = (TextView) rowView.findViewById(R.id.hliex_realTemp);
            windSpeed = (TextView) rowView.findViewById(R.id.hliex_windSpeed);
            pressure = (TextView) rowView.findViewById(R.id.hliex_pressure);
            summary = (TextView) rowView.findViewById(R.id.hliex_summary);
            imageView = (ImageView) rowView.findViewById(R.id.hliex_imageView);
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
            if (dataPoint != null) {
                apparentTemp.setText(getString(R.string.apparentTempLabel, dataPoint.getApparentTemperature()));
                realTemp.setText(getString(R.string.realTempLabel, dataPoint.getTemperature()));
                windSpeed.setText(getString(R.string.windSpeedLabel, dataPoint.getWindSpeed()));
                pressure.setText(getString(R.string.pressureLabel, dataPoint.getPressure()));
                summary.setText(dataPoint.getSummary());
            }   else{
                apparentTemp.setText("");
                realTemp.setText("");
                windSpeed.setText("");
                pressure.setText("");
                summary.setText("");
            }
            int imageResourceId = IconManager.instance().getDrawable(dataPoint == null? null : dataPoint.getIconId(), IconManager.SIZE_MEDIUM);
            imageView.setImageResource(imageResourceId);
            return rowView;
        }
    }
}
