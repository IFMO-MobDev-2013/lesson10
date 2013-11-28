package com.example.MaWeather;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Calendar;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private Context context;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private ArrayList<WeatherItem> items;

    public LazyAdapter(Activity a, Context context, ArrayList<WeatherItem> items) {
        this.activity = a;
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        String[] days = {"   Вс", "   Пон", "   Вт", "   Ср", "   Чт", "   Пт", "   Сб"};

        View vi = convertView;
        if(convertView == null)
            vi = inflater.inflate(R.layout.list_view, null);

        TextView date = (TextView) vi.findViewById(R.id.date);
        date.setText(days[(getDayOfWeek() + position + 1) % days.length]);

        TextView temperature = (TextView) vi.findViewById(R.id.temperature);
        temperature.setText(items.get(position).getTemperature() + "\u00B0 .. " + items.get(position).getHumidity() + "\u00B0   ");

        ImageView imageView = (ImageView) vi.findViewById(R.id.img);
        imageLoader.displayImage("http://yandex.st/weather/1.1.89/i/icons/48x48/" + items.get(position).getPictureType() + ".png", imageView);

        return vi;
    }

    private int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int res = calendar.get(Calendar.DAY_OF_WEEK);
        res--;
        //Log.d("memem", new Integer(res).toString());
        return res;
    }
}
