package com.example.weather;

import android.app.IntentService;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
* Created with IntelliJ IDEA.
* User: slavian
* Date: 17.11.13
* Time: 21:15
* To change this template use File | Settings | File Templates.
*/
public class UpdateService extends IntentService implements DbConstants, QueryConstants{

    public static final String ACTION_UP = "UpdateDBService.UP";


    private String[] requestCities = null;
    //private String[] requestCountries = null;
    private String[] requestLinks = null;


    private DbAdapter mDbHelper = null;

    public UpdateService() {
        super("myName");
    }

    public void onCreate() {
        super.onCreate();
        mDbHelper = new DbAdapter(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        ArrayList<HashMap<String, String> > weather = null;     // for parser
        String xml = null;

        getLinks();

        mDbHelper.open();

        mDbHelper.dropForecast();    // ??
        mDbHelper.dropCurrent();

        for (int i = 0; i < requestCities.length; ++i) {
            xml = null;
            weather = null;
            try {
                // load data
                // it's not cool, to make new objects, but
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(requestLinks[i]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
               // byte[] x = EntityUtils.toByteArray(httpEntity);
                xml = EntityUtils.toString(httpEntity);
                //String encoding = xml.substring(xml.indexOf("encoding") + 10, xml.indexOf("\"?>"));

                //xml = new String(x, encoding);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (xml != null)
                    weather = SAXXMLParser.parse(xml);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (weather != null && weather.size() != 0) {
                for (int j = 0; j < weather.size(); ++j) {
                    HashMap<String, String> fj = weather.get(j);

                    fj.put(KEY_NAME, requestCities[i]);
                    //fj.put(KEY_COUNTRY, requestCountries[i]);

                    if(ParsingConstants.KEY_CC.equals(fj.get(ParsingConstants.KEY_TYPE)))
                    {
                        if(!mDbHelper.createCurrent(fj))
                        {
                            Log.w("FUCKENFUCK", "failed adding to base current");
                        }
                        //mDbHelper.createCurrent(fj);
                    }
                    else
                    {
                        if(!mDbHelper.createForecast(fj))
                        {
                            Log.w("FUCKENFUCK", "failed adding to base forecast");
                        }
                    }
                }
            }
        }

        mDbHelper.close();

        Intent intentResponse = new Intent();
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);

        intentResponse.setAction(ACTION_UP);

        sendBroadcast(intentResponse);
    }

    private void getLinks() {
        mDbHelper.open();
        Cursor cursor = mDbHelper.fetchCity();

//        if(cursor == null || cursor.getCount() == 0)
//        {
//            requestCities = getResources().getStringArray(R.array.default_cities);
//            //requestCountries = getResources().getStringArray(R.array.default_countries);
//            int i=0;
//            requestLinks = new String[requestCountries.length];
//            while(i<requestCountries.length)
//            {
//                requestLinks[i] = BEG_WETH + KEY1+ KEY_Q + requestCities[i]+","+requestCountries[i] + KEY_CCY + KEY_NUM_OF_RES + "3" + KEY_POPULAR + "yes";
//                ++i;
//            }
//            return;
//        }


        int indexName = cursor.getColumnIndex(KEY_NAME);
        int indexLong = cursor.getColumnIndex(KEY_LONG);
        int indexLat = cursor.getColumnIndex(KEY_LAT);
        //int indexCountry = cursor.getColumnIndex(KEY_COUNTRY);

        int i = 0;
        String city = null;
        String latitude = null;
        String longitude = null;
        //String country = null;

        requestCities = new String[cursor.getCount()];
        //requestCountries= new String[cursor.getCount()];
        requestLinks = new String[cursor.getCount()];


        while (cursor.moveToNext() && i < requestLinks.length) {
            try {
                city = cursor.getString(indexName);
                latitude = cursor.getString(indexLat);
                longitude = cursor.getString(indexLong);

                //country = cursor.getString(indexCountry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestCities[i] = city;
            //requestCountries[i] = country;
            //country = country.replaceAll("\\s", "+");
            //city = city.replaceAll("\\s", "+");
            requestLinks[i] = BEG_WETH + KEY1+ KEY_Q + URLEncoder.encode( latitude+ "," + longitude ).toString() + KEY_NUM_OF_RES + "3";
            ++i;
        }
        cursor.close();
        mDbHelper.close();
    }
}


