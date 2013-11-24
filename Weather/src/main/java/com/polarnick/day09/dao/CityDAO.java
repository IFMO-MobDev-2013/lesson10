package com.polarnick.day09.dao;

import android.util.Log;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.polarnick.day09.entities.City;

import java.sql.SQLException;
import java.util.List;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class CityDAO extends BaseDaoImpl<City, Integer> {

    protected CityDAO(ConnectionSource connectionSource, Class<City> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public synchronized List<City> getAllCities() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            Log.e(CityDAO.class.getName(), "Error getting all cities!");
            throw new RuntimeException(e);
        }
    }
}