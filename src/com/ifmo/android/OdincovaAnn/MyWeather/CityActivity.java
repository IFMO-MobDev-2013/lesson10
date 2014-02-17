package com.ifmo.android.OdincovaAnn.MyWeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class CityActivity extends Activity {
    TextView textView;
    WeatherDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;
    final int MENU_DELETE = 11;
    final int DELETE_DIALOG = 10;
    final int ADD = 1;
    final int UPDATE = 2;
    String city;
    FinishBroadcastReceiver finishBR;
    StartBroadcastReceiver startBR;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);
        textView = (TextView) findViewById(R.id.title);
        finishBR = new FinishBroadcastReceiver();
        registerReceiver(finishBR, new IntentFilter(AllCityUpdateService.finishKey));
        startBR = new StartBroadcastReceiver();
        registerReceiver(startBR, new IntentFilter(AllCityUpdateService.startKey));
        showCitiesList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_DELETE, 0, R.string.menu_delete);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long position = info.id;
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToPosition((int) position);
        city = cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY));
        int id = cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper._ID));
        cursor.close();
        database.close();
        dataBaseHelper.close();
        switch (item.getItemId()) {
            case MENU_DELETE: {
                showDialog(DELETE_DIALOG);
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DELETE_DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.deleting);
            adb.setMessage(R.string.message_delete_dialog);
            adb.setPositiveButton(R.string.delete, myClickListener);
            adb.setNegativeButton(R.string.cancel, myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
                    database = dataBaseHelper.getWritableDatabase();
                    database.execSQL("DELETE FROM " + WeatherDataBaseHelper.TABLE_NAME + " WHERE " +
                            WeatherDataBaseHelper.CITY + " = '" + city + "'");
                    database.execSQL("DROP TABLE IF EXISTS " + "table" + city);
                    database.close();
                    dataBaseHelper.close();
                    showCitiesList();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void showCitiesList() {
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            textView.setText(R.string.no_cities);
        }
        final ArrayList<String> cities = new ArrayList<String>();
        while (cursor.moveToNext()) {
            cities.add(cursor.getString(cursor.getColumnIndex(WeatherDataBaseHelper.CITY)).replace("_", " "));
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        ListView listView = (ListView) findViewById(R.id.listViewCity);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
                cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
                cursor.moveToPosition(i);
                ContentValues values = new ContentValues();
                values.put(WeatherDataBaseHelper.CITY, cities.get(i).replace(" ", "_"));
                values.put(WeatherDataBaseHelper.SELECTED, 1);
                database.update(WeatherDataBaseHelper.TABLE_NAME, values, WeatherDataBaseHelper._ID + "=" +
                        cursor.getInt(cursor.getColumnIndex(WeatherDataBaseHelper._ID)), null);
                cursor.close();
                Intent intent = new Intent(CityActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onBackPressed() {
        dataBaseHelper = new WeatherDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getWritableDatabase();
        Cursor cursor = database.query(WeatherDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            Toast myToast = Toast.makeText(getApplicationContext(), R.string.no_city, Toast.LENGTH_LONG);
            myToast.setGravity(Gravity.CENTER, 0, 0);
            myToast.show();
        } else {
            Intent intent = new Intent(CityActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD, 0, R.string.add_new_city);
        menu.add(0, UPDATE, 0, R.string.up_date_all);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case ADD:
                intent = new Intent(CityActivity.this, AddCityActivity.class);
                intent.putExtra("screen", "city");
                startActivity(intent);
                finish();
                return true;
            case UPDATE:
                intent = new Intent(this, AllCityUpdateService.class);
                startService(intent);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public class FinishBroadcastReceiver extends BroadcastReceiver {
        int result;

        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getExtras().getInt("result");
            if (result == 1) {
                Toast myToast = Toast.makeText(getApplicationContext(), R.string.finish, Toast.LENGTH_SHORT);
                myToast.setGravity(Gravity.CENTER, 0, 0);
                myToast.show();
            } else {
                if (result == 4) {
                    Toast myToast = Toast.makeText(getApplicationContext(), R.string.error_no_city, Toast.LENGTH_SHORT);
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

    public class StartBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast myToast = Toast.makeText(getApplicationContext(), R.string.start_update_weather, Toast.LENGTH_SHORT);
            myToast.setGravity(Gravity.CENTER, 0, 0);
            myToast.show();
        }
    }
}
