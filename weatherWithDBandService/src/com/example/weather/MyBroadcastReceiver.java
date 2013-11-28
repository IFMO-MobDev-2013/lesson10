package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 21.11.13
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "FUCKENFUCK::RECEIVER ";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ((Main) context).upAllData();
            ((Main) context).upAllUI();
            Toast.makeText((Main)context, "almost SUCCESS!", 1000).show();
            ((Main) context).stopProgressBar();
        } catch (Exception e) {
            Log.w(TAG, "unable to update data in BroadcastReceiver " + e.getMessage());
        }
    }
}
