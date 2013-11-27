package ru.georgeee.android.singingintherain.model;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseHelperHolder {

    private static DatabaseHelper databaseHelper = null;

    //gets a helper once one is created ensures it doesn't create a new one
    public static DatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public static DatabaseHelper getHelper() {
        return databaseHelper;
    }

    //releases the helper once usages has ended
    public static void releaseHelper() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

}