/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.skipor.weather;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import ru.skipor.RssReader.R;
import ru.skipor.weather.Forecast.ForecastException;
import ru.skipor.weather.Forecast.ForecastProvider;
import ru.skipor.weather.Forecast.WWOForecast;

public class CityEditActivity extends Activity {

    private static String TAG = "CityEditActivity";
    private MenuItem confirmButton;
    private EditText cityNameEditText;
    private TextView suitableNameTextView;
    private Long mRowId;
    private WeatherDatabaseHelper mDatabaseHelper;
    private static final String NO_SUITABLE_CITY_NAME_MESSAGE = "No city is suitable to entered name";
    private String lastValidName;
    private String lastCheckedName;
    private boolean suitableNameRecallEnabled;

    private static final int SUITABLE_NAME_TASK_SLEEP_MILLISECONDS = 300 ;


    private void startSuitableNameTask() {
        SuitableNameTask suitableNameTask = new SuitableNameTask();
        suitableNameTask.execute();


    }

    private String getEnteredName() {
        return cityNameEditText.getText().toString();
    }

    private class SuitableNameTask extends AsyncTask<Void, Void, String> {
        private ForecastProvider forecastProvider;
        private long startTime;


        private SuitableNameTask() {
            forecastProvider = new WWOForecast();
        }

        String inputName ;
        @Override
        protected void onPreExecute() {
            inputName = getEnteredName();
            startTime = SystemClock.elapsedRealtime();

            Log.d(TAG,  "Suitable Name Task Started");


            super.onPreExecute();


        }



        @Override
        protected String doInBackground(Void... params) {

            if(inputName.equals(lastCheckedName)) {
                try {
                    Thread.sleep(SUITABLE_NAME_TASK_SLEEP_MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error", e);
                }
                if(NO_SUITABLE_CITY_NAME_MESSAGE.equals(suitableNameTextView.getText())) {
                    return null;
                } else {
                    return lastValidName;
                }
            }
            try {
//                Log.d(TAG, "request N " + String.valueOf(++apiRequestsUsed));
                try {                                                           // testing time only
                    Thread.sleep(SUITABLE_NAME_TASK_SLEEP_MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error", e);
                }
                return forecastProvider.getSuitableCityName(inputName);
            } catch (ForecastException e) {
                Log.e(TAG + " SuitableNameTask", "Error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String suitableName) {
            super.onPostExecute(suitableName);
            lastCheckedName = inputName;

            if(suitableName == null) {
                suitableNameTextView.setText(NO_SUITABLE_CITY_NAME_MESSAGE);
                confirmButton.setVisible(false);
            } else {
                suitableNameTextView.setText(suitableName);
                confirmButton.setVisible(true);
                lastValidName = suitableName;
            }

            if(suitableNameRecallEnabled) {
                startSuitableNameTask();
            }

            Log.d(TAG, "Task finished in :" + String.valueOf(SystemClock.elapsedRealtime()- startTime));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = WeatherDatabaseHelper.getInstance(this);

        setContentView(R.layout.city_edit);
        setTitle(R.string.edit_feed);

        cityNameEditText = (EditText) findViewById(R.id.title);
        suitableNameTextView = (TextView) findViewById(R.id.suitable_city_name_text_view);
        suitableNameTextView.setText(NO_SUITABLE_CITY_NAME_MESSAGE);

        lastCheckedName = lastValidName = null;



        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(WeatherDatabaseHelper.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(WeatherDatabaseHelper.KEY_ROWID)
                    : null;
        }

        populateFields();

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        confirmButton = menu.findItem(R.id.action_confirm);
        assert confirmButton != null;
        confirmButton.setVisible(false); 
       return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                if(getEnteredName().equals(lastCheckedName)) {

                setResult(RESULT_OK);
                saveState();
                finish();
                break;
                } else {
                    confirmButton.setVisible(false);
                }
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor feed = null;
            try {
                feed = mDatabaseHelper.fetchCity(mRowId);
                cityNameEditText.setText(feed.getString(
                        feed.getColumnIndexOrThrow(WeatherDatabaseHelper.KEY_CITY_NAME)));

            } finally {
                if (feed != null) {
                    feed.close();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(WeatherDatabaseHelper.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        saveState();
        suitableNameRecallEnabled = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
        suitableNameRecallEnabled = true;
        startSuitableNameTask();

    }

    private void saveState() {
//        String cityName = cityNameEditText.getText().toString();
        String cityName = lastValidName;
        if (cityName.equals("")) {
            return;
        }

        if (mRowId == null) {
            long id = mDatabaseHelper.createCity(cityName);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDatabaseHelper.updateCity(mRowId, cityName);
        }
    }

}
