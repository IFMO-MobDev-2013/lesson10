package com.mikhov.Weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Database {
    private static final String DATABASE_TABLE = "towns";
    private static final String DATABASE_TABLE_YA = "yandex";
    private static final String DATABASE_TABLE_C = "c";

    public static final String COL_ID_YA = "_id";
    public static final String COL_CODE_YA = "code";
    public static final String COL_TOWN_YA = "town";

    public static final String COL_ID_C = "_id";
    public static final String COL_C_C = "c";

    public static final String COL_ID = "_id";
    public static final String COL_CODE = "code";
    static final String COL_TOWN = "town";
    static final String COL_TEMPERATURE1 = "temperature1";
    static final String COL_WEATHER1 = "weather1";
    static final String COL_PRESSURE1 = "pressure1";
    static final String COL_SUNRISE1 = "sunrise1";
    static final String COL_SUNSET1 = "sunset1";
    static final String COL_IMAGE1 = "image1";
    static final String COL_IMG1 = "img1";
    static final String COL_TEMPERATURE2 = "temperature2";
    static final String COL_WEATHER2 = "weather2";
    static final String COL_PRESSURE2 = "pressure2";
    static final String COL_SUNRISE2 = "sunrise2";
    static final String COL_SUNSET2 = "sunset2";
    static final String COL_IMAGE2 = "image2";
    static final String COL_IMG2 = "img2";
    static final String COL_TEMPERATURE3 = "temperature3";
    static final String COL_WEATHER3 = "weather3";
    static final String COL_PRESSURE3 = "pressure3";
    static final String COL_SUNRISE3 = "sunrise3";
    static final String COL_SUNSET3 = "sunset3";
    static final String COL_IMAGE3 = "image3";
    static final String COL_IMG3 = "img3";

    private static final String[] STR_TOWNS = {
            COL_ID, COL_CODE, COL_TOWN,
            COL_TEMPERATURE1, COL_WEATHER1,COL_PRESSURE1,
            COL_SUNRISE1, COL_SUNSET1,
            COL_IMAGE1, COL_IMG1, COL_TEMPERATURE2,
            COL_WEATHER2, COL_PRESSURE2,
            COL_SUNRISE2, COL_SUNSET2,
            COL_IMAGE2, COL_IMG2, COL_TEMPERATURE3,
            COL_WEATHER3, COL_PRESSURE3,
            COL_SUNRISE3, COL_SUNSET3,
            COL_IMAGE3, COL_IMG3 };

    private static final String[] STR_TOWNS_YA = { COL_ID_YA, COL_CODE_YA, COL_TOWN_YA };
    private static final String[] STR_C = { COL_ID_C, COL_C_C };

    private Context context;
    private DbHelper databaseHelper;
    private SQLiteDatabase database;


    public Database(Context ctx) {
        this.context = ctx;
    }

    public Database open() throws SQLException {
        databaseHelper = new DbHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public boolean isNotEmpty() {
        Cursor cursor = getAllData();
        return cursor.moveToNext();
    }
    public boolean yaIsNotEmpty() {
        return database.query(DATABASE_TABLE_YA, STR_TOWNS_YA, null, null, null, null, null).moveToNext();
    }
    public boolean cIsNotEmpty() {
        return database.query(DATABASE_TABLE_C, STR_C, null, null, null, null, null).moveToNext();
    }

    public void drop() {
        database.execSQL("DROP TABLE IF EXISTS towns");
        database.execSQL("create table towns (_id integer primary key autoincrement, code text, town text, " +
                "temperature1 text, weather1 text, sunrise1 text, sunset1 text, pressure1 text, image1 integer, img1 blob, " +
                "temperature2 text, weather2 text, sunrise2 text, sunset2 text, pressure2 text, image2 integer, img2 blob, " +
                "temperature3 text, weather3 text, sunrise3 text, sunset3 text, pressure3 text, image3 integer, img3 blob);");
    }

    public void dropYandex() {
        database.execSQL("DROP TABLE IF EXISTS yandex");
        database.execSQL("create table yandex (_id integer primary key autoincrement, code text, town text);");
    }

    public Cursor getAllData() {
        return database.query(DATABASE_TABLE, STR_TOWNS, null, null, null, null, null);
    }
    public Cursor getC() {
        return database.query(DATABASE_TABLE_C, STR_C, null, null, null, null, null);
    }
    public Cursor getYaInfo(String town) {
        return database.query(DATABASE_TABLE_YA, STR_TOWNS_YA, "town LIKE '%" + town + "%'", null, null, null, null);
    }
    public Cursor getAllYaInfo() {
        return database.query(DATABASE_TABLE_YA, STR_TOWNS_YA, null, null, null, null, null);
    }
    public String getYaCode(String town) {
        Cursor cursor =  database.query(DATABASE_TABLE_YA, STR_TOWNS_YA, "town = '" + town + "'", null, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(Database.COL_CODE_YA));
    }

    public boolean uniqueTown(String town) {
        return !database.query(DATABASE_TABLE, STR_TOWNS, "town = '" + town + "'", null, null, null, null).moveToNext();
    }
    public boolean existsTown(String town) {
        return database.query(DATABASE_TABLE_YA, STR_TOWNS_YA, "town = '" + town + "'", null, null, null, null).moveToNext();
    }

    public void updateTown(String town,
                        String temperature1,
                        String weather1,
                        String pressure1,
                        String sunrise1,
                        String sunset1,
                        int image1,
                        Bitmap img1,
                        String temperature2,
                        String weather2,
                        String pressure2,
                        String sunrise2,
                        String sunset2,
                        int image2,
                        Bitmap img2,
                        String temperature3,
                        String weather3,
                        String pressure3,
                        String sunrise3,
                        String sunset3,
                        int image3,
                        Bitmap img3) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TOWN, town);
        cv.put(COL_TEMPERATURE1, temperature1);
        cv.put(COL_WEATHER1, weather1);
        cv.put(COL_SUNRISE1, sunrise1);
        cv.put(COL_SUNSET1, sunset1);
        cv.put(COL_PRESSURE1, pressure1);
        cv.put(COL_IMAGE1, image1);

        ByteArrayOutputStream st = new ByteArrayOutputStream();
        img1.compress(Bitmap.CompressFormat.PNG, 100, st);
        byte[] bm_b1 = st.toByteArray();
        cv.put(COL_IMG1, bm_b1);

        cv.put(COL_TEMPERATURE2, temperature2);
        cv.put(COL_WEATHER2, weather2);
        cv.put(COL_SUNRISE2, sunrise2);
        cv.put(COL_SUNSET2, sunset2);
        cv.put(COL_PRESSURE2, pressure2);
        cv.put(COL_IMAGE2, image2);

        ByteArrayOutputStream st2 = new ByteArrayOutputStream();
        img2.compress(Bitmap.CompressFormat.PNG, 100, st2);
        byte[] bm_b2 = st2.toByteArray();
        cv.put(COL_IMG2, bm_b2);

        cv.put(COL_TEMPERATURE3, temperature3);
        cv.put(COL_WEATHER3, weather3);
        cv.put(COL_SUNRISE3, sunrise3);
        cv.put(COL_SUNSET3, sunset3);
        cv.put(COL_PRESSURE3, pressure3);
        cv.put(COL_IMAGE3, image3);

        ByteArrayOutputStream st3 = new ByteArrayOutputStream();
        img3.compress(Bitmap.CompressFormat.PNG, 100, st3);
        byte[] bm_b3 = st3.toByteArray();
        cv.put(COL_IMG3, bm_b3);

        database.update(DATABASE_TABLE, cv, COL_ID + "=" + getTownId(town), null);
    }

    public void addTown(String code, String town) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CODE, code);
        cv.put(COL_TOWN, town);
        byte[] b = null;
        cv.put(COL_IMG1, b);
        database.insert(DATABASE_TABLE, null, cv);
    }
    public void addYaTown(String code, String town) {
        ContentValues cv = new ContentValues();
        cv.put(COL_CODE_YA, code);
        cv.put(COL_TOWN_YA, town);
        database.insert(DATABASE_TABLE_YA, null, cv);
    }

    public void fillC() {
        ContentValues cv = new ContentValues();
        cv.put(COL_C_C, "true");
        database.insert(DATABASE_TABLE_C, null, cv);
    }
    public void clearC() {
        database.delete(DATABASE_TABLE_C, COL_C_C + " = 'true'", null);
    }

    public Cursor getTownInfo(long id) {
        return database.query(DATABASE_TABLE, STR_TOWNS, "_id = " + id, null, null, null, null);
    }
    public long getTownId(String town) {
        Cursor res = database.query(DATABASE_TABLE, STR_TOWNS, "town = '" + town + "'", null, null, null, null);
        res.moveToNext();
        return res.getLong(res.getColumnIndex(COL_ID));
    }
    public String getTownById(String id) {
        Cursor res = database.query(DATABASE_TABLE, STR_TOWNS, "code = '" + id + "'", null, null, null, null);
        res.moveToNext();
        return res.getString(res.getColumnIndex(COL_TOWN));
    }
    public byte[] getImageByTownId(long id) {
        Cursor res = database.query(DATABASE_TABLE, STR_TOWNS, "_id = '" + id + "'", null, null, null, null);
        res.moveToNext();
        return res.getBlob(res.getColumnIndex(COL_IMG1));
    }
    public void deleteTown(long id) {
        database.delete(DATABASE_TABLE, COL_ID + "=" + id, null);
    }
}
