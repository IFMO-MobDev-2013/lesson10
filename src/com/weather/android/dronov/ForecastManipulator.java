package com.weather.android.dronov;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.weather.android.dronov.City.AddCityActivity;
import com.weather.android.dronov.database.WeatherDB;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 21.11.13
 * Time: 2:33
 * To change this template use File | Settings | File Templates.
 */
public class ForecastManipulator extends Activity {
    public static final int DELETE = 0;

    private WeatherDB weatherDB = null;
    private ListView listView = null;
    private SimpleCursorAdapter adapter = null;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_manipulator);

        weatherDB = new WeatherDB(this);
        weatherDB.open();
        cursor = weatherDB.getAllDataCursor();
        startManagingCursor(cursor);

        String[] from = new String[] {
                WeatherDB.COLUMN_CITY,
                WeatherDB.COLUMN_COUNTRY
        };

        int[] to = new int[] {
                R.id.city,
                R.id.country
        };

        adapter = new SimpleCursorAdapter(this, R.layout.adapter_forecast_manipulator, cursor, from, to);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        Button button = (Button) findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForecastManipulator.this, AddCityActivity.class);
                startActivity(intent);
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent backIntent = new Intent(ForecastManipulator.this, MainActivity.class);
                backIntent.putExtra(MainActivity.ITEM_INDEX, i);
                startActivity(backIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForecastManipulator.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE, 0, getString(R.string.DeleteCity));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            weatherDB.deleteCity(adapterContextMenuInfo.id);
            cursor.requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weatherDB != null)
            weatherDB.close();
    }
}
