package ifmo.mobdev.Metcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class WeatherUpdater extends BroadcastReceiver {
    public static final String ACTION_WEATHERUPDATER = "startAlarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        WeatherDBAdapter mDbHelper = new WeatherDBAdapter(context);
        mDbHelper.open();
        Cursor cursor = mDbHelper.fetchAllCities();
        int index = cursor.getColumnIndex(WeatherDBAdapter.KEY_CITY);
        while (cursor.moveToNext()) {
            String city = cursor.getString(index);
            Intent intentMyIntentService = new Intent(context, WeatherIntentService.class);
            intentMyIntentService.putExtra("city", city);
            intentMyIntentService.putExtra("screen", "NO");
            context.startService(intentMyIntentService);
        }
    }
}
