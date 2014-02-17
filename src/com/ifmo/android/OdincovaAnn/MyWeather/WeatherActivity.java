package com.ifmo.android.OdincovaAnn.MyWeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WeatherActivity extends Activity implements View.OnTouchListener {
    WeatherDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;
    Cursor cursor;
    WeatherCodes weatherCodes = new WeatherCodes();
    final int UPDATE = 1;
    final int SELECTED = 2;
    final int ADD = 3;
    final int EXIT_DIALOG = 1;
    String name;
    int WIDTH;
    int HEIGHT;
    FinishCityBroadcastReceiver finishBR;
    private ViewFlipper flipper = null;
    private float fromPosition;
    int MOVE_LENGTH;
    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);

        flipper = (ViewFlipper) findViewById(R.id.flipper);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{R.layout.first, R.layout.second};
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));

        finishBR = new FinishCityBroadcastReceiver();
        registerReceiver(finishBR, new IntentFilter(OneCityUpdateService.finishUpdateKey));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;
        MOVE_LENGTH = WIDTH * 2 / 3;
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            database.close();
            dataBaseHelper.close();
            Intent intent = new Intent(WeatherActivity.this, AddCityActivity.class);
            intent.putExtra("screen", "weather");
            startActivity(intent);
        } else {
            final ArrayList<String> cityName = new ArrayList<String>();
            final ArrayList<Integer> selected = new ArrayList<Integer>();
            while (cursor.moveToNext()) {
                cityName.add(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY)));
                selected.add(cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.SELECTED)));
            }
            cursor.close();
            int index = -1;
            for (int i = 0; i < selected.size(); i++) {
                if (selected.get(i) == 1) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                database.close();
                dataBaseHelper.close();
                Intent intent = new Intent(WeatherActivity.this, CityActivity.class);
                startActivity(intent);
            } else {
                name = cityName.get(index);
                showWeather(name);

            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(finishBR);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(finishBR, new IntentFilter(OneCityUpdateService.finishUpdateKey));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, UPDATE, 0, R.string.update_weather);
        menu.add(0, SELECTED, 0, R.string.selected_city);
        menu.add(0, ADD, 0, R.string.add_new_city);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case UPDATE:
                intent = new Intent(this, OneCityUpdateService.class);
                intent.putExtra("nameCity", name);
                intent.putExtra("screen", "weather");
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.start_update_weather, Toast.LENGTH_LONG);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
                startService(intent);
                return true;
            case SELECTED:
                intent = new Intent(WeatherActivity.this, CityActivity.class);
                startActivity(intent);
                finish();
                return true;
            case ADD:
                intent = new Intent(WeatherActivity.this, AddCityActivity.class);
                intent.putExtra("screen", "weather");
                startActivity(intent);
                finish();
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public class FinishCityBroadcastReceiver extends BroadcastReceiver {
        int result;

        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getExtras().getInt("result");
            if (result == 1) {
                String tmp = intent.getExtras().getString("name");
                showWeather(tmp);
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.finish, Toast.LENGTH_SHORT);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
            } else {
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.download_error, Toast.LENGTH_SHORT);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
            }
        }
    }

    public void showWeather(String city) {
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        TextView textViewTitle = (TextView) findViewById(R.id.textViewCity);
        textViewTitle.setText(city.replace("_", " "));
        cursor = database.query(city, null, null, null, null, null, null);
        cursor.moveToPosition(0);
        int code = cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.WEATHER_CODE));
        int i = weatherCodes.getIndex(code);
        ImageView imageViewWeather = (ImageView) findViewById(R.id.imageViewWeather);
        if (WIDTH < HEIGHT) {
            imageViewWeather.setMinimumHeight((int) (WIDTH * 0.4));
            imageViewWeather.setMinimumWidth((int) (WIDTH * 0.4));
        } else {
            imageViewWeather.setMinimumHeight((int) (HEIGHT * 0.4));
            imageViewWeather.setMinimumWidth((int) (HEIGHT * 0.4));
        }
        int day = cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.NIGHT));
        InputStream myInput = null;
        try {
            if (day == 0) {
                myInput = getAssets().open(WeatherCodes.image[i] + "day" + ".png");
            } else {
                myInput = getAssets().open(WeatherCodes.image[i] + "night" + ".png");
            }
        } catch (IOException e) {
        }
        TextView textViewTemp = (TextView) findViewById(R.id.textViewTemp);
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.TEMP))) > 0) {
            textViewTemp.setText("+" + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.TEMP)) + "\u00B0" + "C");
        } else {
            textViewTemp.setText(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.TEMP)) + "\u00B0" + "C");
        }
        imageViewWeather.setImageDrawable(Drawable.createFromStream(myInput, null));
        TextView textViewDiscr = (TextView) findViewById(R.id.textViewDescr);
        textViewDiscr.setText(WeatherCodes.description[i]);
        TextView textViewWind = (TextView) findViewById(R.id.textViewWind);
        textViewWind.setText("Ветер: " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.WIND_DIR)) +
                ", " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.WIND_SPEED)) + " м/с");
        TextView textViewHum = (TextView) findViewById(R.id.textViewHum);
        textViewHum.setText("Влажность: " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.HUMIDITY)) + "%");
        TextView textViewRain = (TextView) findViewById(R.id.textViewRain);
        textViewRain.setText("Осадки: " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.PRECIP_MM)) + " мм");
        TextView textViewPress = (TextView) findViewById(R.id.textViewPress);
        textViewPress.setText("Давление: " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.PRESSURE)) +
                " мм.рт.ст");
        TextView textViewCloud = (TextView) findViewById(R.id.textViewCloud);
        textViewCloud.setText("Облачность: " + cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CLOUD_COVER))
                + "%");
        ArrayList<DaysWeather> days = new ArrayList<DaysWeather>();
        while (cursor.moveToNext()) {
            DaysWeather daysWeather = new DaysWeather();
            daysWeather.setDate(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.DATE)));
            daysWeather.setNight(cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.NIGHT)));
            daysWeather.setPrecip(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.PRECIP_MM)));
            daysWeather.setTempMax(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.TEMP_MAX)));
            daysWeather.setTempMin(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.TEMP_MIN)));
            daysWeather.setWeatherCode(cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.WEATHER_CODE)));
            daysWeather.setWindDir(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.WIND_DIR)));
            daysWeather.setWindSpeed(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.WIND_SPEED)));
            days.add(daysWeather);
        }
        cursor.close();
        database.close();
        dataBaseHelper.close();
        lv = (ListView) this.findViewById(R.id.listView);
        lv.setAdapter(new MyAdapter(this, days));
    }

    public void onBackPressed() {
        showDialog(EXIT_DIALOG);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == EXIT_DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.exit);
            adb.setMessage(R.string.confirm_exiit);
            adb.setPositiveButton(R.string.yes, myClickListener);
            adb.setNegativeButton(R.string.no, myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float toPosition = event.getX();

                if ((fromPosition - MOVE_LENGTH) > toPosition) {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_out));
                    flipper.showNext();
                } else if ((fromPosition + MOVE_LENGTH) < toPosition) {
                    fromPosition = toPosition;
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
                    flipper.showPrevious();
                }
            default:
                break;
        }
        return true;
    }

    public class MyAdapter extends BaseAdapter {

        ArrayList<DaysWeather> data = new ArrayList<DaysWeather>();
        Context context;

        public MyAdapter(Context context, ArrayList<DaysWeather> arr) {
            if (arr != null) {
                data = arr;
            }
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int num) {
            return data.get(num);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int i, View someView, ViewGroup arg2) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (someView == null) {
                someView = inflater.inflate(R.layout.item, arg2, false);
            }
            TextView dataTextView = (TextView) someView.findViewById(R.id.mainTextView);
            TextView descrTextView = (TextView) someView.findViewById(R.id.descrTextView);
            TextView dayTextView = (TextView) someView.findViewById(R.id.textViewDay);
            TextView nightTextView = (TextView) someView.findViewById(R.id.textViewNight);
            TextView otherTextView = (TextView) someView.findViewById(R.id.textViewOther);
            ImageView image = (ImageView) someView.findViewById(R.id.imageView);

            dataTextView.setText(data.get(i).getDate());
            int code = data.get(i).getWeatherCode();
            int iter = weatherCodes.getIndex(code);
            InputStream myInput = null;
            try {
                myInput = getAssets().open(WeatherCodes.image[iter] + "day" + ".png");
            } catch (IOException e) {
            }

            image.setImageDrawable(Drawable.createFromStream(myInput, null));
            if (Integer.parseInt(data.get(i).getTempMax()) > 0) {
                dayTextView.setText("день: " + "+" + data.get(i).getTempMax() + "\u00B0" + "C");
            } else {
                dayTextView.setText("день: " + data.get(i).getTempMax() + "\u00B0" + "C");
            }

            if (Integer.parseInt(data.get(i).getTempMin()) > 0) {
                nightTextView.setText("ночь: " + "+" + data.get(i).getTempMin() + "\u00B0" + "C");
            } else {
                nightTextView.setText("ночь: " + data.get(i).getTempMin() + "\u00B0" + "C");
            }

            descrTextView.setText(WeatherCodes.description[iter]);
            otherTextView.setText("ветер: " + data.get(i).getWindDir() + ", " + data.get(i).getWindSpeed()
                    + "м/с, осадки: " + data.get(i).getPrecip() + " мм");
            return someView;
        }

    }
}
