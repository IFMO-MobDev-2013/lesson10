package com.example.lesson10;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import org.w3c.dom.Document;

public class CityWebActivity extends Activity implements IEventHadler
{
    private int id_city = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cityweather);
        id_city = getIntent().getIntExtra("ID_CITY", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void handleEvent(Event e)
    {
          e.target.removeEventListener(this);
          if (e.type == Event.COMPLETE)
          {
              Document xml = (Document) e.data.get("document");
              xml.getDocumentElement().getElementsByTagName("fact").item(0);

              runOnUiThread(new Runnable()
              {
                  @Override
                  public void run() {
                      TextView tv = (TextView)findViewById(R.id.cityNameLabel);
                      tv.setText(City.findById(id_city).name+"");
                  }
              });
          }
    }
}
