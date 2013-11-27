package com.example.weather;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;


public class MyActivity extends Activity {

    MyAdapter adapter;
    ArrayList<Town> array;
    ListView listView;
    int maxTown = 0;

    public static final String YAHOO_ID = "2_W.fE_V34EWOs2C3xHpCuUUPlsQWUlZsJlMQUUSQqWCw7N06ufii74sZOrA1jkuW1sDaA--";

    class MyAdapter extends ArrayAdapter<Town> {
        private Context context;

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Town> items) {
            super(context, textViewResourceId, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            Town item = getItem(position);
            TextView itemView = new TextView(context);
            itemView.setTextSize(30);
            itemView.setMaxLines(10);
            itemView.setTextColor(Color.GREEN);
            if (item != null) {
                itemView.setText(item.name);
            } else itemView.setText(R.string.ErrorChannel);
            return itemView;
        }
    }

    public AdapterView.OnItemClickListener goToTown = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra(Town.NAME, array.get(position).name);
            intent.putExtra(Town.WOEID, array.get(position).woeid);
            intent.putExtra(Town._ID, array.get(position).id);
            intent.setClass(getApplicationContext(), TownActivity.class);

            startActivity(intent);
        }
    };


    public class MyAlertDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.IfDelete)
                    .setPositiveButton(R.string.YesDelete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if ("".equals(getIntent().getStringExtra(Town.WOEID)) || null == getIntent().getStringExtra(Town.WOEID))
                                        return;

                                    MyDataBaseTownHelper myDataBaseTownHelper = new MyDataBaseTownHelper(getApplicationContext(), getIntent().getStringExtra(Town.WOEID));
                                    SQLiteDatabase sqLiteDatabase = myDataBaseTownHelper.getWritableDatabase();
                                    sqLiteDatabase.execSQL(MyDataBaseTownHelper.DROP_DATABASE_NO_ID + getIntent().getStringExtra(Town.WOEID));
                                    sqLiteDatabase.close();
                                    myDataBaseTownHelper.close();

                                    MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
                                    sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
                                    sqLiteDatabase.delete(MyDataBaseHelper.DATABASE_NAME, MyDataBaseHelper.WOEID + "=" + getIntent().getStringExtra(Town.WOEID), null);
                                    sqLiteDatabase.close();
                                    myDataBaseHelper.close();
                                    fillingArraysAndAdapter();
                                }
                            }
                    )
                    .setNegativeButton(R.string.NoDelete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }
                    )
                    .setCancelable(true)
                    .create();
        }
    }


    public AdapterView.OnItemLongClickListener deleteTown = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView parent, View v, int position, long id) {

            getIntent().putExtra(Town._ID, array.get(position).id);
            if (null == array.get(position).woeid || "".equals(array.get(position)))
                getIntent().putExtra(Town.WOEID, "");
            else
                getIntent().putExtra(Town.WOEID, array.get(position).woeid);
            DialogFragment newFragment = new MyAlertDialogFragment();
            newFragment.show(getFragmentManager(), "dialog");

            return true;
        }
    };

    public void addChannel(View view) {
        Intent intent = new Intent();
        maxTown++;
        intent.putExtra(Town._ID, maxTown);
        intent.setClass(getApplicationContext(), AddingTownActivity.class);

        startActivity(intent);
    }

    public void updateFeed(View view) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        Intent intent = new Intent(getApplicationContext(), UpdatingService.class);
        intent.putExtra(UpdatingService.ALL_ID, true);
        startService(intent);
    }

    public void fillingArraysAndAdapter() {
        MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MyDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int id_column = cursor.getColumnIndex(MyDataBaseHelper._ID);
        int name_column = cursor.getColumnIndex(MyDataBaseHelper.NAME);
        int woeid_column = cursor.getColumnIndex(MyDataBaseHelper.WOEID);

        array = new ArrayList<Town>();

        while (cursor.moveToNext()) {
            if (cursor.getString(name_column) == null || "".equals(cursor.getString(name_column)) ||
                    cursor.getString(woeid_column) == null || "".equals(cursor.getString(woeid_column)))
                continue;
            array.add(new Town(cursor.getInt(id_column), cursor.getString(name_column), cursor.getString(woeid_column)));
            maxTown = Math.max(maxTown, cursor.getInt(id_column));
        }
        cursor.close();
        sqLiteDatabase.close();
        myDataBaseHelper.close();

        adapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, array);

        listView = (ListView) findViewById(R.id.listViewMain);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(goToTown);

        listView.setOnItemLongClickListener(deleteTown);

        adapter.notifyDataSetChanged();

        getIntent().putExtra(Town.WOEID, "");
        getIntent().putExtra(Town._ID, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        fillingArraysAndAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter(UpdatingService.UPDATING_ACTION));
        fillingArraysAndAdapter();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fillingArraysAndAdapter();
            Toast.makeText(context, intent.getStringExtra(UpdatingService.RESULT), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(getApplicationContext(), UpdatingService.class);
        intent.putExtra(UpdatingService.ALL_ID, true);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 109023421, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 15000, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

    }
}