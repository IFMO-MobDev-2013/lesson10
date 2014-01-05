package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


public class MyArrayAdapter extends ArrayAdapter<City> {

    private final Context context;
    private final List<City> values;
    private DataBase db;

    public MyArrayAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);
        this.context = context;
        this.values = objects;
        db = new DataBase(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.row, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.city_name);
        textView.setText(values.get(position).getCity());
        ImageButton deleteButton = (ImageButton) row.findViewById(R.id.del);
        deleteButton.setFocusable(false);
        deleteButton.setTag(position);

        deleteButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer index = (Integer) v.getTag();
                        City c = values.get(index);
                        db.deleteCity(c.getId());
                        values.remove(index.intValue());
                        notifyDataSetChanged();
                    }
                }
        );
        return row;
    }
}
