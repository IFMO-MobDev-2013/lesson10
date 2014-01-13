/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.skipor.weather;


import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ru.skipor.RssReader.R;

/**
 * Created by Vladimir Skipor on 11/7/13.
 * Email: vladimirskipor@gmail.com
 */
public class CitiesListActivity extends ListActivity {


    public static final long UPDATE_PERIOD_SECONDS =  60 * 60 * 3;
    private PendingIntent pIntent;


    private void openFeed(String feedName) {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra(WeatherActivity.EXTRA_CITY_NAME, feedName);
        startActivity(intent);
    }

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int EDIT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;


    private WeatherDatabaseHelper myDatabaseHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_list);
        setTitle(R.string.cities_list_title);
        myDatabaseHelper = WeatherDatabaseHelper.getInstance(this);
        myDatabaseHelper.open();
        //fill data
        Cursor feedsCursor = myDatabaseHelper.fetchAllCities();

        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{WeatherDatabaseHelper.KEY_CITY_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter feeds =
                new SimpleCursorAdapter(this, R.layout.city_row, feedsCursor, from, to, 0);
        setListAdapter(feeds);

        //fill data end
        registerForContextMenu(getListView());


        setUpdateTimer(UPDATE_PERIOD_SECONDS);
    }



    private void refillData() {
        Cursor newCursor = myDatabaseHelper.fetchAllCities();
        Cursor oldCursor = ((SimpleCursorAdapter) getListAdapter()).swapCursor(newCursor);
        if (oldCursor != null) {
            oldCursor.close();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cities_menu, menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                createFeed();
                return true;
            case R.id.action_refresh_all:
                final Intent intent = new Intent(WeatherUpdateService.ACTION_UPDATE_ALL, null, this, WeatherUpdateService.class);
                intent.putExtra(WeatherUpdateService.EXTRA_INFORM_ABOUT_UPDATE, true);
                startService(intent);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo infoDelete = (AdapterContextMenuInfo) item.getMenuInfo();
                myDatabaseHelper.deleteCity(infoDelete.id);
                refillData();
                break;
            case EDIT_ID:
                AdapterContextMenuInfo infoEdit = (AdapterContextMenuInfo) item.getMenuInfo();
                Intent i = new Intent(this, CityEditActivity.class);
                i.putExtra(WeatherDatabaseHelper.KEY_ROWID, infoEdit.id);
                startActivityForResult(i, ACTIVITY_EDIT);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void createFeed() {
        Intent i = new Intent(this, CityEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = null;
        try {
            cursor = myDatabaseHelper.fetchCity(id);
            String name = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_CITY_NAME));
            openFeed(name);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        refillData();
    }


    private void setUpdateTimer(long periodSeconds) {
        Intent intent = new Intent(this, WeatherUpdateService.class);
        intent.setAction(WeatherUpdateService.ACTION_UPDATE_ALL);
        intent.putExtra(WeatherUpdateService.EXTRA_INFORM_ABOUT_UPDATE, false);

        pIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //for 30 mint 60*60*1000
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, 0,
                1000 * periodSeconds, pIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refillData();
    }

    @Override
    protected void onStop() {
        closeCursor();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pIntent);

        myDatabaseHelper.close();
        super.onDestroy();
    }

    private void closeCursor() {
        Cursor cursor = ((SimpleCursorAdapter) getListAdapter()).getCursor();
        if (cursor != null) {
            cursor.close();
        }
    }
}
