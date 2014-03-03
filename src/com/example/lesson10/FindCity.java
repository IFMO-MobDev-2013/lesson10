package com.example.lesson10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 03.03.14
 * Time: 4:32
 * To change this template use File | Settings | File Templates.
 */
public class FindCity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_city);

        Button find = (Button)findViewById(R.id.buttonFind);
        final EditText city = (EditText)findViewById(R.id.editCity);
        final EditText country = (EditText)findViewById(R.id.editCountry);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.instance,MyIntentServ.class);
                String name = city.getText().toString().replaceAll(" ","+") + "," + country.getText().toString().replaceAll(" ","+");
                startService(intent.putExtra("task","find").putExtra("name",name));
                intent = new Intent(MyActivity.instance,ResultFind.class);
                startActivity(intent);
                close();
            }
        });
    }

    private void close(){
        super.onBackPressed();
    }
}