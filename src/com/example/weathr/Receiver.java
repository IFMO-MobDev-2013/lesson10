package com.example.weathr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Sergey on 12/5/13.
 */
public class Receiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        AlarmManager m = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, WeatherUpdateService.class);
        PendingIntent pi = PendingIntent.getService(context,0,i,Intent.FILL_IN_DATA);
        m.cancel(pi);
        m.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()+300000,AlarmManager.INTERVAL_HOUR,pi);
    }
}
