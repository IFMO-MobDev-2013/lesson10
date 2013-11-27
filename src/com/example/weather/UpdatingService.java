package com.example.weather;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UpdatingService extends IntentService {


    public static final String ALL_ID = "all_id";
    public static final String RESULT = "result";

    public UpdatingService(String name) {
        super(name);
    }

    public UpdatingService() {
        super("default_name_");
    }

    public static final String UPDATING_ACTION = "updating";

    ArrayList<Town> array;
    int maxChannel = 0;

    public void fillingArrayAtStart() {
        MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MyDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int id_column = cursor.getColumnIndex(MyDataBaseHelper._ID);
        int name_column = cursor.getColumnIndex(MyDataBaseHelper.NAME);
        int woeid_column = cursor.getColumnIndex(MyDataBaseHelper.WOEID);

        array = new ArrayList<Town>();

        while (cursor.moveToNext()) {
            array.add(new Town(cursor.getInt(id_column), cursor.getString(name_column), cursor.getString(woeid_column)));
            maxChannel = Math.max(maxChannel, cursor.getInt(id_column));
        }

        cursor.close();
        sqLiteDatabase.close();
        myDataBaseHelper.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fillingArrayAtStart();

        boolean allId = intent.getBooleanExtra(ALL_ID, true);
        Town town = new Town(intent.getIntExtra(Town._ID, 0), intent.getStringExtra(Town.NAME), intent.getStringExtra(Town.WOEID));

        Intent intentResponse = new Intent();
        boolean bad = false;

        for (int i = 0; i < array.size(); i++) {
            if (allId || town.id == array.get(i).id) {
                TownUpdater townUpdater = new TownUpdater(array.get(i).woeid, getApplicationContext());
                try {
                    townUpdater.update();
                } catch (Exception e) {
                    e.printStackTrace();
                    bad = true;
                }
            }
        }

        if (bad)
            intentResponse.putExtra(RESULT, getString(R.string.ErrorWithInternet));
        else
            intentResponse.putExtra(RESULT, getString(R.string.SuccessfulUpdate));
        if (!allId)
            intentResponse.setAction(UPDATING_ACTION + "_" + town.name);
        else
            intentResponse.setAction(UPDATING_ACTION);
        sendBroadcast(intentResponse);
    }

}
