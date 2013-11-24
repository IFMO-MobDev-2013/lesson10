package com.polarnick.day09.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastData;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "forecasts.db";

    private static final int DATABASE_VERSION = 239;

    private CityDAO cityDAO = null;
    private ForecastForCityDAO forecastForCityDAO = null;
    private ForecastDataDAO forecastDataDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ForecastData.class);
            TableUtils.createTable(connectionSource, ForecastForCity.class);
            TableUtils.createTable(connectionSource, City.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Error creating DB " + DATABASE_NAME + "!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, City.class, true);
            TableUtils.dropTable(connectionSource, ForecastForCity.class, true);
            TableUtils.dropTable(connectionSource, ForecastData.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Error upgrading db " + DATABASE_NAME + " to version " + newVer
                    + " from ver " + oldVer + "!");
            throw new RuntimeException(e);
        }
    }

    public CityDAO getCityDAO() {
        if (cityDAO == null) {
            try {
                cityDAO = new CityDAO(getConnectionSource(), City.class);
            } catch (SQLException e) {
                Log.e(DatabaseHelper.class.getName(), "Error creating CityDAO!");
                throw new RuntimeException(e);
            }
        }
        return cityDAO;
    }

    public ForecastForCityDAO getForecastForCityDAO() {
        if (forecastForCityDAO == null) {
            try {
                forecastForCityDAO = new ForecastForCityDAO(getConnectionSource(), ForecastForCity.class);
            } catch (SQLException e) {
                Log.e(DatabaseHelper.class.getName(), "Error creating ForecastForCityDAO!");
                throw new RuntimeException(e);
            }
        }
        return forecastForCityDAO;
    }

    public ForecastDataDAO getForecastDataDAO() {
        if (forecastDataDAO == null) {
            try {
                forecastDataDAO = new ForecastDataDAO(getConnectionSource(), ForecastData.class);
            } catch (SQLException e) {
                Log.e(DatabaseHelper.class.getName(), "Error creating ForecastDataDAO!");
                throw new RuntimeException(e);
            }
        }
        return forecastDataDAO;
    }

    @Override
    public void close() {
        super.close();
        cityDAO = null;
        forecastDataDAO = null;
        forecastForCityDAO = null;
    }
}