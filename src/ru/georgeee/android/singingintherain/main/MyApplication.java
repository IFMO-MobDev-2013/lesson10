package ru.georgeee.android.singingintherain.main;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import ru.georgeee.android.singingintherain.misc.AlarmBroadcastReceiver;
import ru.georgeee.android.singingintherain.model.DatabaseHelperHolder;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.11.13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class MyApplication extends Application {
    public static final String PACKAGE = "ru.georgeee.android.singingintherain";

    /**
     * Alarm interval in seconds
     */
    public static final int ALARM_INTERVAL = 30*60;


    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelperHolder.getHelper(getApplicationContext());
        AlarmManager alarmManager =  (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL * 1000L, alarmIntent);
    }

    @Override
    public void onTerminate() {
        DatabaseHelperHolder.releaseHelper();
        super.onTerminate();
    }

    public static MyApplication getApplication(Context context) {
        if (context instanceof MyApplication) {
            return (MyApplication) context;
        }
        return (MyApplication) context.getApplicationContext();
    }

}
