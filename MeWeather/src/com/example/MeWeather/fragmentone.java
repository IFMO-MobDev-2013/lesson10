package com.example.MeWeather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

/**
 * Created by ViruZ on 08.01.14.
 */
public class fragmentone extends Fragment {
    private static final int DELETE_ID = Menu.FIRST;
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(CityService.UPDATE_DONE);
    private static final IntentFilter REFRESH = new IntentFilter(refresh.REFRESH);
    private WeatherAdapter adapter;
    private DBWeather dbWeather;
    private Context ctx;
    private ListView listView;
    private Button update;

    private final BroadcastReceiver refresh_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.firstscreen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = getActivity();

        DBHelper helper = new DBHelper(getActivity());
        dbWeather = new DBWeather(helper.getWritableDatabase());

        update = (Button) getActivity().findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iServis.requestUpdate(ctx, true);
            }

        });
        final TextView cityName = (TextView) getActivity().findViewById(R.id.cityName);
        if (dbWeather.getSelected() != null) {
            try {
                cityName.setText(dbWeather.getSelected().getCityName());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        listView = (ListView) getActivity().findViewById(R.id.list);
        adapter = new WeatherAdapter(ctx, dbWeather);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        final AutoCompleteTextView cityToAdd = (AutoCompleteTextView) getActivity().findViewById(R.id.editText);
        cityToAdd.setThreshold(3);
        cityToAdd.setAdapter(new AddCityAdapter(getActivity()));
        cityToAdd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String name = cityToAdd.getText().toString();
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cityToAdd.getWindowToken(), 0);
                    CityService.requestUpdate(getActivity(), name);
                    return true;
                }
                return false;
            }
        });


        iServis.ensureUpdating(getActivity(), true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg) {
                if (dbWeather.getSelected() != null)
                    dbWeather.updateSelection(dbWeather.getSelected().getId(), 0);
                dbWeather.updateSelection(arg, 1);
                try {
                    cityName.setText(dbWeather.getSelected().getCityName());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                refreshWeather();
            }
        });
    }

    private void refreshWeather() {
        adapter.refresh();
        refresh.refreshAll(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, UPDATE_FILTER);
        getActivity().registerReceiver(refresh_receiver, REFRESH);
        refreshWeather();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(refresh_receiver);
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dbWeather.deleteCity(info.id);
                adapter.refresh();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
