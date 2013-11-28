package ru.georgeee.android.singingintherain;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.georgeee.android.singingintherain.model.Forecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
public class HourlyFragment extends ForecastFragment {
    ExpandableListView hourList;
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
            hourList = (ExpandableListView) rootView.findViewById(R.id.hourList);
            listAdapter = new HourlyListAdapter(inflater);
            hourList.setAdapter(listAdapter);
            onUpdateViewImpl();
        }
    }

    protected class HourlyListAdapter extends BaseExpandableListAdapter{
        protected ArrayList<Forecast.DataPoint> dataPoints;

        protected LayoutInflater inflator;
        public HourlyListAdapter(LayoutInflater inflator) {
            this.inflator = inflator;
            dataPoints = new ArrayList<Forecast.DataPoint>();
        }

        @Override
        public int getGroupCount() {
            return dataPoints.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return dataPoints.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
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

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View rowView = inflator.inflate(R.layout.hourly_list_item_expanded, null, true);
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
            Forecast.DataPoint dataPoint = dataPoints.get(groupPosition);
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

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void clear() {
            dataPoints.clear();
        }

        public void addAll(List<Forecast.DataPoint> dataPoints) {
            this.dataPoints.addAll(dataPoints);
        }
    }
}