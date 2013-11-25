package com.polarnick.day09.entities;

import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Date: 18.11.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
@DatabaseTable(tableName = "forecastsForCity")
public class ForecastForCity {

    public static final int HOURS_COUNT = 3;
    public static final int[] HOURS_START_OFFSET = new int[]{0, 1, 3};
    public static final int[] HOURS_END_OFFSET = new int[]{1, 3, 7};
    public static final int[] HOURS_DIFF = new int[]{1, 2, 4};

    public static final int DAYS_COUNT = 7;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private long downloadedAt;
    @DatabaseField(foreign = true)
    private ForecastData current;
    @DatabaseField()
    private String hoursSummary;
    @ForeignCollectionField(eager = true)
    private ForeignCollection<ForecastData> data;
    @DatabaseField()
    private String daysSummary;

    public ForecastForCity() {
    }

    public ForecastForCity(long downloadedAt, ForecastData current, String hoursSummary, String daysSummary) {
        this.downloadedAt = downloadedAt;
        this.current = current;
        this.hoursSummary = hoursSummary;
        this.daysSummary = daysSummary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(long downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public ForecastData getCurrent() {
        return current;
    }

    public void setCurrent(ForecastData current) {
        this.current = current;
    }

    public String getHoursSummary() {
        return hoursSummary;
    }

    public void setHoursSummary(String hoursSummary) {
        this.hoursSummary = hoursSummary;
    }

    public String getDaysSummary() {
        return daysSummary;
    }

    public void setDaysSummary(String daysSummary) {
        this.daysSummary = daysSummary;
    }

    public ForeignCollection<ForecastData> getData() {
        return data;
    }

    public List<ForecastData> getHours() {
        List<ForecastData> data = getDataSortedByTime();
        List<ForecastData> hours = new ArrayList<ForecastData>(HOURS_COUNT);
        for (int i = 0; i < HOURS_COUNT; i++) {
            hours.add(data.get(i));
        }
        return hours;
    }

    public List<ForecastData> getDays() {
        List<ForecastData> data = getDataSortedByTime();
        List<ForecastData> days = new ArrayList<ForecastData>(DAYS_COUNT);
        for (int i = HOURS_COUNT; i < HOURS_COUNT + DAYS_COUNT; i++) {
            days.add(data.get(i));
        }
        return days;
    }

    private List<ForecastData> getDataSortedByTime() {
        ArrayList<ForecastData> dataArrayList = new ArrayList<ForecastData>(data);
        Preconditions.checkState(dataArrayList.size() == DAYS_COUNT + HOURS_COUNT);
        Collections.sort(dataArrayList, new Comparator<ForecastData>() {
            @Override
            public int compare(ForecastData forecast1, ForecastData forecast2) {
                if (forecast1.isHour() && !forecast2.isHour()) {
                    return -1;
                }
                if (!forecast1.isHour() && forecast2.isHour()) {
                    return 1;
                }
                if (forecast1.getTime() > forecast2.getTime()) {
                    return 1;
                }
                if (forecast1.getTime() < forecast2.getTime()) {
                    return -1;
                }
                return 0;
            }
        });
        return dataArrayList;
    }
}
