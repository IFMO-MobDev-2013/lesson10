package com.example.weather;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class AddingTownActivity extends Activity {

    EditText editTextName;
    ListView listView;
    int id;

    ArrayList<Town> array;
    MyAdapter adapter;
    SAXParserTown saxParserTown;

    class MyAdapter extends ArrayAdapter<Town> {
        private Context context;

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Town> items) {
            super(context, textViewResourceId, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            Town item = getItem(position);
            TextView itemView = new TextView(context);
            itemView.setMaxLines(10);
            itemView.setTextSize(30);
            itemView.setTextColor(Color.GREEN);
            if (item != null) {
                itemView.setText(item.name);
            } else itemView.setText(R.string.ErrorChannel);
            return itemView;
        }
    }

    public static String placeRequest(String place) {
        String YAHOO_ADDRESS = "http://where.yahooapis.com/v1/places.q('";
        String YAHOO_ADDRESS2 = "');count=30?appid=[";
        return (YAHOO_ADDRESS + place + YAHOO_ADDRESS2 + MyActivity.YAHOO_ID + "]");
    }

    public AdapterView.OnItemClickListener adding = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
            SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();

            Town town = new Town(array.get(position).id, array.get(position).name, array.get(position).woeid);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MyDataBaseHelper.NAME, town.name);
            contentValues.put(MyDataBaseHelper.WOEID, town.woeid);
            //contentValues.put(MyDataBaseHelper._ID, id);

            sqLiteDatabase.insert(MyDataBaseHelper.DATABASE_NAME, null, contentValues);

            sqLiteDatabase.close();
            myDataBaseHelper.close();
            finish();
        }
    };

    private class DownloadFilesTask extends AsyncTask<Object, Integer, Boolean> {
        protected Boolean doInBackground(Object... urls) {
            Downloader downloader = new Downloader();
            try {
                downloader = new Downloader(urls[0].toString(), saxParserTown);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return downloader.successfulDownload;
        }

        protected void onPostExecute(Boolean result) {

            if (!result)
                return;
            array = new ArrayList<Town>();
            for (int i = 0; i < saxParserTown.array.size(); i++)
                array.add((Town) saxParserTown.array.get(i));

            adapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, array);

            listView = (ListView) findViewById(R.id.listViewAdding);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(adding);

            Toast.makeText(getApplicationContext(), R.string.TownsDownload, Toast.LENGTH_SHORT).show();
        }
    }


    View.OnClickListener beginSearching = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if ("".equals(editTextName.getText().toString()))
                Toast.makeText(getApplicationContext(), getString(R.string.ClearSpace), Toast.LENGTH_SHORT).show();
            else {
                getIntent().putExtra(Town.NAME, editTextName.getText().toString());
                saxParserTown = new SAXParserTown();
                AsyncTask asyncTask = new DownloadFilesTask();
                Object object = placeRequest(editTextName.getText().toString());
                asyncTask.execute(object);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding);
        editTextName = (EditText) findViewById(R.id.editNameChange);

        id = getIntent().getIntExtra(Town._ID, 0);

        final Button buttonFind = (Button) findViewById(R.id.buttonFindTown);

        buttonFind.setOnClickListener(beginSearching);
    }

}
