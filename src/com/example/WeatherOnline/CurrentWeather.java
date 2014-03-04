package com.example.WeatherOnline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 03.02.14
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class CurrentWeather extends Activity {
    ArrayList<String> maxTemp = new ArrayList<String>();
    ArrayList<String> minTemp = new ArrayList<String>();
    ArrayList<String> windSpeed = new ArrayList<String>();
    ArrayList<String> windDirection = new ArrayList<String>();
    ArrayList<String> weatherIcon = new ArrayList<String>();
    ArrayList<String> date = new ArrayList<String>();
    ArrayList<String> weatherDesc = new ArrayList<String>();
    String time;
    String icon;
    String desc;
    String speed;
    String humidity;
    String temp;
    String wind;
    String city;
    int cur = -1;
    int error = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);
        city = getIntent().getExtras().getString("city");
        WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());
        String data = weatherDataBase.getCityWeather(city);
        try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(data.getBytes()), new Handler());
        } catch (IOException e){

        } catch (SAXException e){

        } catch (ParserConfigurationException e){

        }
        catch (Exception e){

        }
        draw(cur + error);
    }

    public void draw(int cur){
        TextView time = (TextView) findViewById(R.id.updateTime);
        ImageView icon = (ImageView) findViewById(R.id.weatherIcon);
        TextView description = (TextView) findViewById(R.id.temperature);
        TextView maxTemp = (TextView) findViewById(R.id.maxT);
        TextView minTemp = (TextView) findViewById(R.id.minT);
        TextView maxTValue = (TextView) findViewById(R.id.maxTValue);
        TextView minTValue = (TextView) findViewById(R.id.minTValue);
        TextView windSpeed = (TextView) findViewById(R.id.windSpeed);
        TextView windSpeedValue = (TextView) findViewById(R.id.windSpeedValue);
        TextView windDirection = (TextView) findViewById(R.id.windDirection);
        TextView windDirectionValue = (TextView) findViewById(R.id.windDirectionValue);
        WeatherDataBase weatherDataBase = new WeatherDataBase(getApplicationContext());

        ArrayList<Bitmap> bitmaps = weatherDataBase.getPic(city);
        if(cur == -2){
            icon.setImageBitmap(null);
            time.setText("");
            description.setText("");
            minTemp.setText("");
            minTValue.setText("");
            maxTemp.setText("");
            maxTValue.setText("");
            windSpeedValue.setText("");
            windDirectionValue.setText("");
            windDirection.setText("");
            windSpeed.setText("");
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.loadError), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        if(cur == -1){
            icon.setImageBitmap(bitmaps.get(cur + 1));
            time.setText(city + ", " + this.time);
            description.setText(desc);
            minTemp.setText(getResources().getString(R.string.humidity));
            minTValue.setText(humidity + getResources().getString(R.string.degree));
            maxTemp.setText(getResources().getString(R.string.outdoor));
            maxTValue.setText(temp + getResources().getString(R.string.percent));
            windSpeedValue.setText(speed + getResources().getString(R.string.speed));
            windDirectionValue.setText(wind);
            windDirection.setText(getResources().getString(R.string.windDirection));
            windSpeed.setText(getResources().getString(R.string.windSpeed));
        }else{
            icon.setImageBitmap(bitmaps.get(cur + 1));
            time.setText(city + ", " + date.get(cur));
            description.setText(weatherDesc.get(cur));
            maxTemp.setText("Максимальная температура:");
            maxTValue.setText(this.maxTemp.get(cur) + getResources().getString(R.string.degree));
            minTemp.setText("Минимальная температура:");
            minTValue.setText(this.minTemp.get(cur) + getResources().getString(R.string.degree));
            windSpeedValue.setText(this.windSpeed.get(cur) + getResources().getString(R.string.speed));
            windDirection.setText(getResources().getString(R.string.windDirection));
            windSpeed.setText(getResources().getString(R.string.windSpeed));
        }
    }

    private class Handler extends DefaultHandler {
        final String TEMP_T = "temp_C";
        final String ICON = "weatherIconUrl";
        final String WEATHER_DESC = "weatherDesc";
        final String WIND_SPEED = "windspeedKmph";
        final String HUMIDITY = "humidity";
        final String WIND = "winddir16Point";
        final String NOW = "current_condition";
        final String WEATHER = "weather";
        final String DATE = "date";
        final String TEMP_MAX = "tempMaxC";
        final String TEMP_MIN = "tempMinC";
        final String TIME = "observation_time";
        final String ERROR = "error";
        String buffer = "";

        boolean current = false, weather = false;


        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs)throws SAXException {
            buffer = "";
            if(ERROR.equals(localName))
                error = -1;
            if(NOW.equals(localName)){
                current = true;
            }

            if(WEATHER.equals(localName)){
                weather = true;
            }
        }

        public void endElement(String uri, String localName, String qName)throws SAXException{
            if(NOW.equals(localName)){
                current = false;
            }
            if(TIME.equals(localName) && current){
                time = buffer;
            }
            if(WEATHER.equals(localName)){
                weather = false;
            }
            if(TEMP_T.equals(localName) && current){
                temp = buffer;
            }
            if(ICON.equals(localName)){
                if(current)
                    icon = buffer;
                if(weather)
                    weatherIcon.add(buffer);
            }
            if(WEATHER_DESC.equals(localName)){
                if(current)
                    desc = buffer;
                if(weather)
                    weatherDesc.add(buffer);
            }
            if(WIND_SPEED.equals(localName)){
                if(current)
                    speed = buffer;
                if(weather)
                    windSpeed.add(buffer);
            }
            if(HUMIDITY.equals(localName) && current){
                humidity = buffer;
            }
            if(WIND.equals(localName)){
                if(current)
                    wind = buffer;
                if(weather)
                    windDirection.add(buffer);;
            }
            if(TEMP_MAX.equals(localName) && weather){
                maxTemp.add(buffer);
            }
            if(TEMP_MIN.equals(localName) && weather){
                minTemp.add(buffer);
            }
            if(DATE.equals(localName) && weather){
                date.add(buffer);
            }
            buffer = "";
        }

        @Override
        public void characters(char[] ch, int start, int length){
            if(current || weather)
                buffer += new String(ch, start, length);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, "Назад");
        menu.add(0, 2, 1, "Вперед");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == 2){
            if(cur < 2)
                cur++;
        }else{
            if(cur > -1)
                cur--;
        }
        draw(cur);
        return super.onOptionsItemSelected(item);
    }
}
