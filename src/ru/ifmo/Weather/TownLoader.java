package ru.ifmo.Weather;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 19.11.13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public class TownLoader extends IntentService {
    final static private String TAG = "Reloader service (town)";
    final static String LOADING_ERROR = "Error loading (town)";

    public TownLoader(){
        super("TownLoader");
    }

    public void onCreate(){
        super.onCreate();

    }
    static ArrayList<Location> locs;

    @Override
    protected void onHandleIntent(Intent intent) {

        String url = intent.getStringExtra("url");

        Intent intentResponse = new Intent();
        intentResponse.setAction(AddTownActivity.ACTION_response);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            locs = TownParser.parse(url);
            for (int i = 0; i < locs.size(); i++){
                intentResponse.putExtra("data"+i, locs.get(i).param);
            }
        } catch (Exception ex){
            Log.w(TAG, "Parser failed");
            intentResponse.putExtra(LOADING_ERROR, true);
        }
        sendBroadcast(intentResponse);

    }
}

