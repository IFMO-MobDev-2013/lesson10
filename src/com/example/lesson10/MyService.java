package com.example.lesson10;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class MyService extends IntentService
{
    public MyService()
    {
        super("Global Weather service");
    }

    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            if (Database.gi() == null)
            {
                Database.init(this);
            }
            Console.print("Service is updating...");
            Cursor sth = Database.gi().query("select * from cities where chosen = 1");
            Console.print("Count "+sth.getCount());
            while (sth.moveToNext())
            {
                Console.print("Update id = "+sth.getString(0));
                City city = City.findById(Integer.parseInt(sth.getString(0)));
                city.startUpdate();
            }
        }
        catch (Exception e)
        {
            Log.i("CONSOLE", "Service Error", e);
        }
    }
}
