package com.example.weather;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    ArrayList<IdCity> data;
    DataBase sqh;
    SQLiteDatabase sqdb;
    final ArrayList<String> texts = new ArrayList<String>();
    final ArrayList<String> index = new ArrayList<String>();
    ListView MainListView;
    SimpleAdapter sAdapter;
    EditText editCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intentAlarm = new Intent(StartActivity.this, AlarmService.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15 * 60 * 1000, 15 * 60 * 1000, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000, 100000, pendingIntent);

        Class<?> activityClass;
        SharedPreferences prefs = null;
        String cityId = null;
        String cityName = null;
        try {
            prefs = getSharedPreferences("X", MODE_PRIVATE);
            activityClass = Class.forName(prefs.getString("lastActivity", StartActivity.class.getName()));
            cityId = String.valueOf(prefs.getInt("cityId", 0));
            cityName = prefs.getString("cityName", "");

        } catch (ClassNotFoundException ex) {
            activityClass = StartActivity.class;
        }
        if ("com.example.weather.ShowWeather".equals(activityClass.getName()))
            startActivity(new Intent(this, activityClass).putExtra("cityid", cityId).putExtra("city", cityName)
                    .putExtra("one", "1"));

        editCity = (EditText) findViewById(R.id.editcity);
        final Button addCity = (Button) findViewById(R.id.addcity);
        MainListView = (ListView) findViewById(R.id.listView);

        sqh = new DataBase(this);
        sqdb = sqh.getWritableDatabase();

        updateList();

        MainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent intent = new Intent(StartActivity.this, ShowWeather.class);
                intent.putExtra("city", texts.get(position));
                intent.putExtra("cityid", index.get(position));
                startActivity(intent);
            }
        });

        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editCity.getText().toString();
                addNewCity t = new addNewCity(city);
                t.execute();
            }
        });
        registerForContextMenu(MainListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "delete city");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Cursor cursor = sqdb.query(DataBase.TABLE_NAME, new String[]{
                    DataBase._ID, DataBase.CITYID, DataBase.CITYNAME, DataBase.TEMPERATURE, DataBase.DATE,
                    DataBase.SKY, DataBase.SUNRISE, DataBase.SUNSET, DataBase.SPEED, DataBase.DIRECTION, DataBase.HUMIDITY,
                    DataBase.DAY1, DataBase.DAY2, DataBase.DAY3, DataBase.IMAGE},
                    null, null,
                    null, null, null);
            texts.clear();
            index.clear();
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(DataBase.CITYID));
                    String cityName_ = cursor.getString(cursor.getColumnIndex(DataBase.CITYNAME));
                    texts.add(cityName_);
                    index.add(id);
                } while (cursor.moveToNext());
            }
            cursor.close();
            sqdb.delete(DataBase.TABLE_NAME, DataBase.CITYID + "=?", new String[]{String.valueOf(index.get(acmi.position))});
            texts.remove(acmi.position);
            index.remove(acmi.position);

            ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>(texts.size());
            HashMap<String, String> map;

            for (int i = 0; i < texts.size(); i++) {
                map = new HashMap<String, String>();
                map.put("city", texts.get(i));
                items.add(map);
            }
            String[] from = {"city"};
            int[] to = {R.id.tvText};
            sAdapter = new SimpleAdapter(this, items, R.layout.item, from, to);
            MainListView.setAdapter(sAdapter);
            return true;
        }
        return super.onContextItemSelected(item);
    }


    void updateList() {
        Cursor cursor = sqdb.query(DataBase.TABLE_NAME, new String[]{
                DataBase._ID, DataBase.CITYID, DataBase.CITYNAME, DataBase.TEMPERATURE, DataBase.DATE,
                DataBase.SKY, DataBase.SUNRISE, DataBase.SUNSET, DataBase.SPEED, DataBase.DIRECTION, DataBase.HUMIDITY,
                DataBase.DAY1, DataBase.DAY2, DataBase.DAY3, DataBase.IMAGE},
                null, null,
                null, null, null);
        texts.clear();
        index.clear();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(DataBase.CITYID));
                String cityName_ = cursor.getString(cursor.getColumnIndex(DataBase.CITYNAME));
                texts.add(cityName_);
                index.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>(texts.size());
        HashMap<String, String> map;

        for (int i = 0; i < texts.size(); i++) {
            map = new HashMap<String, String>();
            map.put("city", texts.get(i));
            items.add(map);
        }

        String[] from = {"city"};
        int[] to = {R.id.tvText};
        sAdapter = new SimpleAdapter(this, items, R.layout.item, from, to);
        MainListView.setAdapter(sAdapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);

        final String[] Cityes = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Cityes[i] = data.get(i).get();
        }

        builder.setTitle("Выберите нужный город");

        builder.setItems(Cityes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                ContentValues newValues = new ContentValues();
                newValues.put(DataBase.CITYID, data.get(item).getId());
                newValues.put(DataBase.CITYNAME, data.get(item).getName());
                newValues.put(DataBase.TEMPERATURE, "");
                newValues.put(DataBase.DATE, "");
                newValues.put(DataBase.SKY, "");
                newValues.put(DataBase.SUNRISE, "");
                newValues.put(DataBase.SUNSET, "");
                newValues.put(DataBase.SPEED, "");
                newValues.put(DataBase.DIRECTION, "");
                newValues.put(DataBase.HUMIDITY, "");
                newValues.put(DataBase.DAY1, "");
                newValues.put(DataBase.DAY2, "");
                newValues.put(DataBase.DAY3, "");
                newValues.put(DataBase.IMAGE, "");
                sqdb.delete(DataBase.TABLE_NAME, DataBase.CITYID + "=?", new String[]{String.valueOf(data.get(item).getId())});
                sqdb.insert(DataBase.TABLE_NAME, null, newValues);

                Intent go = new Intent(StartActivity.this, ServiceDownload.class).putExtra("cityid", String.valueOf(String.valueOf(data.get(item).getId()))).putExtra("city", data.get(item).getName())
                        .putExtra("one", "0");
                startService(go);
                editCity.setText("");
                updateList();
                removeDialog(0);

            }
        });
        removeDialog(0);
        builder.setCancelable(true);
        return builder.create();
    }




    public class addNewCity extends AsyncTask<Void, Void, ArrayList<IdCity>> {
        String link;

        addNewCity(String city) {
            this.link = "http://where.yahooapis.com/v1/places.q('" + URLEncoder.encode(city) + "');count=30?appid=[2_W.fE_V34EWOs2C3xHpCuUUPlsQWUlZsJlMQUUSQqWCw7N06ufii74sZOrA1jkuW1sDaA--]";
        }

        @Override
        protected void onPostExecute(ArrayList<IdCity> result) {
            super.onPostExecute(result);
            if (result.size() == 0) {
                Toast.makeText(getApplicationContext(), "city not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            data = result;
            showDialog(0);
        }

        @Override
        protected ArrayList<IdCity> doInBackground(Void... params) {
            ArrayList<IdCity> cities = new ArrayList<IdCity>();
            try {
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dB = dBF.newDocumentBuilder();
                    Document document = dB.parse(input);
                    Element element = document.getDocumentElement();
                    NodeList nodeList = element.getElementsByTagName("place");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element entry = (Element) nodeList.item(i);
                        Element name = (Element) entry.getElementsByTagName("name").item(0);
                        Element locality = (Element) entry.getElementsByTagName("woeid").item(0);
                        String id = locality.getFirstChild().getNodeValue();
                        Element country = (Element) entry.getElementsByTagName("country").item(0);
                        Element region = (Element) entry.getElementsByTagName("admin1").item(0);

                        String country_ = country.getFirstChild().getNodeValue();
                        String region_ = region.getFirstChild().getNodeValue();

                        IdCity tmp = new IdCity(name.getFirstChild().getNodeValue(), id, country_, region_);
                        cities.add(tmp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cities;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.putInt("cityId", 0);
        editor.putString("cityName", "null");
        editor.commit();
    }
}
