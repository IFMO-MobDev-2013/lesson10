package com.example.weather;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Charm
 * Date: 19.11.13
 * Time: 15:58
 * To change this template use File | Settings | File Templates.
 */
public class ShowWeather extends Activity {
    String globalCityName;
    ArrayList<DetailsDay> days;

    ArrayList<String> result;

    DataBase sqh;
    SQLiteDatabase sqdb;
    int cityId;
    byte[] byteArray;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showweather);


        sqh = new DataBase(this);
        sqdb = sqh.getWritableDatabase();


        Intent intent = getIntent();
        globalCityName = intent.getStringExtra("city");
        String tmp = intent.getStringExtra("cityid");
        cityId = Integer.parseInt(tmp);

        print();
    }

    void updateDataBase() {
        sqdb.delete(DataBase.TABLE_NAME, DataBase.CITYID + "=?", new String[]{String.valueOf(cityId)});
        ContentValues newValues = new ContentValues();
        newValues.put(DataBase.CITYID, result.get(0));
        newValues.put(DataBase.CITYNAME, result.get(1));
        newValues.put(DataBase.TEMPERATURE, result.get(2));
        newValues.put(DataBase.DATE, result.get(3));
        newValues.put(DataBase.SKY, result.get(4));
        newValues.put(DataBase.SUNRISE, result.get(5));
        newValues.put(DataBase.SUNSET, result.get(6));
        newValues.put(DataBase.SPEED, result.get(7));
        newValues.put(DataBase.DIRECTION, result.get(8));
        newValues.put(DataBase.HUMIDITY, result.get(9));
        newValues.put(DataBase.DAY1, days.get(0).getAll());
        newValues.put(DataBase.DAY2, days.get(1).getAll());
        newValues.put(DataBase.DAY3, days.get(2).getAll());
        newValues.put(DataBase.IMAGE, byteArray);
        sqdb.insert(DataBase.TABLE_NAME, null, newValues);
    }

    void print() {
        TextView cityName = (TextView) findViewById(R.id.city);
        TextView date = (TextView) findViewById(R.id.date);
        TextView temperature = (TextView) findViewById(R.id.temperature);
        ImageView image = (ImageView) findViewById(R.id.image);
        TextView direction = (TextView) findViewById(R.id.direction);
        TextView speed = (TextView) findViewById(R.id.speed);
        TextView humidity = (TextView) findViewById(R.id.humidity);
        TextView sunRise = (TextView) findViewById(R.id.sunrise);
        TextView sunSet = (TextView) findViewById(R.id.sunset);
        TextView sky = (TextView) findViewById(R.id.sky);
        TextView day1 = (TextView) findViewById(R.id.day1);
        TextView day2 = (TextView) findViewById(R.id.day2);
        TextView day3 = (TextView) findViewById(R.id.day3);
        Button refresh = (Button) findViewById(R.id.refresh);

        Cursor cursor = sqdb.query(DataBase.TABLE_NAME, null, "cityid = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(DataBase.CITYID));
                String cityName_ = cursor.getString(cursor.getColumnIndex(DataBase.CITYNAME));
                String temperature_ = cursor.getString(cursor.getColumnIndex(DataBase.TEMPERATURE));
                String date_ = cursor.getString(cursor.getColumnIndex(DataBase.DATE));
                String sky_ = cursor.getString(cursor.getColumnIndex(DataBase.SKY));
                String sunRise_ = cursor.getString(cursor.getColumnIndex(DataBase.SUNRISE));
                String sunSet_ = cursor.getString(cursor.getColumnIndex(DataBase.SUNSET));
                String speed_ = cursor.getString(cursor.getColumnIndex(DataBase.SPEED));
                String direction_ = cursor.getString(cursor.getColumnIndex(DataBase.DIRECTION));
                String humidity_ = cursor.getString(cursor.getColumnIndex(DataBase.HUMIDITY));
                String day1_ = cursor.getString(cursor.getColumnIndex(DataBase.DAY1));
                String day2_ = cursor.getString(cursor.getColumnIndex(DataBase.DAY2));
                String day3_ = cursor.getString(cursor.getColumnIndex(DataBase.DAY3));
                byte[] image_ = cursor.getBlob(cursor.getColumnIndex(DataBase.IMAGE));
                if (String.valueOf(cityId).equals(id)) {
                    cityName.setText(cityName_);
                    if ("".equals(temperature_)) {
                        cityName.setText("нет загруженной базы/доступа к интернету");
                    } else {
                        temperature.setText(temperature_ + " °C");
                        date.setText("last update: " + date_);
                        sky.setText(sky_);
                        sunRise.setText("sunrise: " + sunRise_);
                        sunSet.setText("sunset: " + sunSet_);
                        speed.setText("speed wild " + speed_ +"km/h");
                        direction.setText("direction wild " + direction_ +"°");
                        humidity.setText("humidity " + humidity_ + "%");
                        day1.setText(day1_);
                        day2.setText(day2_);
                        day3.setText(day3_);
                        image.setImageBitmap(BitmapFactory.decodeByteArray(image_, 0, image_.length));
                    }
                }
            }
            cursor.close();
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadcastReceiver br = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        days = intent.getExtras().getParcelableArrayList("days");
                        result = intent.getExtras().getStringArrayList("result");
                        byteArray = intent.getExtras().getByteArray("image");
                        if (days != null && result != null) {
                            if (result.size() > 0) {
                                updateDataBase();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "can't to connect",
                                        Toast.LENGTH_SHORT).show();
                            }
                            print();
                        }

                    }
                };



                IntentFilter intentFilter = new IntentFilter("1");
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(br, intentFilter);

                Intent go = new Intent(ShowWeather.this, ServiceDownload.class).putExtra("cityid", String.valueOf(cityId)).putExtra("city", globalCityName)
                        .putExtra("one", "1");
                startService(go);
                print();


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.putInt("cityId", cityId);
        editor.putString("cityName", globalCityName);
        editor.commit();
    }
}