package ru.georgeee.android.singingintherain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.georgeee.android.singingintherain.model.City;
import ru.georgeee.android.singingintherain.model.Forecast;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 2:29
 * To change this template use File | Settings | File Templates.
 */
abstract class ForecastFragment extends Fragment {

    City city;
    View rootView;

    public void setCity(City city) {
        this.city = city;
        update();
    }

    public void update() {
        if (rootView != null) onUpdateViewImpl();
    }

    protected abstract int getLayoutId();

    protected void onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        if (rootView != null) onUpdateViewImpl();
    }

    protected abstract void onUpdateViewImpl();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        onCreateViewImpl(inflater, container, savedInstanceState);
        return rootView;
    }



}