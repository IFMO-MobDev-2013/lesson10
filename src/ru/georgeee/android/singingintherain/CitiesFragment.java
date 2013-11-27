package ru.georgeee.android.singingintherain;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;
import ru.georgeee.android.singingintherain.model.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 26.11.13
 * Time: 2:39
 * To change this template use File | Settings | File Templates.
 */
public class CitiesFragment extends Fragment {
    View rootView;
    AutoCompleteTextView cityNameInput;
    Button cityAddButton;
    ListView cityListView;

    HashMap<String, GeocoderResult> options = new HashMap<String, GeocoderResult>();

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = new ArrayList<String>();


        final Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(input).setLanguage(Locale.getDefault().getLanguage()).getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

        options.clear();

        List<GeocoderResult> geocoderResultList = geocoderResponse.getResults();
        for(GeocoderResult result : geocoderResultList){
            String address = result.getFormattedAddress();
            resultList.add(address);
            options.put(address, result);
        }

        return resultList;
    }

    private class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public AutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.cityList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(cityListAdapter.getItem(info.position).getName());
            menu.add(0, MENU_DELETE_ITEM, 0, getString(R.string.deleteBtn));
        }
    }

    public static final int MENU_DELETE_ITEM = 1;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DELETE_ITEM) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            City city = cityListAdapter.getItem(info.position);
            cityListAdapter.remove(city);
            cityListAdapter.notifyDataSetChanged();
            city.delete();
            return true;
        }
        return super.onContextItemSelected(item);
    }


    ArrayAdapter<City> cityListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cities_fragment, container, false);
        cityAddButton = (Button) rootView.findViewById(R.id.cityAddBtn);
        cityNameInput = (AutoCompleteTextView) rootView.findViewById(R.id.cityNameInput);
        cityListView = (ListView) rootView.findViewById(R.id.cityList);
        cityNameInput.setAdapter(new AutoCompleteAdapter(inflater.getContext(), R.layout.autocomplete_list_item));
        cityListAdapter = new ArrayAdapter<City>(inflater.getContext(), R.layout.autocomplete_list_item, City.loadAllFromDB());
        cityListView.setAdapter(cityListAdapter);
        registerForContextMenu(cityListView);
        cityAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityNameInput.getText().toString();
                if(options.containsKey(cityName)){
                    GeocoderResult result = options.get(cityName);
                    LatLng location = result.getGeometry().getLocation();
                    City city = new City();
                    city.setName(result.getAddressComponents().get(0).getLongName());
                    city.setLatitude(location.getLat().toPlainString());
                    city.setLongitude(location.getLng().toPlainString());
                    city.save();
                    UpdateForecastService.startService(v.getContext(), city);
                    cityListAdapter.add(city);
                    cityListAdapter.notifyDataSetChanged();
                    cityNameInput.getText().clear();
                }
            }
        });
        return rootView;
    }




}
