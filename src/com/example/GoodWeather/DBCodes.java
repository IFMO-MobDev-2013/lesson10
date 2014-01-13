package com.example.GoodWeather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCodes {

    public static final String TABLE_ID = "_id";
    private static final String DATABASE_NAME = "codes";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "code_table";
    public static final String CODE = "code";
    public static final String WEATHER = "weather";
    public static final String IMAGE = "image";

    private static final String SQL_CREATE_ENTRIES = "create table "
            + TABLE_NAME + " ("
            + TABLE_ID + " integer primary key autoincrement, "
            + CODE + " text not null, "
            + WEATHER + " text not null, "
            + IMAGE + " integer not null ); ";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    private final Context mcontext;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DBCodes(Context context) {
        this.mcontext = context;
        DBHelper = new DatabaseHelper(mcontext);
        db = DBHelper.getWritableDatabase();
    }


    public void insert() {
        String[] wthrs = new String[] {"Умеренный или сильный снегопад с громом",
                "Местами небольшой снегопад с громом",
                "Умеренный или сильный дождь с громом",
                "Местами небольшой дождь с громом",
                "Умеренный или сильный град",
                "Легкий град",
                "Умеренные или сильный снегопад",
                "Небольшой снегопад",
                "Умеренный или сильный снегопад с дождем",
                "Легкий снегопад с дождем",
                "Проливной дождь",
                "Умеренный или сильный дождь",
                "Легкий дождь",
                "Град",
                "Сильный снегопад",
                "Местами сильный снегопад",
                "Умеренный снегопад",
                "Местами умеренный снегопад",
                "Небольшой снегопад",
                "Местами небольшой снегопад",
                "Умеренный или сильный дождь со снегом",
                "Легкий снег с дождем",
                "Облачно, дождь со снегом",
                "Умеренный или сильный дождь с градом",
                "Сильный дождь",
                "Временами сильный дождь",
                "Умеренный дождь",
                "Местами умеренный дождь",
                "Небольшой дождь",
                "Местами небольшой дождь",
                "Сильный снег с дождем",
                "Снег с дождем",
                "Легкий дождь",
                "Местами моросящий дождь",
                "Холодный туман",
                "Густой туман",
                "Метель с сильным снегопадом",
                "Метель с небольшим снегопадом",
                "Гроза неподалеку",
                "Изморозь",
                "Местами снег с дождем",
                "Местами снегопад",
                "Местами легкий дождь",
                "Легкий туман",
                "Ясно",
                "Облачно",
                "Небольшая облачность",
                "Солнечно"};
        String[] cods = new String[]{"395", "392", "389", "386", "377","374","371", "368", "365", "362","359","356","353","350","338","335", "332","329","326","323","320","317","314","311","308","305","302","299","296","293","284","281","266","263","260","248","230","227","200","185","182","179","176","143","122","119","116","113"};
        int[] imgs = new int[]{R.drawable.a12, R.drawable.a10, R.drawable.a1, R.drawable.a1, R.drawable.a13, R.drawable.a13, R.drawable.a12, R.drawable.a10, R.drawable.a3, R.drawable.a3,
                R.drawable.a8, R.drawable.a8, R.drawable.a7, R.drawable.a7, R.drawable.a13,
                R.drawable.a12, R.drawable.a12, R.drawable.a10, R.drawable.a10, R.drawable.a9, R.drawable.a9,
                R.drawable.a3, R.drawable.a3, R.drawable.a3, R.drawable.a4, R.drawable.a8, R.drawable.a8,
                R.drawable.a7, R.drawable.a7, R.drawable.a7, R.drawable.a3,
                R.drawable.a3, R.drawable.a6, R.drawable.a6, R.drawable.a14, R.drawable.a15, R.drawable.a11,
                R.drawable.a11, R.drawable.a1, R.drawable.a15, R.drawable.a3, R.drawable.a9,
                R.drawable.a6, R.drawable.a14, R.drawable.a25, R.drawable.a21, R.drawable.a23, R.drawable.a25};
        ContentValues cv;
        for (int i = 0; i < 48; ++i) {
            cv = new ContentValues();
            cv.put(WEATHER, wthrs[i]); cv.put(CODE, cods[i]); cv.put(IMAGE, imgs[i]);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public void check() {
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() <= 0) insert();
    }

    public int getImage(String code) {
        check();
        Cursor cursor = db.query(TABLE_NAME, null, CODE + " = ?", new String[]{code}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) return R.drawable.a1;
        return cursor.getInt(cursor.getColumnIndex(IMAGE));
    }
    public String getWeather(String code) {
        check();
        Cursor cursor = db.query(TABLE_NAME, null, CODE + " = ?", new String[]{code}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) return "BE HAPPY!";
        return cursor.getString(cursor.getColumnIndex(WEATHER));
    }
    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
