package com.example.weather;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 17.11.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class DbAdapter implements DbConstants {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    public static final int DATABASE_VERSION = 5;

    public static final String TAG = "FUCKENFUCK: ";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_CITIES);
            db.execSQL(CREATE_TABLE_FORECAST);
            db.execSQL(CREATE_TABLE_CURRENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT);
            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            try {
                mDb = mDbHelper.getReadableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                return this;
            }
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //-------------------------------------------------------------------------------------------
    public void dropCurrent() {
        try {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT);
            mDb.execSQL(CREATE_TABLE_CURRENT);
        } catch (Exception e) {
            Log.w(TAG, "can't drop CURRENT - " + e.getMessage());
        }
    }

    public void dropCities() {
        try {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
            mDb.execSQL(CREATE_TABLE_CITIES);
        } catch (Exception e) {
            Log.w(TAG, "can't drop CITIES - " + e.getMessage());
        }
    }

    public void dropForecast() {
        try {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
            mDb.execSQL(CREATE_TABLE_FORECAST);
        } catch (Exception e) {
            Log.w(TAG, "can't drop FORECAST - " + e.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------
    // CITY:
    public Cursor fetchCity() {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_CITIES, null, null, null, null, null, null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

    public boolean createCity(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, h.get(KEY_NAME));
        initialValues.put(KEY_LAT, h.get(KEY_LAT));
        initialValues.put(KEY_LONG, h.get(KEY_LONG));
        try {
            return mDb.insertOrThrow(TABLE_CITIES, null, initialValues) > 0;
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
            return false;
        }
    }

    public boolean deleteCity(long rowId) {
        return mDb.delete(TABLE_CITIES, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteCity(String name) {
        return deleteCity(getCityId(name));
    }


    public long getCityId(String name) {
        try {
            Cursor cursor = mDb.query(TABLE_CITIES, new String[]{KEY_ROWID}, KEY_NAME + " =? ", new String[]{name}, null, null, null, "1");
            if (cursor == null)
                return -1;
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(KEY_ROWID));
        } catch (Exception e) {
            Log.w(TAG, "getCityId Failed!! " + e.getMessage().toString());
            return -1;
        }
    }
     //  /CITY. works correct
    //---------------------------------------------------------------------------------------------------
    // CURRENT
     public Cursor fetchCurrent() {
         Cursor cursor = null;
         try {
             cursor = mDb.query(TABLE_CURRENT, null, null, null, null, null, null);
         } catch (Exception e) {
             Log.w(TAG, getClass().getName());
         } finally {
             return cursor;
         }
     }

    public boolean createCurrent(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TEMPC, h.get(KEY_TEMPC));
        initialValues.put(KEY_OBSTIME, h.get(KEY_OBSTIME));
        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHICON, h.get(KEY_WTHICON));
        //initialValues.put(KEY_DATE, h.get(KEY_DATE));
        initialValues.put(KEY_CITYID, getCityId(h.get(KEY_NAME)));

        try {
            return mDb.insertOrThrow(TABLE_CURRENT, null, initialValues) > 0;
        } catch (Exception e) {
            Log.w(TAG, " createCurrent problem "+ e.getMessage());
            return false;
        }
    }

    public boolean deleteCurrent(long cityId) {
        return mDb.delete(TABLE_CURRENT, KEY_CITYID + "=" + cityId, null) > 0;
    }

    public boolean updateCurrent(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TEMPC, h.get(KEY_TEMPC));
        initialValues.put(KEY_OBSTIME, h.get(KEY_OBSTIME));
        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHICON, h.get(KEY_WTHICON));
        //initialValues.put(KEY_DATE, h.get(KEY_DATE));
        long city_id = getCityId(h.get(KEY_NAME));
        return mDb.update(TABLE_CURRENT, initialValues, KEY_CITYID + " = " + city_id, null) > 0;
    }

    public boolean deleteCurrent(String city_name) {
        return deleteCurrent(getCityId(city_name));
    }

    // /CURRENT     correct
    //----------------------------------------------------------------------------------------
    // FORECAST
    private Cursor fetchForecast() {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_FORECAST, null, null, null, null, null, null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

    public Cursor fetchForecast(String name) {
        if(name == null)
            return fetchForecast();
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_FORECAST, null, KEY_CITYID + " = " + getCityId(name), null, null, null, null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }


    public boolean createForecast(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TEMPMAXC, h.get(KEY_TEMPMAXC));
        initialValues.put(KEY_TEMPMINC, h.get(KEY_TEMPMINC));

        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        //initialValues.put(KEY_HUMID, h.get(KEY_HUMID));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHICON, h.get(KEY_WTHICON));
        initialValues.put(KEY_DATE, h.get(KEY_DATE));
        initialValues.put(KEY_CITYID, getCityId(h.get(KEY_NAME)));

        try {
            return mDb.insertOrThrow(TABLE_FORECAST, null, initialValues) > 0;
        } catch (Exception e) {
            Log.w(TAG, " createCurrent problem "+ e.getMessage());
            return false;
        }
    }

    public boolean deleteForecast(long cityId) {
        return mDb.delete(TABLE_CURRENT, KEY_CITYID + "=" + cityId, null) > 0;
    }

    public boolean updateForecast(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TEMPMAXC, h.get(KEY_TEMPMAXC));
        initialValues.put(KEY_TEMPMINC, h.get(KEY_TEMPMINC));

        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHICON, h.get(KEY_WTHICON));
        initialValues.put(KEY_DATE, h.get(KEY_DATE));

        long city_id = getCityId(h.get(KEY_NAME));
        return mDb.update(TABLE_FORECAST, initialValues, KEY_CITYID + " = " + city_id, null) > 0;
    }

    public boolean deleteForecast(String city_name) {
        return deleteForecast(getCityId(city_name));
    }



    // /FORECAST   not tested!!!    I suppose it's ok

    //-------------------------------------------------------------------------------------------------------
}/*


//    public boolean createCurrent(HashMap<String, String> h) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_CITY, h.get(KEY_CITY));
//        initialValues.put(KEY_COUNTRY, h.get(KEY_COUNTRY));
//        initialValues.put(KEY_TEMPC, h.get(KEY_TEMPC));
//        initialValues.put(KEY_TEMPF, h.get(KEY_TEMPF));
//        initialValues.put(KEY_WSM, h.get(KEY_WSM));
//        initialValues.put(KEY_WSK, h.get(KEY_WSK));
//        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
//        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
//        try {
//            boolean b = mDb.insert(TABLE_CURRENT, null, initialValues) > 0;
//            if (b) {
//                return b;
//            } else {
//                Log.w(TAG, getClass().getName());
//                return b;
//            }
//        } catch (Exception e) {
//            Log.w(TAG, getClass().getName());
//            return false;
//        }
//    }
//
//    public Cursor fetchCurrent() {
//        Cursor cursor = null;
//        try {
//            cursor = mDb.query(TABLE_CURRENT, null, null, null, null, null, null);
//        } catch (Exception e) {
//            Log.w(TAG, getClass().getName());
//        } finally {
//            return cursor;
//        }
//    }

    public Cursor fetchCurrent(Integer id) {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_CURRENT, null,KEY_ROWID + " = " + id.toString(),null,null,null,null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

    public long fetchCurrentID(String city, String country) {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_CURRENT, new String[]{KEY_ROWID}, KEY_CITY + "=? AND " + KEY_COUNTRY + "=? ",new String[] {city, country},null,null,null);
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(KEY_ROWID));
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
            return -1;
        }
    }


//    public void dropCities() {
//        try {
//            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
//            mDb.execSQL(CREATE_TABLE_CITIES);
//        } catch (Exception e) {
//
//            Log.w(TAG, getClass().getName());
//        }
//    }

//    public void dropCurCond() {
//        try {
//            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_CURCOND);
//            mDb.execSQL(CREATE_TABLE_CURCOND);
//        } catch (Exception e) {
//            Log.w(TAG, getClass().getName());
//        }
//    }

    public void dropWeather() {
        try {
            mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
            mDb.execSQL(CREATE_TABLE_WEATHER);
        } catch (Exception e) {
            Log.w(TAG, "CAN'T CREATE!!!!!" );
        }
    }


//    public boolean createCity(String city, String country) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_CITY, city);
//        initialValues.put(KEY_COUNTRY, country);
//
//        try {
//            return mDb.insert(TABLE_CITIES, null, initialValues) > 0;
//        } catch (Exception e) {
//            Log.w(TAG, getClass().getName());
//            return false;
//        }
//    }

    public boolean createWeather(HashMap<String, String> h) {
        ContentValues initialValues = new ContentValues();

        int city_id = (int)fetchCurrentID(h.get(KEY_CITY), h.get(KEY_COUNTRY));
        if(city_id <0)
        {
            createCurrent(h);
            city_id = (int)fetchCurrentID(h.get(KEY_CITY), h.get(KEY_COUNTRY));
        }

        initialValues.put(KEY_CITYID, city_id);
        //initialValues.put(KEY_COUNTRY, h.get(KEY_COUNTRY));
        //initialValues.put(KEY_CITY, h.get(KEY_CITY));
        initialValues.put(KEY_DATE, h.get(KEY_DATE));
        initialValues.put(KEY_TEMPMAXC, h.get(KEY_TEMPMAXC));
        initialValues.put(KEY_TEMPMINC, h.get(KEY_TEMPMINC));
        initialValues.put(KEY_TEMPMAXF, h.get(KEY_TEMPMAXF));
        initialValues.put(KEY_TEMPMINF, h.get(KEY_TEMPMINF));
        initialValues.put(KEY_WSM, h.get(KEY_WSM));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        initialValues.put(KEY_WDD, h.get(KEY_WDD));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));

        try {
            long b = mDb.insert(TABLE_WEATHER, null, initialValues);
            return  b>0;
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
            return false;
        }
    }


    public boolean updateCurrent(HashMap<String, String> h)
    {
        ContentValues initialValues = new ContentValues();
        Integer id = (int)fetchCurrentID(h.get(KEY_CITY), h.get(KEY_COUNTRY));

        if(id<0)
            return createCurrent(h);
        initialValues.put(KEY_TEMPC, h.get(KEY_TEMPC));
        initialValues.put(KEY_TEMPF, h.get(KEY_TEMPF));
        initialValues.put(KEY_WSM, h.get(KEY_WSM));
        initialValues.put(KEY_WSK, h.get(KEY_WSK));
        initialValues.put(KEY_WTHCODE, h.get(KEY_WTHCODE));
        initialValues.put(KEY_WTHDESC, h.get(KEY_WTHDESC));
        return mDb.update(TABLE_CURRENT, initialValues, KEY_ROWID+ " = " + id.toString(), null) > 0;
    }

    public Cursor fetchAllCitiesNames()
    {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_CURRENT, new String[] {KEY_ROWID, KEY_CITY, KEY_COUNTRY},null,null,null,null,null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

    public Cursor fetchForecast(Integer id)
    {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_WEATHER, new String[] {KEY_ROWID, KEY_TEMPMINC, KEY_TEMPMAXC, KEY_DATE, KEY_WTHDESC},
                    KEY_CITYID + " = " + id.toString(),null,null,null, KEY_DATE + "ASC");
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

    public Cursor fetchMainInfo(Integer id)
    {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_CURRENT, new String[] {KEY_ROWID, KEY_TEMPC, KEY_WSK, KEY_WTHDESC},
                    KEY_ROWID + " = " + id.toString(),null,null,null, null);
        } catch (Exception e) {
            Log.w(TAG, getClass().getName());
        } finally {
            return cursor;
        }
    }

//    public boolean editFeed(int rowID, String name, String link) {
//        ContentValues initialValues = new ContentValues();
//
//        if (rowID > 0) initialValues.put(KEY_ROWID, rowID);
//        if (name != null && name != "") initialValues.put(KEY_NAME, name);
//        if (name != null && link != "") initialValues.put(KEY_LINK, link);
//        try {
//            return mDb.update(TABLE_FEEDS, initialValues, KEY_ROWID + " =?", new String[]{((Integer) rowID).toString()}) > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean deleteCity(long rowId) {
        return mDb.delete(TABLE_CURRENT, KEY_ROWID + "=" + rowId, null) > 0;
    }

//    public boolean deleteContent(int feed_id) {
//        return mDb.delete(TABLE_CONTENT, KEY_FEEDID + "=" + feed_id, null) > 0;
//    }


//    public Cursor fetchAllCities() {
//        Cursor cursor = null;
//        try {
//            cursor = mDb.query(TABLE_CITIES + ", " + TABLE_CURCOND, new String[]{KEY_ROWID, KEY_CITY, KEY_COUNTRY,
//                    KEY_TEMPC, KEY_TEMPF, KEY_WSM, KEY_WSK, KEY_WTHDESC},
//                    TABLE_CURCOND + "." + KEY_CITYID + " = " + TABLE_CITIES + "." + KEY_ROWID, null, null, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return cursor;
//        }
//    }

//    public Cursor fetchAllFeedsNamesAndLinks() {
//        Cursor cursor = null;
//        try {
//            cursor = mDb.query(TABLE_FEEDS, new String[]{KEY_ROWID, KEY_NAME, KEY_LINK}, null, null, null, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return cursor;
//        }
//    }
//
//    public Integer fetchFeedId(String s) {
//        Cursor cursor = null;
//        Integer res = null;
//        try {
//            if (s == null)
//                cursor = mDb.query(TABLE_FEEDS, new String[]{KEY_ROWID}, null, null, null, null, null);
//            else
//                cursor = mDb.query(TABLE_FEEDS, new String[]{KEY_ROWID}, KEY_NAME + " =?", new String[]{s}, null, null, null, null);
//
//            cursor.moveToNext();
//            res = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
//            cursor.close();
//
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return DEFAULT_ID;
//        } finally {
//            return res;
//        }
//    }
//
////        public boolean createContent(FeedItem fi, String feed) {
////            ContentValues initialValues = new ContentValues();
////            initialValues.put(KEY_TITLE, fi.getTitle());
////            initialValues.put(KEY_DATE, fi.getDate());
////            initialValues.put(KEY_DESC, fi.getDesc());
////            initialValues.put(KEY_LINK, fi.getLink());
////
////            Integer feed_id = fetchFeedId(feed);
////            initialValues.put(KEY_FEEDID, feed_id);
////
////            try {
////                return mDb.insert(TABLE_CONTENT, null, initialValues) > 0;
////            } catch (Exception e) {
////                e.printStackTrace();
////                return false;
////            }
////        }
//
//    public Cursor fetchArticles(Integer feed_id) {
//        Cursor cursor = null;
//        try {
//            if (feed_id == null) {
//                cursor = mDb.query(TABLE_CONTENT, new String[]{KEY_ROWID, KEY_TITLE, KEY_DATE}, null, null, null, null, null);
//            } else {
//                cursor = mDb.query(TABLE_CONTENT, new String[]{KEY_ROWID, KEY_TITLE, KEY_DATE}, KEY_FEEDID + " = " + feed_id, null, null, null, null);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return cursor;
//        }
//    }
//
//    public Cursor fetchContent(Integer content_id) throws SQLException {
//        Cursor cursor = null;
//        try {
//            cursor = mDb.query(TABLE_CONTENT, new String[]{KEY_ROWID, KEY_TITLE, KEY_DATE, KEY_DESC, KEY_LINK}, KEY_ROWID + "=" + content_id.toString(), null, null, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return cursor;
//        }
//    }
//
//    public Cursor fetchFeed(Integer cur_id) {
//        Cursor cursor = null;
//        try {
//            cursor = mDb.query(TABLE_FEEDS, new String[]{KEY_ROWID, KEY_NAME, KEY_LINK}, KEY_ROWID + "=" + cur_id.toString(), null, null, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            return cursor;
//        }
//    }
}

*/