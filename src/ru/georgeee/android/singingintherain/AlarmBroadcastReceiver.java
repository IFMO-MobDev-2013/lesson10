package ru.georgeee.android.singingintherain;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.georgeee.android.singingintherain.model.City;
import ru.georgeee.android.singingintherain.model.DatabaseHelperHolder;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 25.11.13
 * Time: 0:01
 * To change this template use File | Settings | File Templates.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        List<City> cities = City.loadAllFromDB();
        for(City city : cities){
            Intent nextIntent = new Intent(context, UpdateForecastService.class);
            nextIntent.putExtra(UpdateForecastService.ARGUMENT_CITY, city) ;
            nextIntent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startService(nextIntent);
        }
    }
}