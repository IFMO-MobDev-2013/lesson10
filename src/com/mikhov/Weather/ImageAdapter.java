package com.mikhov.Weather;


import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ImageAdapter extends SimpleCursorAdapter {
    private int layout;

    public ImageAdapter(Context in_context, int in_layout, Cursor in_cursor, String[] in_from, int[] in_to) {
        super(in_context, in_layout, in_cursor, in_from, in_to);
        layout = in_layout;
    }

    @Override
     public void bindView(View view, Context in_context, Cursor in_cursor) {
        byte[] dbImage = in_cursor.getBlob(in_cursor.getColumnIndex(Database.COL_IMG1));
        String dbTown = in_cursor.getString(in_cursor.getColumnIndex(Database.COL_TOWN));
        String dbTemperature = in_cursor.getString(in_cursor.getColumnIndex(Database.COL_TEMPERATURE1));

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView town = (TextView) view.findViewById(R.id.town);
        TextView temperature = (TextView) view.findViewById(R.id.temperature);

        town.setText(dbTown);
        temperature.setText(dbTemperature);
        if (dbImage != null) {
            image.setImageBitmap(BitmapFactory.decodeByteArray(dbImage, 0, dbImage.length));
        }
    }

    @Override
    public View newView(Context in_context, Cursor in_cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) in_context.getSystemService(in_context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);
        return view;
    }
}