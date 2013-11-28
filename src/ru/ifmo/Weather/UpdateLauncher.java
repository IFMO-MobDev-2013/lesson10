package ru.ifmo.Weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 19.11.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public class UpdateLauncher extends BroadcastReceiver {
    private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    private static ArrayList<Location> townList;

    public UpdateLauncher() {
        super();
    }

    public UpdateLauncher(ArrayList<Location> towns) {
        super();
        townList = towns;
    }

    @Override
    public void onReceive(Context context, Intent broadcastIntent) {

        if (BOOT_ACTION.equalsIgnoreCase(broadcastIntent.getAction())) {
            UpdateLauncher.this.start(context);
        }

        Toast.makeText(context, "Погода обновляется", Toast.LENGTH_LONG).show();

        for (int i = 0; i < townList.size(); i++) {
            Intent updateServiceIntent = new Intent(context, WeatherUpdater.class);
            context.startService(updateServiceIntent.putExtra("url", MainActivity.createWeatherRequest(townList.get(i), 3)).putExtra("isLast", i == townList.size() - 1));
        }
    }

    public void start(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateLauncher.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR*4, pi);
    }
}

