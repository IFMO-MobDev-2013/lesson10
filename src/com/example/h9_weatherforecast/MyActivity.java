package com.example.h9_weatherforecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    ListView list;
    EditText edit;
    int DIALOG_DELETE = 1;
    int deleted;
    private static final int CM_DELETE_ID = 1;
    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    final String CITY_ENG = "city_eng" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        list = (ListView)findViewById(R.id.listView);
        edit = (EditText)findViewById(R.id.editText);
        db = new DB(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);
        String[] from = new String[]{DB.COLUMN_TXT};
        int[] to = new int[] {R.id.tvText};
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        list.setAdapter(scAdapter);
        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), WeatherActivity.class);
                intent.putExtra(CITY_ENG,db.getRec(position));
                startActivity(intent);
            }
        });
    }

    public void addingnewcity(View v)
    {
        if (edit.getText().toString().equals(""))
            return;
        db.addRec(edit.getText().toString());
        cursor.requery();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить запись?");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            db.delRec(acmi.id);
            cursor.requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();

    }
}
