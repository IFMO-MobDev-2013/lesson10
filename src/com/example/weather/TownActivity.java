package com.example.weather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;


public class TownActivity extends Activity {

    class MyAdapter extends ArrayAdapter<Day> {
        private Context context;

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Day> items) {
            super(context, textViewResourceId, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.dayitem, null);
            TextView textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);
            TextView textViewLow = (TextView) rowView.findViewById(R.id.textViewLow);
            TextView textViewHigh = (TextView) rowView.findViewById(R.id.textViewHigh);
            TextView textViewClouds = (TextView) rowView.findViewById(R.id.textViewClouds);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);

            Day item = getItem(position);

            if (item == null) {
                textViewDate.setText(R.string.ErrorChannel);
                imageView.setImageResource(R.drawable.w3200);
                return rowView;
            }

            Time time = new Time();
            time.set(Long.parseLong(item.date));
            textViewDate.setText(time.format3339(true));
            textViewLow.setText(item.low_temperature);
            textViewHigh.setText(item.high_temperature);
            textViewClouds.setText(item.clouds);

            imageView.setImageResource(getResources().getIdentifier("w" + item.image, "drawable", getPackageName()));

            return rowView;
        }
    }

    public AdapterView.OnItemClickListener goToDay = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            Intent intent = new Intent();

            Day item = array.get(position);
            intent.putExtra(Day.DATE, item.date);
            intent.putExtra(Day.HUMIDITY, item.humidity);
            intent.putExtra(Day.PRESSURE, item.pressure);
            intent.putExtra(Day.WIND_DIRECTION, item.wind_direction);
            intent.putExtra(Day.WIND_SPEED, item.wind_speed);
            intent.putExtra(Day.LOW_TEMPERATURE, item.low_temperature);
            intent.putExtra(Day.HIGH_TEMPERATURE, item.high_temperature);
            intent.putExtra(Day.CLOUDS, item.clouds);
            intent.putExtra(Day.IMAGE, item.image);

            intent.putExtra(Town.NAME, townName);

            intent.setClass(getApplicationContext(), DayActivity.class);

            startActivity(intent);
        }
    };

    public View.OnClickListener updateTown = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), UpdatingService.class);
            intent.putExtra(UpdatingService.ALL_ID, false);
            intent.putExtra(Town._ID, townId);
            intent.putExtra(Town.WOEID, townWOEID);
            intent.putExtra(Town.NAME, townName);

            startService(intent);
        }
    };

    ArrayList<Day> array;
    ListView listView;
    MyAdapter adapter;
    int townId;
    String townWOEID;
    String townName;

    public void makingList() {

        TextView textViewTownName = (TextView) findViewById(R.id.channelName);
        textViewTownName.setText(getIntent().getStringExtra(Town.NAME));

        townId = getIntent().getIntExtra(Town._ID, 0);
        townName = getIntent().getStringExtra(Town.NAME);
        townWOEID = getIntent().getStringExtra(Town.WOEID);

        MyDataBaseTownHelper myDataBaseTownHelper = new MyDataBaseTownHelper(getApplicationContext(), townWOEID);
        SQLiteDatabase sqLiteDatabase = myDataBaseTownHelper.getWritableDatabase();

        boolean sqb = (sqLiteDatabase == null);
        System.out.println("sq " + sqb) ;

        Cursor cursor = sqLiteDatabase.query(MyDataBaseTownHelper.DATABASE_NAME_NO_ID + townWOEID, null, null,
                null, null, null, null);

        int date_column = cursor.getColumnIndex(MyDataBaseTownHelper.DATE);
        int humidity_column = cursor.getColumnIndex(MyDataBaseTownHelper.HUMIDITY);
        int pressure_column = cursor.getColumnIndex(MyDataBaseTownHelper.PRESSURE);
        int wind_direction_column = cursor.getColumnIndex(MyDataBaseTownHelper.WIND_DIRECTION);
        int wind_speed_column = cursor.getColumnIndex(MyDataBaseTownHelper.WIND_SPEED);
        int low_temperature_column = cursor.getColumnIndex(MyDataBaseTownHelper.LOW_TEMPERATURE);
        int high_temperature_column = cursor.getColumnIndex(MyDataBaseTownHelper.HIGH_TEMPERATURE);
        int clouds_column = cursor.getColumnIndex(MyDataBaseTownHelper.CLOUDS);
        int image_column = cursor.getColumnIndex(MyDataBaseTownHelper.IMAGE);

        array = new ArrayList<Day>();
        Time now = new Time();
        now.setToNow();
        System.out.println(" now = " + now.toString());
        while (cursor.moveToNext()) {

            Time current = new Time();
            current.set(Long.parseLong(cursor.getString(date_column)));
            System.out.println(" checking date: " + current.toString());
            if (now.after(current) && (now.yearDay > current.yearDay || now.year > current.year))
                continue;
            System.out.println(" nice day " + current.toString());
            array.add(new Day(cursor.getString(date_column), cursor.getString(humidity_column),
                    cursor.getString(pressure_column), cursor.getString(wind_direction_column), cursor.getString(wind_speed_column),
                    cursor.getString(low_temperature_column), cursor.getString(high_temperature_column), cursor.getString(clouds_column),
                    cursor.getInt(image_column)));
        }

        cursor.close();
        sqLiteDatabase.close();
        myDataBaseTownHelper.close();

        adapter = new MyAdapter(getApplicationContext(), R.layout.dayitem, array);   //TODO   norm?

        listView = (ListView) findViewById(R.id.listViewTown);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(goToDay);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdatingService.UPDATING_ACTION + "_" + townName);
        registerReceiver(mMessageReceiver, intentFilter);
        makingList();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdatingService.UPDATING_ACTION + "_" + townName);
        registerReceiver(mMessageReceiver, intentFilter);
        makingList();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            makingList();
            Toast.makeText(context, intent.getStringExtra(UpdatingService.RESULT), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.town);

        Button button = (Button) findViewById(R.id.buttonUpdateThisChannel);
        button.setOnClickListener(updateTown);

        makingList();
    }
}
