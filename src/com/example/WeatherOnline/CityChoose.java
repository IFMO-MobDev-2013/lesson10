package com.example.WeatherOnline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class CityChoose extends Activity {
    /**
     * Called when the activity is first created.
     */
    StartLoadCatcher startLoadCatcher = new StartLoadCatcher();
    FinishLoadCatcher finishLoadCatcher = new FinishLoadCatcher();
    IntentFilter filter;
    int currentTown = -1;
    boolean reloadStart = false;
    String contextChooseCity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //filters
        filter = new IntentFilter(WeatherIntentService.start);
        registerReceiver(startLoadCatcher, filter);
        filter = new IntentFilter(WeatherIntentService.finish);
        registerReceiver(finishLoadCatcher, filter);

        WeatherDataBase dataBaseHelper = new WeatherDataBase(getApplicationContext());
        if(dataBaseHelper.isEmpty()){
            if(checkConnection()){
                dataBaseHelper.createCityTable();
                Intent intentService = new Intent(this, WeatherIntentService.class);
                startService(intentService);
                dataBaseHelper.close();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.internetAvailable, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else{
            dataBaseHelper.close();
            draw(true);
        }
    }

    public boolean checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null){
            return false;
        }
        NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
        for(NetworkInfo currentInfo : networkInfos){
            if(currentInfo.getTypeName().equalsIgnoreCase("WIFI") || currentInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if(currentInfo.isConnected())
                    return true;
        }
        return false;
    }

    public void draw(boolean firstStart){
        ListView view = (ListView) findViewById(R.id.cityList);
        WeatherDataBase dataBaseHelper = new WeatherDataBase(getApplicationContext());
        final ArrayList<String> names = dataBaseHelper.getCityList();
        if(names.size() == 0){
            names.add(getResources().getString(R.string.noCities));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CityChoose.this, android.R.layout.simple_list_item_1, names);
            view.setAdapter(adapter);
            checkFirstStart(firstStart);
            return;
        }
        MyAdapter adapter = new MyAdapter(getApplicationContext(), names);
        view.setAdapter(adapter);
        checkFirstStart(firstStart);
    }

    public void checkFirstStart(boolean firstStart){
        if(firstStart){
            if(checkConnection()){
                Intent intentService = new Intent(this, WeatherIntentService.class);
                intentService.putExtra("flag", true);
                startService(intentService);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.internetAvailable, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.updatingSuccess, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public class StartLoadCatcher extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            reloadStart = true;
            TextView text = (TextView) findViewById(R.id.appNameView);
            text.setText(R.string.updating);
        }
    }

    public class FinishLoadCatcher extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            currentTown = -1;
            reloadStart = false;
            TextView text = (TextView) findViewById(R.id.appNameView);
            text.setText(R.string.app_name);
            draw(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, "Добавить город");
        menu.add(0, 2, 1, "Очистить список городов");//написать метод в базе
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == 1){
            Intent intent = new Intent(CityChoose.this, AddTown.class);
            startActivity(intent);
            finish();
        }else{
            WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());
            ArrayList<String> names = weatherDataBase.getCityList();
            for(int i = 0; i < names.size(); i++){
                weatherDataBase.deleteCity(names.get(i));
            }
            draw(false);
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final ArrayList<String> data;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<String> values){
            super(context, R.id.cityList, values);
            this.context = context;
            data = values;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent){
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.support, parent, false);
            }
            TextView text = (TextView) view.findViewById(R.id.textView);
            final String sup = data.get(pos);
            text.setText(sup);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(reloadStart){
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.updatingNow, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }else{
                        Intent channel = new Intent(CityChoose.this, CurrentWeather.class);
                        channel.putExtra("city", sup);
                        startActivity(channel);
                    }
                }
            });
            registerForContextMenu(text);
            return view;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        menu.add(0, 1, 0, "Удалить");
        contextChooseCity = ((TextView) view).getText().toString();
    }

    @Override
    public boolean onContextItemSelected (MenuItem item){
        WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());
        weatherDataBase.deleteCity(contextChooseCity);
        draw(false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.setGroupVisible(0, true);
        return super.onPrepareOptionsMenu(menu);
    }

    public void onUpdateClick(View view){
        Intent intentService = new Intent(this, WeatherIntentService.class);
        intentService.putExtra("flag", false);
        startService(intentService);
    }
}
