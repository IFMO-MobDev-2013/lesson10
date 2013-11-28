package ru.georgeee.android.singingintherain.misc;

import android.widget.BaseExpandableListAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 28.11.13
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
abstract public class DetailExpandableListAdapter<T> extends BaseExpandableListAdapter {

    protected ArrayList<T> dataPoints;

    public DetailExpandableListAdapter() {
        dataPoints = new ArrayList<T>();
    }

    @Override
    public int getGroupCount() {
        return dataPoints.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dataPoints.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void clear() {
        dataPoints.clear();
    }

    public void addAll(List<T> dataPoints) {
        this.dataPoints.addAll(dataPoints);
    }
}