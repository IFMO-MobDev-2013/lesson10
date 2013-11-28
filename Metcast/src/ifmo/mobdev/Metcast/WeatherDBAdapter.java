package ifmo.mobdev.Metcast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static String DROP = " DROP TABLE IF EXISTS ";

    private static final String TAG = "WeatherDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "weatherdata";

    //----- cities ----
    private static final String CITIES_DATABASE_TABLE = "feeds";
    public static final String KEY_CITY = "city";
    public static final String KEY_COUNTRY = "country";
    private static final int DATABASE_VERSION = 2;
    private static final String CITIES_DATABASE_CREATE =
            "create table " + CITIES_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_CITY + " text not null unique, " + KEY_COUNTRY + " text not null);";
    //----today-----
    private static final String TODAY_DATABASE_TABLE = "today";
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DESCR = "descr";
    public static final String KEY_WIND = "wind";
    public static final String KEY_PRESS = "press";
    public static final String KEY_HUM = "hum";
    public static final String KEY_TEMP = "temp";
    public static final String KEY_ICON_ID = "icon_id";
    private static final String TODAY_DATABASE_CREATE =
            "create table " + TODAY_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_CITY_ID + " integer not null, " + KEY_ICON_ID + " integer not null, " + KEY_TEMP
                    + " text not null, " + KEY_DATE + " text not null, " + KEY_DESCR + " text not null, "
                    + KEY_WIND + " text not null, " + KEY_PRESS + " text not null, " + KEY_HUM + " text not null);";

    //----week------
    private static final String WEEK_DATABASE_TABLE = "week";
    public static final String KEY_MIN_TEMP = "min_temp";
    public static final String KEY_MAX_TEMP = "max_temp";
    private static final String WEEK_DATABASE_CREATE =
            "create table " + WEEK_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_CITY_ID + " text not null, " + KEY_DATE + " text not null, " + KEY_DESCR + " text not null, "
                    + KEY_MIN_TEMP + " text not null, " + KEY_ICON_ID + " integer not null, " + KEY_MAX_TEMP + " text not null);";

    //----selected----
    private static final String SELECTED_DATABASE_TABLE = "last_selected";
    public static final String KEY_SEL = "selected";
    private static final String SELECTED_DATABASE_CREATE =
            "create table " + SELECTED_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_SEL + " text not null);";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CITIES_DATABASE_CREATE);
            db.execSQL(TODAY_DATABASE_CREATE);
            db.execSQL(WEEK_DATABASE_CREATE);
            db.execSQL(SELECTED_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(DROP + CITIES_DATABASE_TABLE);
            db.execSQL(DROP + TODAY_DATABASE_TABLE);
            db.execSQL(DROP + WEEK_DATABASE_TABLE);
            db.execSQL(DROP + SELECTED_DATABASE_TABLE);
            onCreate(db);
        }
    }


    public WeatherDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public WeatherDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            try {
                mDb = mDbHelper.getReadableDatabase();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void drop() {
        mDb.execSQL(DROP + CITIES_DATABASE_TABLE);
        mDb.execSQL(DROP + TODAY_DATABASE_TABLE);
        mDb.execSQL(DROP + WEEK_DATABASE_TABLE);
        mDb.execSQL(DROP + SELECTED_DATABASE_TABLE);
        mDb.execSQL(CITIES_DATABASE_CREATE);
        mDb.execSQL(TODAY_DATABASE_CREATE);
        mDb.execSQL(WEEK_DATABASE_CREATE);
        mDb.execSQL(SELECTED_DATABASE_CREATE);
    }

    public boolean createLast(long id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SEL, Long.toString(id));
        return mDb.update(SELECTED_DATABASE_TABLE, initialValues, KEY_ROWID + "=" + 1, null) > 0;
    }

    public long createCity(String city, String country) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CITY, city);
        initialValues.put(KEY_COUNTRY, country);

        return mDb.insert(CITIES_DATABASE_TABLE, null, initialValues);
    }

    public long createToday(long city_id, String date, String descr, String wind, String press, String hum, String temp, String iconID) {
        if (date == null) {
            date = "2013-11-21";
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CITY_ID, city_id);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_DESCR, descr);
        initialValues.put(KEY_TEMP, temp);
        initialValues.put(KEY_WIND, wind);
        initialValues.put(KEY_PRESS, press);
        initialValues.put(KEY_HUM, hum);
        initialValues.put(KEY_ICON_ID, new WeatherPicture(iconID).getID());

        return mDb.insert(TODAY_DATABASE_TABLE, null, initialValues);
    }

    public long createWeek(long city_id, String date, String descr, String maxtemp, String mintemp, String iconID) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CITY_ID, city_id);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_DESCR, descr);
        initialValues.put(KEY_MAX_TEMP, maxtemp);
        initialValues.put(KEY_MIN_TEMP, mintemp);
        initialValues.put(KEY_ICON_ID, new WeatherPicture(iconID).getID());

         return mDb.insert(WEEK_DATABASE_TABLE, null, initialValues);
    }

    public long getCityIdByName(String name) {
        if (name == null) return -1;
        Cursor cursor = mDb.query(CITIES_DATABASE_TABLE, new String[] {KEY_ROWID},
                KEY_CITY + "=?", new String[] {name}, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_ROWID);
        cursor.moveToNext();
        long a = -1;
        try {
            a = cursor.getLong(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public long getLastUpdatedID() {
        Cursor cursor = mDb.query(SELECTED_DATABASE_TABLE, new String[] {KEY_SEL},
                KEY_ROWID + "=" + 1, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_SEL);
        cursor.moveToNext();
        long a = -1;
        try {
            a = Long.parseLong(cursor.getString(index));
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public String getCityCountryByID(long id) {
        Cursor cursor = mDb.query(CITIES_DATABASE_TABLE, new String[] {KEY_COUNTRY},
                KEY_ROWID + "=" + id, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_COUNTRY);
        cursor.moveToNext();
        String a = null;
        try {
            a = cursor.getString(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public String getCityNameByID(long id) {
        Cursor cursor = mDb.query(CITIES_DATABASE_TABLE, new String[] {KEY_CITY},
                KEY_ROWID + "=" + id, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_CITY);
        cursor.moveToNext();
        String a = null;
        try {
            a = cursor.getString(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public boolean deleteCity(long rowId) {
        mDb.delete(TODAY_DATABASE_TABLE, KEY_CITY_ID + "=" + rowId, null);
        mDb.delete(WEEK_DATABASE_TABLE, KEY_CITY_ID + "=" + rowId, null);
        return mDb.delete(CITIES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteToday(long rowId) {
        return mDb.delete(TODAY_DATABASE_TABLE, KEY_CITY_ID + "=" + rowId, null) > 0;
    }

    public boolean deleteWeek(long rowId) {
        return mDb.delete(WEEK_DATABASE_TABLE, KEY_CITY_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllCities() {
        return mDb.query(CITIES_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CITY,
                KEY_COUNTRY}, null, null, null, null, null);
    }

    public Cursor fetchCityToday(long city_id) {
        Cursor mCursor = mDb.query(TODAY_DATABASE_TABLE, new String[]
                {KEY_ROWID, KEY_CITY_ID, KEY_DATE, KEY_DESCR, KEY_WIND, KEY_PRESS, KEY_HUM, KEY_TEMP, KEY_ICON_ID},
                KEY_CITY_ID + "=" + city_id, null, null, null, null);
        return mCursor;
    }

    public Cursor fetchCityWeek(long city_id) {
        Cursor mCursor = mDb.query(WEEK_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CITY_ID, KEY_DATE,
                KEY_MAX_TEMP, KEY_MIN_TEMP, KEY_DESCR, KEY_ICON_ID},
                KEY_CITY_ID + "=" + city_id, null, null, null, null);
        return mCursor;
    }
}
