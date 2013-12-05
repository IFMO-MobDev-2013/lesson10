package ru.ifmo.ctddev.skripnikov.Weather2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressBarView extends FrameLayout {

    private TextView text;

    public ProgressBarView(Context context) {
        super(context);
        initialization(context);
    }

    public ProgressBarView(Context context, AttributeSet attr) {
        super(context, attr);
        initialization(context);
    }

    private void initialization(Context context) {
        ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.progress_bar_view, this);
        text = (TextView) findViewById(R.id.text);
        LinearLayout progressBar = (LinearLayout) findViewById(R.id.base);
        progressBar.setGravity(Gravity.CENTER);
        setBackgroundColor(Color.BLACK);
        setAlpha(0.8f);
        setVisibility(INVISIBLE);
    }

    public void setText(CharSequence text) {
        this.text.setText(text);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(INVISIBLE);
    }
}