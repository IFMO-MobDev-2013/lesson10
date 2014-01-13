package ru.skipor.weather;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import ru.skipor.RssReader.R;
import ru.skipor.Utils.InternalSrorageUtils;

public class WeatherActivity extends ListActivity implements AppResultReceiver.Receiver {


    public static final String EXTRA_CITY_NAME = "ru.skipor.weather.WeatherActivity city name";
    public static final String UPDATE_MESSAGE = "Weather is up to date";


    public static final String TAG = "WeatherActivity";
    private WeatherDatabaseHelper myDatabaseHelper;
    private String cityName;
    private Cursor dayCursor;
    private View currentConditionView;
    //    private Cursor currentConditionCursor;
    private AppResultReceiver appResultReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cityName = intent.getStringExtra(EXTRA_CITY_NAME);
        setTitle(cityName);
        appResultReceiver = new AppResultReceiver(new Handler());
        appResultReceiver.setReceiver(this);


        setContentView(R.layout.weather_activity);

        currentConditionView = findViewById(R.id.current_cond_layout);
        myDatabaseHelper = WeatherDatabaseHelper.getInstance(this);
        myDatabaseHelper.open();
//        fill data










        dayCursor = myDatabaseHelper.fetchDayForecasts(cityName);
        DayForecastCursorAdapter adapter = new DayForecastCursorAdapter(this, dayCursor);
        setListAdapter(adapter);


        if (dayCursor == null || dayCursor.getCount() == 0) { // got request, while UpdateService city updating
            updateFeed();
        }

        fillCurrentCondition();

//        fill data end


    }

    private void fillCurrentCondition() {
        Cursor currentConditionCursor = null;
        try {

            currentConditionCursor = myDatabaseHelper.fetchCurrentCondition(cityName);
            if (currentConditionCursor == null || currentConditionCursor.getCount() == 0) {
                return;
            }
            currentConditionCursor.moveToFirst();
            ((TextView) currentConditionView.findViewById(R.id.current_cond_weather_desc_text_view)).
                    setText(currentConditionCursor.getString(currentConditionCursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_WEATHER_DESCRIPTION)));
            ((TextView) currentConditionView.findViewById(R.id.current_cond_temp_text_view)).
                    setText(currentConditionCursor.getString(currentConditionCursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_TEMP)) + "°");
//
            String iconName = currentConditionCursor.getString(currentConditionCursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_ICON_NAME));
//
            Bitmap icon = InternalSrorageUtils.loadBitmap(this, iconName);
//
            int pictureBackgroundColor = InternalSrorageUtils.getBackgroundColor(icon);
//
            currentConditionView.setBackgroundColor(pictureBackgroundColor);
//
//
            ((ImageView) currentConditionView.findViewById(R.id.current_cond_icon_image_view)).setImageBitmap(icon);

        }finally {
            if (currentConditionCursor != null) {
                currentConditionCursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.weather_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateFeed();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    private void refillData() {
        dayCursor = myDatabaseHelper.fetchDayForecasts(cityName);
        Cursor oldCursor = ((DayForecastCursorAdapter) getListAdapter()).swapCursor(dayCursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
        fillCurrentCondition();
    }

    private void updateFeed() {

        final Intent intent = new Intent(WeatherUpdateService.ACTION_UPDATE_ONE, null, this, WeatherUpdateService.class);
        intent.putExtra(WeatherUpdateService.EXTRA_CITY_NAME, cityName);
        intent.putExtra(WeatherUpdateService.EXTRA_INFORM_ABOUT_UPDATE, true);
        intent.putExtra(WeatherUpdateService.EXTRA_RECEIVER, appResultReceiver);
        startService(intent);
    }


    @Override
    protected void onResume() {
//        refillData(); //refill only when result received
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {

//        closeCursor();
        myDatabaseHelper.close();
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        if (resultCode == WeatherUpdateService.RESULT_CODE_UPTODATE) {
            refillData();
            Toast.makeText(this, UPDATE_MESSAGE, Toast.LENGTH_LONG).show();
        }

    }

    private void closeCursor() {
        Cursor cursor = ((DayForecastCursorAdapter) getListAdapter()).getCursor();
        if (cursor != null) {
            cursor.close();
        }
    }


    private static class DayForecastCursorAdapter extends CursorAdapter {


        public DayForecastCursorAdapter(Context context, int layout, Cursor c, int flags) {
            super(context, c, flags);
        }

        public DayForecastCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.day_row, null);
        }


        private Map<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>(); // caches icons in RAM, have only one bitmap for one image


        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view.findViewById(R.id.date_text_view)).
                    setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_DAY_OF_WEEK)));
            ((TextView) view.findViewById(R.id.maxt_text_view)).
                    setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_TEMP_MAX)));
            ((TextView) view.findViewById(R.id.mint_text_view)).
                    setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_TEMP_MIN)));

            String iconName = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_ICON_NAME));

            Bitmap icon = bitmapMap.get(iconName);
            if (icon == null) {
                Bitmap internalBitmap = InternalSrorageUtils.loadBitmap(context, iconName);
                bitmapMap.put(iconName, internalBitmap);
                icon = internalBitmap;
            }

            int pictureBackgroundColor = InternalSrorageUtils.getBackgroundColor(icon);

            view.setBackgroundColor(pictureBackgroundColor);


            ((ImageView) view.findViewById(R.id.icon_image_view)).setImageBitmap(icon);


        }
    }

}

