package com.polarnick.day09.dao;

import android.util.Log;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.polarnick.day09.entities.City;
import com.polarnick.day09.entities.ForecastForCity;

import java.sql.SQLException;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ForecastForCityDAO extends BaseDaoImpl<ForecastForCity, Integer> {

    protected ForecastForCityDAO(ConnectionSource connectionSource, Class<ForecastForCity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public synchronized ForecastForCity getForecastForCity(City city) {
        try {
            return this.queryForId(city.getId());
        } catch (SQLException e) {
            Log.e(ForecastForCityDAO.class.getName(), "Error forecast for city with id=" + city.getId() + "!");
            throw new RuntimeException(e);
        }
    }
}