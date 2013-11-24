package com.polarnick.day09.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.polarnick.day09.entities.ForecastData;

import java.sql.SQLException;
import java.util.List;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ForecastDataDAO extends BaseDaoImpl<ForecastData, Integer> {

    protected ForecastDataDAO(ConnectionSource connectionSource, Class<ForecastData> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}