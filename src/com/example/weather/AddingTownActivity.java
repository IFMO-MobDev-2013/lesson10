package com.example.weather;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
            itemView.setTextAppearance(context, R.style.SmallTownName);
            if (item != null) {
                itemView.setText(item.longName);
            } else itemView.setText(R.string.ErrorChannel);
            return itemView;
        }
    }

    public static String placeRequest(String place) {
        String YAHOO_ADDRESS = "http://where.yahooapis.com/v1/places.q('";
        String YAHOO_ADDRESS2 = "');count=30?appid=[";
        String curPlace = "";
        for (int i = 0; i < place.length(); i++) {
            if (Character.isWhitespace(place.charAt(i)))
                curPlace += "%" + place.getBytes()[i];
            else
                curPlace += place.charAt(i);
        }
        return (YAHOO_ADDRESS + curPlace + YAHOO_ADDRESS2 + MyActivity.YAHOO_ID + "]");
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

            Intent intent = new Intent(getApplicationContext(), UpdatingService.class);
            intent.putExtra(UpdatingService.ALL_ID, false);
            intent.putExtra(Town._ID, town.id);
            intent.putExtra(Town.WOEID, town.woeid);
            intent.putExtra(Town.NAME, town.name);

            startService(intent);

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
            {
                Toast.makeText(getApplicationContext(), R.string.TownsDownloadBad, Toast.LENGTH_LONG).show();
                return;
            }
            array = new ArrayList<Town>();
            for (int i = 0; i < saxParserTown.array.size(); i++)
                array.add((Town) saxParserTown.array.get(i));

            adapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, array);

            listView = (ListView) findViewById(R.id.listViewAdding);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(adding);

            if(array.isEmpty())
                Toast.makeText(getApplicationContext(), R.string.TownsDownloadBad, Toast.LENGTH_LONG).show();


        }
    }


    View.OnClickListener beginSearching = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

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
