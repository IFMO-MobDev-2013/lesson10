package ru.ifmo.mobdev.weather.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.ifmo.mobdev.weather.R;
import ru.ifmo.mobdev.weather.service.UpdateCityService;
import ru.ifmo.mobdev.weather.service.UpdateWeatherService;
import ru.ifmo.mobdev.weather.adapters.CityWeatherAdapter;
import ru.ifmo.mobdev.weather.database.DataBaseTable;
import ru.ifmo.mobdev.weather.database.dbOpenHelper;

public class CityWeatherFragment extends Fragment {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(UpdateCityService.UPDATE_DONE);
    private dbOpenHelper helper;
    public CityWeatherAdapter adapter;
    DataBaseTable table;
    Context ctx;
    ListView listView;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            Toast.makeText(ctx, R.string.updated, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.citylist_activity, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = getActivity();

        helper = new dbOpenHelper(getActivity());
        table = new DataBaseTable(helper.getWritableDatabase());

        final TextView cityName = (TextView) getActivity().findViewById(R.id.cityName);
        final TextView coutryName = (TextView) getActivity().findViewById(R.id.countyName);
        if (table.getSelected() != null) {
            cityName.setText(table.getSelected().getCityName());
            coutryName.setText(table.getSelected().getCountry());
        }

        listView = (ListView) getActivity().findViewById(R.id.list);
        adapter = new CityWeatherAdapter(ctx, table);
        listView.setAdapter(adapter);

        final EditText edit = (EditText) getActivity().findViewById(R.id.editText);
        Button addButton = (Button) getActivity().findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = edit.getText().toString();
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                UpdateCityService.requestUpdate(getActivity(), name);
            }
        });


        UpdateWeatherService.ensureUpdating(getActivity(), true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg) {
                if (table.getSelected() != null)
                    table.updateSelection(table.getSelected().getId(), 0);
                table.updateSelection(arg, 1);
                cityName.setText(table.getSelected().getCityName());
                coutryName.setText(table.getSelected().getCountry());
                adapter.notifyDataSetChanged();
                adapter.refresh();
            }
        });
    }

    private void refreshWeather() {
        adapter.refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, UPDATE_FILTER);
        refreshWeather();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }
}