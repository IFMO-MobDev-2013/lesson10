package com.mikhov.Weather;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

public class AddTownAct extends ListActivity implements View.OnClickListener {

    public static enum TransitionType {
        SlideLeft
    }
    public static TransitionType transitionType;

    EditText addTown;
    Database database;
    ImageButton save;
    String[] townsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_town);
        addTown = (EditText) findViewById(R.id.add_town);
        save = (ImageButton)findViewById(R.id.btn_add_save);
        save.setOnClickListener(this);

        database = new Database(this);
        database.open();

        addTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cur = s.toString();
                if (cur.length() > 0) {
                    Cursor search = database.getYaInfo(cur);
                    if (search.moveToNext()) {
                        String str_search = search.getString(search.getColumnIndex(Database.COL_TOWN_YA)) + "#";
                        while (search.moveToNext()) {
                            str_search += search.getString(search.getColumnIndex(Database.COL_TOWN_YA)) + "#";
                        }
                        str_search = str_search.substring(0, str_search.length() - 1);
                        townsArray = str_search.split("#");
                        showResults();
                    }
                } else {
                    hideResults();
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_save:
                String townToAdd = addTown.getText().toString();
                if (!townToAdd.equals("")) {
                    if (database.uniqueTown(townToAdd) && database.existsTown(townToAdd)) {
                        String codeToAdd = database.getYaCode(townToAdd);
                        database.addTown(codeToAdd, townToAdd);
                        this.finish();
                        Intent intent = new Intent(this, Act.class);
                        intent.putExtra("update", "true");
                        startActivity(intent);
                        overridePendingTransition(R.layout.slide_right_in, R.layout.slide_right_out);
                    } else {
                        if (addTown.getText().toString().indexOf(getString(R.string.oops)) == -1) {
                            addTown.setText(addTown.getText().toString() + " " + getString(R.string.oops));
                        } else {
                            addTown.setText(addTown.getText().toString().substring(0, addTown.getText().toString().length() - 38) + " (ой, уже есть или в базе отсутствует)");
                        }
                    }
                }
                break;
        }
    }

    public void showResults() {
        ArrayAdapter<String> results = new ArrayAdapter<String>(this, R.layout.town_item, townsArray);
        setListAdapter(results);
    }

    public void hideResults() {
        townsArray = new String[0];
        ArrayAdapter<String> results = new ArrayAdapter<String>(this, R.layout.town_item, townsArray);
        setListAdapter(results);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        addTown.setText(townsArray[position]);
        hideResults();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        Intent intent = new Intent(this, Act.class);
        startActivity(intent);
        overridePendingTransition(R.layout.slide_right_in, R.layout.slide_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}