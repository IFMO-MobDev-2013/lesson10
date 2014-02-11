package com.mikhov.Weather;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Act2 extends Activity {

    Database database;
    Cursor cursor;
    long town_id;
    TextView currentTown, title, t1, w1, p1, sr1, ss1, t2, w2, p2, sr2, ss2, t3, w3, p3, sr3, ss3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act2);

        database = new Database(this);
        database.open();
        Bundle extras = getIntent().getExtras();
        town_id = Long.parseLong(extras.getSerializable("id").toString());

        cursor = database.getTownInfo(town_id);
        cursor.moveToNext();

        title = (TextView) findViewById(R.id.title);
        title.setText(cursor.getString(cursor.getColumnIndex(Database.COL_TOWN)));

        t1 = (TextView) findViewById(R.id.t1);
        w1 = (TextView) findViewById(R.id.w1);
        p1 = (TextView) findViewById(R.id.p1);
        sr1 = (TextView) findViewById(R.id.sr1);
        ss1 = (TextView) findViewById(R.id.ss1);

        t2 = (TextView) findViewById(R.id.t2);
        w2 = (TextView) findViewById(R.id.w2);
        p2 = (TextView) findViewById(R.id.p2);
        sr2 = (TextView) findViewById(R.id.sr2);
        ss2 = (TextView) findViewById(R.id.ss2);

        t3 = (TextView) findViewById(R.id.t3);
        w3 = (TextView) findViewById(R.id.w3);
        p3 = (TextView) findViewById(R.id.p3);
        sr3 = (TextView) findViewById(R.id.sr3);
        ss3 = (TextView) findViewById(R.id.ss3);

        t1.setText(getString(R.string.temperature) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_TEMPERATURE1)));
        w1.setText(getString(R.string.weather) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_WEATHER1)));
        p1.setText(getString(R.string.pressure) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_PRESSURE1)) + " " + getString(R.string.pressure_letter));
        sr1.setText(getString(R.string.sunrise) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNRISE1)));
        ss1.setText(getString(R.string.sunset) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNSET1)));
        t2.setText(getString(R.string.temperature) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_TEMPERATURE2)));
        w2.setText(getString(R.string.weather) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_WEATHER2)));
        p2.setText(getString(R.string.pressure) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_PRESSURE2)) + " " + getString(R.string.pressure_letter));
        sr2.setText(getString(R.string.sunrise) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNRISE2)));
        ss2.setText(getString(R.string.sunset) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNSET2)));
        t3.setText(getString(R.string.temperature) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_TEMPERATURE3)));
        w3.setText(getString(R.string.weather) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_WEATHER3)));
        p3.setText(getString(R.string.pressure) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_PRESSURE3)) + " " + getString(R.string.pressure_letter));
        sr3.setText(getString(R.string.sunrise) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNRISE3)));
        ss3.setText(getString(R.string.sunset) + " " + cursor.getString(cursor.getColumnIndex(Database.COL_SUNSET3)));

        ImageView iv1 = (ImageView) findViewById(R.id.image1);
        ImageView iv2 = (ImageView) findViewById(R.id.image2);
        ImageView iv3 = (ImageView) findViewById(R.id.image3);

        byte[] image_byte1 = cursor.getBlob(cursor.getColumnIndex(Database.COL_IMG1));
        iv1.setImageBitmap(scaleLarge(BitmapFactory.decodeByteArray(image_byte1, 0, image_byte1.length)));
        byte[] image_byte2 = cursor.getBlob(cursor.getColumnIndex(Database.COL_IMG2));
        iv2.setImageBitmap(scaleLarge(BitmapFactory.decodeByteArray(image_byte2, 0, image_byte2.length)));
        byte[] image_byte3 = cursor.getBlob(cursor.getColumnIndex(Database.COL_IMG3));
        iv3.setImageBitmap(scaleLarge(BitmapFactory.decodeByteArray(image_byte3, 0, image_byte3.length)));
    }

    public Bitmap scaleLarge(Bitmap b) {
        double nw, nh, scale;
        int displayWidth = 70;
        int bitmapWidth = b.getWidth();
        int bitmapHeight= b.getHeight();
        nw = displayWidth;
        scale = nw / bitmapWidth;
        nh = scale * bitmapHeight;
        b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        return b;
    }

    @Override
    public void onBackPressed() {
        Act2.this.finish();
        Intent intent = new Intent(Act2.this, Act.class);
        startActivity(intent);
        overridePendingTransition(R.layout.slide_right_in, R.layout.slide_right_out);
    }
}