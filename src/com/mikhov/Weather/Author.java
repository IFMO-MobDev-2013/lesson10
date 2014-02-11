package com.mikhov.Weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Author extends Activity {

    public static enum TransitionType {
        SlideLeft
    }
    public static TransitionType transitionType;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author);
        TextView tv = (TextView) findViewById(R.id.author);
        tv.setText(getString(R.string.about_me));
    }


    @Override
    public void onBackPressed() {
        this.finish();
        Intent intent = new Intent(this, Act.class);
        startActivity(intent);
        overridePendingTransition(R.layout.slide_right_in, R.layout.slide_right_out);
    }
}
