package com.example.Weather;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AddCityActivity extends Activity {

    ArrayList<City> arrayList;
    ArrayAdapter<City> arrayAdapter;
    ListView listView;

    class Task extends AsyncTask<String, Void, Void> {
        public static final String API_URL = "http://api.worldweatheronline.com/free/v1/search.ashx?";
        public static final String API_KEY = "c52mv9u4u49cvxc8se4nqk2r";

        String search(String name) throws IOException {
            String requestUrl = API_URL + "key=" + API_KEY + "&query=" + URLEncoder.encode(name, "UTF-8") + "&num_of_results=5&format=json&popular=yes";
            URL url = new URL(requestUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.connect();
            int rc = httpConnection.getResponseCode();
            if (rc == HttpURLConnection.HTTP_OK) {
                String line = null;
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                StringBuilder strBuilder = new StringBuilder();
                while ((line = buffReader.readLine()) != null) {
                    strBuilder.append(line + '\n');
                }
                return strBuilder.toString();
            } else {
                return null;
            }
        }

        void parseFromJSON(String source) throws JSONException {
            JSONObject object = (JSONObject) new JSONTokener(source).nextValue();
            object = object.getJSONObject("search_api");
            JSONArray jsonArray = object.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getJSONObject(i);
                JSONArray tmp = object.getJSONArray("areaName");
                String name = tmp.getJSONObject(0).getString("value");
                tmp = object.getJSONArray("country");
                name += ", " + tmp.getJSONObject(0).getString("value");
                arrayList.add(new City(name));
            }
        }


        @Override
        protected Void doInBackground(String... params) {
            String result = null;
            try {
                result = search(params[0]);
            } catch (IOException e) {
            }
            if (result != null) {
                try {
                    parseFromJSON(result);
                } catch (JSONException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (arrayList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Nothing Found", 2000);
            } else {
                Toast.makeText(getApplicationContext(), "Three best results", 2000);
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    Button button;
    EditText editText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city_activity);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = editText.getText().toString();
                if (!"".equals(tmp) && !" ".equals(tmp)) {
                    arrayList.clear();
                    arrayAdapter.notifyDataSetChanged();
                    Task task = new Task();
                    String[] temp = new String[1];
                    temp[0] = tmp;
                    task.execute(temp);
                }
            }
        });

        editText = (EditText) findViewById(R.id.editText);

        arrayList = new ArrayList<City>();
        arrayAdapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.adapter_view, arrayList);
        listView = (ListView) findViewById(R.id.search_city_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityDataBaseHelper myDataBaseHelper = new CityDataBaseHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();

                City city = new City(arrayList.get(position).name);

                ContentValues contentValues = new ContentValues();
                contentValues.put(CityDataBaseHelper.NAME, city.name);

                sqLiteDatabase.insert(CityDataBaseHelper.DATABASE_NAME, null, contentValues);

                sqLiteDatabase.close();
                myDataBaseHelper.close();
                finish();
            }
        });
        listView.setAdapter(arrayAdapter);


    }
}