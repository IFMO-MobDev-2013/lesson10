package com.example.MeWeather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ViruZ on 09.01.14.
 */
public class ForecastAdapter extends BaseAdapter {
    private DBWeather dbWeather;
    private Context context;
    private DBCityInform dbCityInform;
    public ForecastAdapter(Context context, DBWeather dbWeather1) {
        this.context = context;
        this.dbWeather = dbWeather1;
        this.dbCityInform = dbWeather1.getSelected();
    }
    public void refresh() {
        dbCityInform = dbWeather.getSelected();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dbCityInform.getForecast().getDays();
    }

    @Override
    public Object getItem(int position) {
        return dbCityInform.getForecast().getDay(position);
    }

    @Override
    public long getItemId(int position) {
        return dbCityInform.getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.forecastadapter, parent, false);
        TextView date = (TextView) view.findViewById(R.id.Date);
        TextView weather = (TextView) view.findViewById(R.id.Condition);
        ImageView weatherPicture = (ImageView) view.findViewById(R.id.ConditionImage);
        TextView maxTemp = (TextView) view.findViewById(R.id.MaxTemp);
        TextView minTemp = (TextView) view.findViewById(R.id.MinTemp);

        Forecast forecast = dbCityInform.getForecast();


        setWeek(date, forecast, position);
        maxTemp.setText(Integer.toString(forecast.getDay(position).getMaxTemp()) + "°C");
        minTemp.setText(Integer.toString(forecast.getDay(position).getMinTemp()) + "°C");

        PictureClass pic = PictureManager.getWeather(forecast.getDay(position).getCode());
        weatherPicture.setImageResource(pic.dayPict);
        weather.setText(pic.subj);

        return view;
    }
    private void setWeek(TextView date, Forecast forecast, int position) {
        String d = forecast.getDay(position).getDate().toString();
        switch (d.substring(0, 3)) {
            case "Sun":
                date.setText(context.getString(R.string.sun));
                break;
            case "Mon":
                date.setText(context.getString(R.string.mon));
                break;
            case "Tue":
                date.setText(context.getString(R.string.tue));
                break;
            case "Wed":
                date.setText(context.getString(R.string.wed));
                break;
            case "Thu":
                date.setText(context.getString(R.string.thu));
                break;
            case "Fri":
                date.setText(context.getString(R.string.fri));
                break;
            case "Sat":
                date.setText(context.getString(R.string.sat));
                break;
        }
    }

}
