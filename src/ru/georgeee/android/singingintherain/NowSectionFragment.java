package ru.georgeee.android.singingintherain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import ru.georgeee.android.singingintherain.model.City;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 20.11.13
 * Time: 0:19
 * To change this template use File | Settings | File Templates.
 */
public class NowSectionFragment  extends CityDependentFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.now_section_fragment;
    }

    @Override
    protected void onUpdateViewImpl() {
    }

}