package ru.zulyaev.ifmo.zeather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author seidhe
 */
public class CityDeletionDialogFragment extends DialogFragment {
    private static final DialogInterface.OnClickListener NO_OP_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    };

    private Context context;
    private final WeatherForecast forecast;
    private final CityDeletionListener listener;

    public CityDeletionDialogFragment(WeatherForecast forecast, CityDeletionListener listener) {
        this.forecast = forecast;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.deletion_confirmation, forecast.getLocation()))
                    .setNeutralButton(R.string.cancel, NO_OP_LISTENER)
                    .setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onDelete(forecast);
                        }
                    })
                    .create();
    }

    @Override
    public void onAttach(Activity activity) {
        this.context = activity;
        super.onAttach(activity);
    }

    interface CityDeletionListener {
        void onDelete(WeatherForecast forecast);
    }
}
