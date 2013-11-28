package com.example.lesson10;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CityEditActivity extends Activity
{
    private long id_subject = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cityedit);

        id_subject = getIntent().getLongExtra("ID_CITY", 0);

        if (id_subject != 0)
        {
            TextView name = (TextView)findViewById(R.id.editSubjectName);
            name.setText(City.findById(id_subject).name);
            ((Button)findViewById(R.id.deleteButton)).setEnabled(true);
        }
        else
        {
            ((Button)findViewById(R.id.deleteButton)).setEnabled(false);
        }
    }

    public void onDeleteButtonClicked(View v)
    {
        City.findById(id_subject).chosen = false;
        City.findById(id_subject).flush();
        Toast.makeText(this, "City has been deleted", 3000).show();
        finish();
    }
}
