package ifmo.mobdev.Metcast;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class WeatherIntentService extends IntentService {
    public static final String ACTION_MyIntentService = "ifmo.mobdev.Metcast.intentservice.RESPONSE";
    public static final String DATA_LOADED = "something loaded";
    public static final String DATA_LOAD_OK = "weather loaded successfully";
    public static final String DATA_LOAD_BAD = "xml = null";
    public static final String DATA_PARSE_OK = "items parsed successfully";
    public static final String DATA_PARSE_BAD = "items = null";
    String url = "http://api.worldweatheronline.com/free/v1/weather.ashx";
    String key = "?key=7s2dwea7mtuyj8sjrx9qyfex";
    private ArrayList<HashMap<String, String>> items;

    public WeatherIntentService() {
        super("Weather Service");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String city = intent.getStringExtra("city");
        String screen = intent.getStringExtra("screen");
        String country;
        WeatherDBAdapter mDbHelper = new WeatherDBAdapter(this);
        mDbHelper.open();
        Long city_id = mDbHelper.getCityIdByName(city);
        int p = city.indexOf(',');
        country = city.substring(p + 1);
        city = city.substring(0, p);
        country = country.trim();
        //city = city + "," + country;
        String citycopy = "";
        while ((p = city.indexOf(' ')) != -1) {
            citycopy += city.substring(0, p) + "+";
            city = city.substring(p + 1);
        }
        city = citycopy + city;

        String result = url + key + "&q=" + city + "&cc=yes" + "&num_of_days=5" + "&format=xml";
        String xml = null;
        if (city != null) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(result);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                byte[] x = EntityUtils.toByteArray(httpEntity);
                xml = new String(x);
            } catch (UnsupportedEncodingException e) {
                Log.d("DownloadXMLTask", e.getLocalizedMessage());
            } catch (ClientProtocolException e) {
                Log.d("DownloadXMLTask", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.d("DownloadXMLTask", "IOException");
            } catch (RuntimeException e) {
                Log.d("DownloadXMLTask", "RuntimeException");
            }
        }

        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);

        if (xml != null) {
            intentResponse.putExtra("xml", DATA_LOAD_OK);
            SAXXMLParser parser = new SAXXMLParser(xml);
            items = parser.parse();

            if (items == null || items.size() == 0) {
                intentResponse.putExtra("items", DATA_PARSE_BAD);
            } else {
                mDbHelper.deleteToday(city_id);
                intentResponse.putExtra("items", DATA_PARSE_OK);
                HashMap<String, String> map0 = items.get(0);
                mDbHelper.createToday(city_id, map0.get(SAXXMLHandler.DATE),
                        map0.get(SAXXMLHandler.DESCR), map0.get(SAXXMLHandler.CUR_WIND),
                        map0.get(SAXXMLHandler.CUR_PRESS), map0.get(SAXXMLHandler.CUR_HUM),
                        map0.get(SAXXMLHandler.CUR_TEMP), map0.get(SAXXMLHandler.IMG_ID));
                mDbHelper.deleteWeek(city_id);
                for (int i = 1; i < items.size(); i++) {
                    HashMap<String, String> map = items.get(i);
                    String currentDate =  map.get(SAXXMLHandler.DATE);
                    try {
                        Locale.setDefault(Locale.US);
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);
                        String dayOfTheWeek = new SimpleDateFormat("EEEE").format(date),
                            day = new SimpleDateFormat("d").format(date);
                        currentDate = dayOfTheWeek + ", " + day;
                    } catch (ParseException e) {

                    }
                    mDbHelper.createWeek(city_id, currentDate, map.get(SAXXMLHandler.DESCR),
                            map.get(SAXXMLHandler.MAX_TEMP), map.get(SAXXMLHandler.MIN_TEMP), map.get(SAXXMLHandler.IMG_ID));
                }
            }
        } else {
            intentResponse.putExtra("xml", DATA_LOAD_BAD);
        }
        intentResponse.putExtra("screen", screen);
        sendBroadcast(intentResponse);
    }
}
