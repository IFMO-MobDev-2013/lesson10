package com.example.weather;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Change extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change);
        TextView textView = (TextView) findViewById(R.id.textView_delete);
        textView.setText(getIntent().getStringExtra("name"));
        Button button = (Button) findViewById(R.id.button_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase wdb = dbHelper.getWritableDatabase();
                Cursor cursor = wdb.query(DBHelper.DATABASE_NAME, null, null, null, null, null, null);
                int name_column = cursor.getColumnIndex(DBHelper.TOWN_NAME);
                int id_column = cursor.getColumnIndex(DBHelper._ID);
                while (cursor.moveToNext()) {
                    if (cursor.getString(name_column) != null && cursor.getString(name_column).equals(getIntent().getStringExtra("name"))) {
                        wdb.delete(DBHelper.DATABASE_NAME, DBHelper._ID + "=" + cursor.getString(id_column), null);
                    }
                }
                finish();
            }
        });
    }
}
