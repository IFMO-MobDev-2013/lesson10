package ifmo.mobdev.Metcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.*;

public class WeatherBroadcastReceiver extends BroadcastReceiver {
    View page1, page2, page3;
    private TextView date, temp, descr, wind, press, hum, curCity;
    private String city, country;
    private long city_id;
    ListView lv2, lv3;
    private EditText addCity;
    private ImageButton imgbut;
    private ImageView picture;
    private WeatherDBAdapter mDbHelper;
    Context context;

    public WeatherBroadcastReceiver() {
        super();
    }

    public WeatherBroadcastReceiver(View p1, View p2, View p3, TextView cur) {
        page1 = p1;
        page2 = p2;
        page3 = p3;
        curCity = cur;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String screen = intent.getStringExtra("screen");
        mDbHelper = new WeatherDBAdapter(context);
        mDbHelper.open();
        this.context = context;
        initialiseViews();
        if (screen.equals("YES")) {
            setToday();
            setWeek();
            WeatherActivity.dialog.dismiss();
        } else {
            Toast toast = Toast.makeText(context, "RELOAD", 1000);
            toast.show();
        }
    }

    private void initialiseViews() {
        //------today-----
        date =    (TextView) page1.findViewById(R.id.txtvDate);
        temp =    (TextView) page1.findViewById(R.id.txtvtemp);
        descr =   (TextView) page1.findViewById(R.id.txtvDescr);
        wind =    (TextView) page1.findViewById(R.id.txtvWind);
        press =   (TextView) page1.findViewById(R.id.txtvPress);
        hum =     (TextView) page1.findViewById(R.id.txtwHum);
        picture = (ImageView) page1.findViewById(R.id.imageView);
        //-----week------
        lv2 = (ListView) page2.findViewById(R.id.lv2);
        //-----city------
        addCity = (EditText) page3.findViewById(R.id.addcity);
        imgbut = (ImageButton) page3.findViewById(R.id.imbv3);
        lv3 = (ListView) page3.findViewById(R.id.lv3);
    }

    private void setToday() {
        city = curCity.getText().toString();
        city_id = mDbHelper.getCityIdByName(city);
        Cursor cursor = mDbHelper.fetchCityToday(city_id);

        int dateind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DATE);
        int tempind = cursor.getColumnIndex(WeatherDBAdapter.KEY_TEMP);
        int descrind = cursor.getColumnIndex(WeatherDBAdapter.KEY_DESCR);
        int windind = cursor.getColumnIndex(WeatherDBAdapter.KEY_WIND);
        int pressind = cursor.getColumnIndex(WeatherDBAdapter.KEY_PRESS);
        int humind = cursor.getColumnIndex(WeatherDBAdapter.KEY_HUM);
        int pic_id = cursor.getColumnIndex(WeatherDBAdapter.KEY_ICON_ID);

        cursor.moveToNext();
        String dt, dr, tm, wn, pr, hm;
        int p_id;

        try {
            dt = cursor.getString(dateind);
            dr = cursor.getString(descrind);
            tm = cursor.getString(tempind);
            wn = cursor.getString(windind);
            pr = cursor.getString(pressind);
            hm = cursor.getString(humind);
            p_id = cursor.getInt(pic_id);
            date.setText("Observation time: " + dt);
            if (Integer.parseInt(tm) > 0) temp.setText("+" + tm + "°C");
            else temp.setText(tm + "°C");
            descr.setText(dr);
            wind.setText("Wind " + wn + " Km/h");
            press.setText("Pressure " + pr + " mb");
            hum.setText("Humidity " + hm + "%");
            picture.setImageResource(p_id);
        } catch (Exception e) {
        }
        cursor.close();
    }

    private void setWeek() {
        city = curCity.getText().toString();
        city_id = mDbHelper.getCityIdByName(city);
        Cursor artCursor = mDbHelper.fetchCityWeek(city_id);
        String[] from = new String[]{WeatherDBAdapter.KEY_DATE, WeatherDBAdapter.KEY_MIN_TEMP,
                WeatherDBAdapter.KEY_MAX_TEMP, WeatherDBAdapter.KEY_DESCR, WeatherDBAdapter.KEY_ICON_ID};
        int[] to = new int[]{R.id.tv2date, R.id.tv2mintemp, R.id.tv2maxtemp, R.id.tv2descr, R.id.weekPic};
        SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(context, R.layout.week_list_item, artCursor, from, to);
        lv2.setAdapter(artAdapter);
    }
}
