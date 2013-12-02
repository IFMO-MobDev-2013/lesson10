package com.example.weather;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<TownShort> {
    Context context;

    public MyAdapter(Context _context, ArrayList<TownShort> value) {
        super(_context, R.layout.list_item, value);
        context = _context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TownShort townShort = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setTextColor(Color.GREEN);
        textView.setTextSize(20);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        textView.setText(townShort.name + " " + ((townShort.temperature > 0) ? "+" : "") + townShort.temperature);
        if (townShort.picture == null) return view;
        if (townShort.picture.charAt(0) == 'n') {
            townShort.picture = townShort.picture.substring(1);
        }
        for (int i = townShort.picture.length() - 1; i >= 0; i--) {
            if (townShort.picture.charAt(i) != ' ' && townShort.picture.charAt(i) != '\n') {
                townShort.picture = townShort.picture.substring(0, i + 1);
                break;
            }
        }
        imageView.setImageResource(context.getResources().getIdentifier("p" + townShort.picture, "drawable", context.getPackageName()));
        Log.d("picture", "q" + townShort.picture + "q");
        return view;
    }
}
