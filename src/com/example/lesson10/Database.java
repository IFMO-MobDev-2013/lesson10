package com.example.lesson10;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper implements IEventDispatcher, IEventHadler {
    private static final String DATABASE_NAME = "weatherdb";
    private static final int DATABASE_VERSION = 3;

    private static Database instance = null;
    private SQLiteDatabase connection;

    public EventDispatcher event_pull;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        event_pull = new EventDispatcher();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
    }

    public static Database gi() {
        return instance;
    }

    public void exec(String sql) {
        try {
            connection = getWritableDatabase();
            connection.execSQL(sql);
        } catch (Exception e) {
            Console.print("Message: " + e.toString() + " connection = " + connection);
            dispatchEvent(new Event(this, Event.ERROR));
        }
    }

    public Cursor query(String sql) {
        try {
            connection = getWritableDatabase();
            Cursor c = connection.rawQuery(sql, null);
            return c;
        } catch (Exception e) {
            Console.print("Message: " + e.toString() + " connection = " + connection);
            dispatchEvent(new Event(this, Event.ERROR));
            return null;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Console.print("onCreate");
        db.execSQL("create table cities(id_city integer primary key, name text not null, chosen integer, temperature integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table cities");
        onCreate(db);
    }

    @Override
    public void addEventListener(IEventHadler listener) {
        event_pull.addEventListener(listener);
    }

    @Override
    public void removeEventListener(IEventHadler listener) {
        event_pull.removeEventListener(listener);
    }

    @Override
    public void dispatchEvent(Event e) {
        event_pull.dispatchEvent(e);
    }

    @Override
    public void handleEvent(Event e) {

    }
}
