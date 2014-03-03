package com.example.lesson10;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 02.03.14
 * Time: 2:54
 * To change this template use File | Settings | File Templates.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        if ("com.example.lesson10.RESULT_FIND".equals(intent.getAction())){
            ResultFind.arrayAdapter.notifyDataSetChanged();
        } else if ("com.example.lesson10.REFRESH".equals(intent.getAction())){
            CharSequence text = MyActivity.refreshMsg;
            int duration = Toast.LENGTH_SHORT;

            MyActivity.adapter.notifyDataSetChanged();

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
