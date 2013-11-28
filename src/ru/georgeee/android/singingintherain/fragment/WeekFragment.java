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
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */
public class WeekFragment extends DetailedListForecastFragment {
    @Override
    protected List<Forecast.DataPoint> getDataPoints(Forecast forecast) {
        Forecast.DataBlock daily = forecast.getDaily();
        return daily == null ? null : daily.getDataPoints();
    }

    @Override
    protected DetailedListAdapter createListAdapter(LayoutInflater inflater) {
        return new WeekAdapter(inflater);
    }

    protected class WeekAdapter extends DetailedListAdapter{
        public WeekAdapter(LayoutInflater inflater) {
            super(inflater);
        }

        protected final SimpleDateFormat wliTimeFormat = new SimpleDateFormat(getString(R.string.wliTimeFormat));
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.week_list_item, null, true);
            TextView time;
            TextView realTemp;
//            TextView summary;
            ImageView imageView;
            realTemp = (TextView) rowView.findViewById(R.id.wli_realTemp);
//            summary = (TextView) rowView.findViewById(R.id.wli_summary);
            time = (TextView) rowView.findViewById(R.id.wli_time);
            imageView = (ImageView) rowView.findViewById(R.id.wli_imageView);
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
            if (dataPoint != null) {
                realTemp.setText(getString(R.string.realTempLabelRangeShort, dataPoint.getTemperatureMin(), dataPoint.getTemperatureMax()));
//                if(summary != null) summary.setText(dataPoint.getSummary());
                time.setText(wliTimeFormat.format(dataPoint.getTime()));
            }   else{
                realTemp.setText("");
//                if(summary != null) summary.setText("");
                time.setText("");
            }
            int imageResourceId = IconManager.instance().getDrawable(dataPoint == null? null : dataPoint.getIconId(), IconManager.SIZE_SMALL);
            imageView.setImageResource(imageResourceId);
            return rowView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.week_list_item_expanded, null, true);
            TextView apparentTemp;
            TextView realTemp;
            TextView summary;
            TextView windSpeed;
            TextView pressure;
            ImageView imageView;
            apparentTemp = (TextView) rowView.findViewById(R.id.wliex_apparentTemp);
            realTemp = (TextView) rowView.findViewById(R.id.wliex_realTemp);
            windSpeed = (TextView) rowView.findViewById(R.id.wliex_windSpeed);
            pressure = (TextView) rowView.findViewById(R.id.wliex_pressure);
            summary = (TextView) rowView.findViewById(R.id.wliex_summary);
            imageView = (ImageView) rowView.findViewById(R.id.wliex_imageView);
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
            if (dataPoint != null) {
                apparentTemp.setText(getString(R.string.apparentTempLabelRange, dataPoint.getApparentTemperatureMin(), dataPoint.getApparentTemperatureMax()));
                realTemp.setText(getString(R.string.realTempLabelRange, dataPoint.getTemperatureMin(), dataPoint.getTemperatureMax()));
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
