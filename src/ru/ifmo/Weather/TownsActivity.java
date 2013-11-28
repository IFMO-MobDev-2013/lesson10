package ru.ifmo.Weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.lesson9.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 27.11.13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public class TownsActivity extends Activity {


    ListView townListView;
    Button addButton;
    Button deleteButton;
    ArrayList<Location> townList = new ArrayList<Location>();
    ArrayList<Location> wasTownList = new ArrayList<Location>();
    ArrayList<Location> deletedTownList = new ArrayList<Location>();
    TownAdapter adapter;
    TownDatabase mDbHelper;
    boolean deleteMode = false;

    Intent intentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.town_choosing);

        townListView = (ListView) findViewById(R.id.townChoosingList);
        addButton = (Button) findViewById(R.id.TownChoosingAddButton);
        deleteButton = (Button) findViewById(R.id.TownChoosingDeleteButton);

        mDbHelper = new TownDatabase(this);
        mDbHelper.open();
        adapter = new TownAdapter(this, townList);
        townListView.setAdapter(adapter);
        updateList(mDbHelper.getAllTowns());


        adapter.notifyDataSetChanged();

        intentResponse = new Intent(TownsActivity.this, MainActivity.class);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteMode) {
                    deleteMode = true;
                    deleteButton.setText("Ready");
                    deleteButton.setTextColor(0xFFB0B0FF);
                    addButton.setText("Cancel");
                    addButton.setTextColor(0xFFFFB0B0);
                    copyList(townList, wasTownList);
                } else {
                    deleteMode = false;
                    addButton.setText("Add new");
                    addButton.setTextColor(0xFFB0FFFF);
                    deleteButton.setText("Delete");
                    deleteButton.setTextColor(0xFFB0FFFF);
                    for (int i = 0; i < deletedTownList.size(); i++) {
                        mDbHelper.deleteTown(deletedTownList.get(i));
                    }
                    intentResponse.putExtra("towns changed", true);
                }
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deleteMode) {
                    Intent intent = new Intent(TownsActivity.this, AddTownActivity.class);
                    startActivityForResult(intent, 0);
                    intentResponse.putExtra("towns changed", true);
                } else {
                    deleteMode = false;
                    addButton.setText("Add new");
                    addButton.setTextColor(0xFFB0FFFF);
                    deleteButton.setText("Delete");
                    deleteButton.setTextColor(0xFFB0FFFF);
                    copyList(wasTownList, townList);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        townListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!deleteMode){
                    intentResponse.putExtra("choosedTown", i);
                    setResult(RESULT_OK, intentResponse);
                    finish();
                } else {
                    deletedTownList.add(townList.get(i));
                    townList.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK, intentResponse);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void updateList(ArrayList<Location> t) {
        townList.clear();
        for (int i = 0; i < t.size(); i++) {
            townList.add(t.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    void copyList(ArrayList<Location> a, ArrayList<Location> b){
        b.clear();
        for (int i = 0; i < a.size(); i++) {
            b.add(a.get(i));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Location l = new Location();
            l.param = data.getExtras().getStringArray("data");
            townList.add(l);
            adapter.notifyDataSetChanged();
        }
    }
}
