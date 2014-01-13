package com.example.MeWeather;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ViruZ on 09.01.14.
 */
public class refresh extends IntentService {
    private static final String TAG = refresh.class.toString();
    public static final String REFRESH = TAG.concat(":REFRESH");

    public refresh() {
        super("refresher");
    }

    public static void refreshAll(Context context) {
        context.startService(new Intent(context, refresh.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendBroadcast(new Intent(REFRESH));
    }
}
