package com.example.lesson10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 01.03.14
 * Time: 1:02
 * To change this template use File | Settings | File Templates.
 */
public class MyAdapter extends ArrayAdapter<ItemWeather> {
    private final Context context;
    private final ArrayList<ItemWeather> values;

    public MyAdapter(Context context, ArrayList<ItemWeather> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_weather, parent, false);

        TextView date_temp = (TextView)rowView.findViewById(R.id.weather_temp);
        TextView wind = (TextView)rowView.findViewById(R.id.wind);
        ImageView imageView = (ImageView)rowView.findViewById(R.id.icon);
        TextView day = (TextView)rowView.findViewById(R.id.day);

        //webView.loadData(values.get(position).icon,"text/html; charset=UTF-8",null);
        Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (simpleDateFormat.format(date).equals(values.get(position).date))
            day.setText(MyActivity.instance.getString(R.string.today));
        date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 1);
        if (simpleDateFormat.format(date).equals(values.get(position).date))
            day.setText(MyActivity.instance.getString(R.string.tomorrow));
        date = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2);
        if (simpleDateFormat.format(date).equals(values.get(position).date))
            day.setText(MyActivity.instance.getString(R.string.day_after_tomorrow));
        imageView.setImageBitmap(values.get(position).icon);
        wind.setText(MyActivity.instance.getString(R.string.windtext) + values.get(position).wind + MyActivity.instance.getString(R.string.windveloc));
        date_temp.setText(values.get(position).desc + "   (" + values.get(position).minT + ")-(" + values.get(position).maxT + ")\t");

        return rowView;
    }
}
