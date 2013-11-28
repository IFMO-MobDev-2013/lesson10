package ru.georgeee.android.singingintherain.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 24.11.13
 * Time: 23:36
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper  extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 8;

    // the DAO object we use to access the SimpleData table
    private Dao<City, Integer> cityDao = null;
    private RuntimeExceptionDao<City, Integer> cityRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getCanonicalName(), "onCreate");
            TableUtils.createTable(connectionSource, City.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getCanonicalName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        City spb = new City();
        spb.setName("SPB");
        spb.setLatitude("59.941");
        spb.setLongitude("30.3570167");
        spb.save();
//        // here we try inserting data in the on-create as a test
//        RuntimeExceptionDao<City, Integer> dao = getCitiesDataDao();
//        // create some entries in the onCreate
//        City city = new City("First Test City");
//        dao.create(city);
//        Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate");
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, City.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<City, Integer> getCitiesDao() throws SQLException {
        if (cityDao == null) {
            cityDao = getDao(City.class);
        }
        return cityDao;
    }

    public RuntimeExceptionDao<City, Integer> getCitiesDataDao() {
        if (cityRuntimeDao == null) {
            cityRuntimeDao = getRuntimeExceptionDao(City.class);
        }
        return cityRuntimeDao;
    }


    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        cityDao = null;
        cityRuntimeDao = null;

    }
}
