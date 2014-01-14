package ru.ifmo.Weather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.example.lesson9.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 27.11.13
 * Time: 21:35
 * To change this template use File | Settings | File Templates.
 */
public class AddTownActivity extends Activity {


    final static String ACTION_response = "com.example.lesson9.intentServiceTownLoaderWeather";
    ListView townListView;
    EditText searchTown;
    ArrayList<Location> locations = new ArrayList<Location>();
    TownAdapter adapter;
    TownDatabase mDbHelper;
    BroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_town);

        townListView = (ListView) findViewById(R.id.searchTownListView);
        searchTown = (EditText) findViewById(R.id.newTownSearch);

        mDbHelper = new TownDatabase(this);
        mDbHelper.open();
        adapter = new TownAdapter(this, locations);
        townListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        searchTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) return;
                Intent updateServiceIntent = new Intent(AddTownActivity.this, TownLoader.class);
                startService(updateServiceIntent.putExtra("url", MainActivity.createTownRequest(editable.toString(), 15)));
            }
        });

        townListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent updateServiceIntent = new Intent(AddTownActivity.this, WeatherUpdater.class);
                startService(updateServiceIntent);
                mDbHelper.addTown(locations.get(i));
                Intent intent = new Intent(AddTownActivity.this, TownsActivity.class);
                intent.putExtra("data", locations.get(i).param);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_response);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // un-register BroadcastReceiver
        unregisterReceiver(myBroadcastReceiver);
    }

    void updateList(ArrayList<Location> t) {
        locations.clear();
        for (int i = 0; i < t.size(); i++) {
            locations.add(t.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    void copyList(ArrayList<Location> a, ArrayList<Location> b) {
        b.clear();
        for (int i = 0; i < a.size(); i++) {
            b.add(a.get(i));
        }

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(TownLoader.LOADING_ERROR, false)) {
                Log.w("AddTownActivity", "Service hasn't downloaded data");
                return;
            }

            locations.clear();
            for (int i = 0; ; i++) {
                String[] a = intent.getStringArrayExtra("data" + i);
                if (a == null) break;
                Location l = new Location();
                l.param = a;
                locations.add(l);
            }
            adapter.notifyDataSetChanged();

        }
    }

}
