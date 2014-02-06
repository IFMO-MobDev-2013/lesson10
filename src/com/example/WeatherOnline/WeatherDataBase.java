package com.example.WeatherOnline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 03.02.14
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public class WeatherDataBase extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_NAME = "weather";
    public static final String CITY_LIST = "cities";
    public static final String CITY_NAME = "city_name";

    public static final String WEATHER_DESCRIPTION = "description";
    public static final String W1 = "w1";
    public static final String W2 = "w2";
    public static final String W3 = "w3";
    private static final String W4 = "w4";


    public WeatherDataBase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    public boolean isEmpty(){
        SQLiteDatabase liteDatabase = this.getReadableDatabase();
        if(liteDatabase == null) {
            liteDatabase.close();
            return true;
        }
        else{
            try{
                Cursor cursor = liteDatabase.query(CITY_LIST, null, null, null, null, null, null);
                if(cursor.getCount() == 0){
                    cursor.close();
                    liteDatabase.close();
                    return true;
                }
                cursor.close();
                liteDatabase.close();
            }catch (Exception e){
                return true;
            }finally {
                liteDatabase.close();
            }
            return false;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int nw){
    }

    public boolean addCity(String city, String weather, Bitmap w1, Bitmap w2, Bitmap w3, Bitmap w4){
        SQLiteDatabase liteDatabase = this.getWritableDatabase();
        liteDatabase.execSQL("DELETE FROM " + CITY_LIST + " WHERE " + CITY_NAME + " = '" + city + "'");
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + city);
        liteDatabase.execSQL("CREATE TABLE " + city +
                " (" + WeatherDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                W1 + " BLOB, " + W2 + " BLOB, " + W3 + " BLOB, " + W4 + " BLOB, " + WEATHER_DESCRIPTION + " TEXT);");
        ContentValues values = new ContentValues();
        values.put(CITY_NAME, city);
        liteDatabase.insert(CITY_LIST, null, values);
        if(weather == null)
            return true;
        values = new ContentValues();
        values.put(WEATHER_DESCRIPTION, weather);
        Converter.putBitmapInConvertValue(values, w1, W1);
        Converter.putBitmapInConvertValue(values, w2, W2);
        Converter.putBitmapInConvertValue(values, w3, W3);
        Converter.putBitmapInConvertValue(values, w4, W4);
        liteDatabase.insert(city, null, values);
        liteDatabase.close();
        return true;
    }

    public void deleteCity(String city){
        SQLiteDatabase liteDatabase = this.getWritableDatabase();
        liteDatabase.execSQL("DELETE FROM " + CITY_LIST + " WHERE " + CITY_NAME + " = '" + city + "'");
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + city);
        liteDatabase.close();
    }

    public ArrayList<String> getCityList(){
        ArrayList<String> response = new ArrayList<String>();
        SQLiteDatabase liteDatabase = this.getReadableDatabase();
        Cursor cursor = liteDatabase.query(CITY_LIST, null, null, null , null, null, null);
        while(cursor.moveToNext()){
            response.add(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
        }
        cursor.close();
        liteDatabase.close();
        return response;
    }

    public String getCityWeather(String city){
        String response = null;
        SQLiteDatabase liteDatabase = this.getReadableDatabase();
        Cursor cursor = liteDatabase.query(city, null, null, null , null, null, null);
        while(cursor.moveToNext()){
            response = cursor.getString(cursor.getColumnIndex(WEATHER_DESCRIPTION));
        }
        cursor.close();
        liteDatabase.close();
        return response;
    }

    public void createCityTable(){
        SQLiteDatabase liteDatabase = this.getWritableDatabase();
        liteDatabase.execSQL("DROP TABLE IF EXISTS " + CITY_LIST);
        liteDatabase.execSQL("CREATE TABLE " + CITY_LIST +
                " (" + WeatherDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CITY_NAME + " TEXT);");
        liteDatabase.close();
    }

    public ArrayList<Bitmap> getPic(String city){
        ArrayList<Bitmap> response = new ArrayList<Bitmap>();
        SQLiteDatabase liteDatabase = this.getReadableDatabase();
        Cursor cursor = liteDatabase.query(city, null, null, null , null, null, null);
        cursor.moveToNext();
        response.add(Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                    cursor.getBlob(cursor.getColumnIndex(W1))),
                    64, 64, Bitmap.Config.ARGB_8888
            ));
        response.add(Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                cursor.getBlob(cursor.getColumnIndex(W2))),
                64, 64, Bitmap.Config.ARGB_8888
        ));
        response.add(Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                cursor.getBlob(cursor.getColumnIndex(W3))),
                64, 64, Bitmap.Config.ARGB_8888
        ));
        response.add(Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                cursor.getBlob(cursor.getColumnIndex(W4))),
                64,64, Bitmap.Config.ARGB_8888
        ));

        cursor.close();
        liteDatabase.close();
        return response;
    }
}
