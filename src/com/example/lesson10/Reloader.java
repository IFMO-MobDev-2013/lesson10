package com.example.lesson10;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reloader extends BroadcastReceiver
{
    public Reloader()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent broadcastIntent)
    {
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }

    public void start(Context context)
    {
        Console.print("Start reloader");
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Reloader.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000 * 10, pi);
    }
}
