package ru.ifmo.ctddev.isaev.WeatherForecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.j256.ormlite.dao.Dao;
import ru.ifmo.ctddev.isaev.WeatherForecast.orm.City;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CitiesActivity extends Activity {
    private ListView citiesListView;
    private ListView foundedCitiesListView;
    private EditText editText;
    private Button searchButton;
    private ImageView exitButton;
    private Toast toast;
    Dao cityDAO;

    private Context getActivityContext() {
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        editText.setText("");
        makeDBCitiesVisible();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            cityDAO = DatabaseManager.getInstance().getHelper().getCityDao();
        } catch (SQLException e) {
            Log.e("12", "1212", e);
        }
        citiesListView = (ListView) findViewById(R.id.citiesListView);
        foundedCitiesListView = (ListView) findViewById(R.id.foundedCitiesListView);
        List<City> cityList = null;
        try {
            cityList = cityDAO.queryForAll();
        } catch (SQLException e) {
            android.util.Log.e("11", "111", e);
        }

        searchButton = (Button) findViewById(R.id.searchButton);
        exitButton = (ImageView) findViewById(R.id.exitButton);
        editText = (EditText) findViewById(R.id.editText);
        toast = Toast.makeText(this, "", 2000);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDBCitiesVisible();
            }
        });
        foundedCitiesListView.setAdapter(new AddressAdapter(this,
                android.R.layout.simple_list_item_1, new ArrayList<Address>()));
        final CityAdapter cityAdapter = new CityAdapter(this, android.R.layout.simple_list_item_1, cityList);
        citiesListView.setAdapter(cityAdapter);
        foundedCitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgressDialog.show(getActivityContext(), "Loading", "Wait while loading...");
                final Address address = (Address) adapterView.getItemAtPosition(i);
                City city = new City(address.getLocality(), address.getCountryName(), address.getLatitude(), address.getLongitude());
                try {
                    cityDAO.create(city);
                    cityAdapter.add(city);
                    openForecast(city);
                } catch (SQLException e) {
                    Log.e("", "", e);
                }

            }
        });

        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final City city = (City) adapterView.getItemAtPosition(i);
                openForecast(city);
            }
        });
        citiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                final City city = (City) adapterView.getItemAtPosition(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                builder.setCancelable(true);
                builder.setMessage(R.string.dialog_about_message);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cityAdapter.remove(city);
                        try {
                            cityDAO.delete(city);
                        } catch (SQLException e) {
                            Log.e("", "", e);
                        }
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                makeGeocoderCitiesVisible();
                try {
                    List<Address> cities = geocoder.getFromLocationName(editText.getText().toString(), 50);
                    for (Address a : cities) {
                        Log.i("hello", a.getCountryName() + " " + a.getLocality() + " " + a.getAdminArea() + " ");
                    }
                    ArrayAdapter adapter = (ArrayAdapter) foundedCitiesListView.getAdapter();
                    adapter.clear();
                    for (Address address : cities) {
                        if (address.getLocality() != null & address.getCountryName() != null) {
                            adapter.add(address);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    toast.setText(R.string.empty_field);
                    toast.show();
                } catch (IOException e) {
                    toast.setText(R.string.no_connection);
                    toast.show();
                }

            }
        });
    }


    private void makeDBCitiesVisible() {
        citiesListView.setVisibility(View.VISIBLE);
        foundedCitiesListView.setVisibility(View.GONE);
    }

    private void makeGeocoderCitiesVisible() {
        citiesListView.setVisibility(View.GONE);
        foundedCitiesListView.setVisibility(View.VISIBLE);
    }

    private void openForecast(City city) {
        Intent intent = new Intent(getActivityContext(), ForecastActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);

    }

}
