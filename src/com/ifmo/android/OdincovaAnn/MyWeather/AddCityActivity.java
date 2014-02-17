package com.ifmo.android.OdincovaAnn.MyWeather;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCityActivity extends Activity {
    EditText nameEdittext;
    Button addButton;
    String screen;
    FinishCityBroadcastReceiver finishBR;
    String name;

    WeatherDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        nameEdittext = (EditText) findViewById(R.id.editTextName);
        addButton = (Button) findViewById(R.id.buttonSave);
        screen = getIntent().getExtras().getString("screen");
        finishBR = new FinishCityBroadcastReceiver();
        registerReceiver(finishBR, new IntentFilter(OneCityUpdateService.finishCityKey));
    }

    public void onClickSave(View view) {
        name = nameEdittext.getText().toString();
        while (!"".equals(name) && name.charAt(0) == ' ') {
            name = name.substring(1);
        }

        if ("".equals(name)) {
            Toast myToast = Toast.makeText(getApplicationContext(), R.string.error_null, Toast.LENGTH_SHORT);
            myToast.setGravity(Gravity.CENTER, 0, 0);
            myToast.show();
        } else {
            name = name.replace(" ", "_");
            if (!repetition(name)) {
                Intent newIntent = new Intent(this, OneCityUpdateService.class);
                newIntent.putExtra("nameCity", name);
                newIntent.putExtra("screen", "add");
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.saving_city, Toast.LENGTH_SHORT);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
                startService(newIntent);
            }
        }
    }

    public void onClickCancel(View view) {
        if ("city".equals(screen)) {
            Intent intent = new Intent(AddCityActivity.this, CityActivity.class);
            startActivity(intent);
            finish();
        } else {
            dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
            database = dataBaseHelper.getWritableDatabase();
            Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
            if (cursor.getCount() == 0) {
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.no_city, Toast.LENGTH_LONG);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
            } else {
                Intent intent = new Intent(AddCityActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public boolean repetition(String name) {
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getWritableDatabase();
        Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
        boolean repeat = false;

        while (cursor.moveToNext()) {

            if (name.equals(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY)))) {
                repeat = true;
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.repeat_name, Toast.LENGTH_SHORT);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
                break;
            }
        }
        database.close();
        dataBaseHelper.close();
        return repeat;
    }

    public class FinishCityBroadcastReceiver extends BroadcastReceiver {
        WeatherDataBaseHelper dataBaseHelper;
        SQLiteDatabase database;
        int result;

        @Override
        public void onReceive(Context context, Intent intent) {
            dataBaseHelper = new WeatherDataBaseHelper(context);
            database = dataBaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            result = intent.getExtras().getInt("result");
            if (result == 1) {
                values.put(WeatherDataBaseHelper.CITY, name);
                Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper.SELECTED)) == 1) {
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(WeatherDataBaseHelper.CITY,
                                cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY)));
                        updateValues.put(WeatherDataBaseHelper.SELECTED, 0);
                        database.update(WeatherDataBaseHelper.TABLE_NAME, updateValues, WeatherDataBaseHelper._ID + "=" +
                                cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper._ID)), null);
                    }
                }
                cursor.close();
                values.put(WeatherDataBaseHelper.SELECTED, 1);
                database.insert(WeatherDataBaseHelper.TABLE_NAME, null, values);
                database.close();
                dataBaseHelper.close();
                Intent newIntent = new Intent(AddCityActivity.this, WeatherActivity.class);
                startActivity(newIntent);
                finish();
            } else {
                if (result == 3) {
                    Toast myToast = Toast.makeText(getApplicationContext(), R.string.city_error, Toast.LENGTH_LONG);
                    myToast.setGravity(Gravity.CENTER, 0, 0);
                    myToast.show();
                } else {
                    Toast myToast = Toast.makeText(getApplicationContext(), R.string.download_error, Toast.LENGTH_SHORT);
                    myToast.setGravity(Gravity.CENTER, 0, 0);
                    myToast.show();
                }
            }
        }
    }

    public void onBackPressed() {
        onClickCancel((Button) findViewById(R.id.cancel));
    }
}
