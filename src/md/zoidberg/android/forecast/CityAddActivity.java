package md.zoidberg.android.forecast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by gfv on 04.02.14.
 */
public class CityAddActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_add);

        Button addBtn = (Button)findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText city = (EditText)findViewById(R.id.input_city);
                if (city.getText() != null && city.getText().toString().length() > 0) {
                    Intent ret = new Intent();
                    ret.putExtra(ForecastActivity.NEW_CITY_EXTRA, city.getText().toString());
                    setResult(RESULT_OK, ret);
                    finish();
                }
            }
        });
    }
}