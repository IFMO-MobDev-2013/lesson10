package com.example.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: slavian
* Date: 26.11.13
* Time: 22:33
* To change this template use File | Settings | File Templates.
*/
public class ForecastAdapter extends ArrayAdapter<Forecast> {

    private static final String TAG = "FUCKENFUCK::ForecastAgapter ";

    public ForecastAdapter(Context context, int resourceId){
        super(context, resourceId);
        //this.resourceId = resourceId;
    }

    private List<Forecast> items;
    private int resourceId = R.layout.forecast_list_item;

    public ForecastAdapter(Context context, int resourceID, List<Forecast> items)
    {
        super(context, resourceID, items);
        this.items = items;
        //this.resourceId = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if(v == null)
        {
            //LayoutInflater vi;
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(resourceId, null);
        }

        Forecast f = items.get(position);

        if(f!= null)
        {
            TextView tv = (TextView)v.findViewById(R.id.textView);
            TextView tv1 = (TextView)v.findViewById(R.id.textView1);
            TextView tv2 = (TextView)v.findViewById(R.id.textView2);
            TextView tv3 = (TextView)v.findViewById(R.id.textView3);
            //TextView tv4 = (TextView)v.findViewById(R.id.textView4);
            ImageView img = (ImageView)v.findViewById(R.id.imageView);

            try
            {
                tv.setText(f.tempMin + Main.KEY_GRAD + " .. " + f.tempMax+Main.KEY_GRAD);
                tv1.setText(f.date);
                tv2.setText(f.descr);
                tv3.setText(f.wind+ " km/h");   // todo make m/s
                //tv4.setText(f.humidity + " %");
                img.setImageResource(f.image);
            }
            catch (Exception e)
            {
                Log.w(TAG, "failed creating a view for forecast " + e.getMessage());
            }
        }

        return v;
    }
}
