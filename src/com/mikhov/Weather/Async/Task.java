package com.mikhov.Weather.Async;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.mikhov.Weather.Database;
import com.mikhov.Weather.R;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class Task extends AsyncTask<Void, String, String> {
    
    protected final Resources mResources;
    
    private String mResult, num, town, task;
    private String mProgressMessage;
    private IProgressTracker mProgressTracker;
    Database database;
    Context context;
    String[] towns, townsArray;

    public Task(String in_task, Resources resources, Context in_context) {
        context = in_context;
        task = in_task;
        mResources = resources;
        if (task.equals("forecast")) {
            mProgressMessage = resources.getString(com.mikhov.Weather.R.string.task_starting);
        } else {
            mProgressMessage = mResources.getString(R.string.first_launch);
        }
    }

    public void setProgressTracker(IProgressTracker progressTracker) {
	mProgressTracker = progressTracker;
        if (mProgressTracker != null) {
            mProgressTracker.onProgress(mProgressMessage);
            if (mResult != null) {
                mProgressTracker.onComplete();
            }
        }
    }

    @Override
    protected void onCancelled() {
	    mProgressTracker = null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mProgressMessage = values[0];
        if (mProgressTracker != null) {
            mProgressTracker.onProgress(mProgressMessage);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        mResult = result;
        if (mProgressTracker != null) {
            mProgressTracker.onComplete();
        }
        mProgressTracker = null;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        database = new Database(context);
        database.open();
        if (task.equals("forecast")) {
            try {
                if (database.isNotEmpty()) {
                    Cursor codes = database.getAllData();
                    String str_codes = "";
                    while (codes.moveToNext()) {
                        str_codes += codes.getString(codes.getColumnIndex(Database.COL_CODE)) + "#";
                    }
                    str_codes = str_codes.substring(0, str_codes.length() - 1);
                    towns = str_codes.split("#");
                    String str_url = "http://export.yandex.ru/weather-ng/forecasts/";

                    try {
                        for (int i = 0; i < towns.length; i++) {
                            num = towns[i];
                            URL url = new URL(str_url + towns[i] + ".xml");
                            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                            SAXParser saxParser = saxParserFactory.newSAXParser();

                            XMLReader xmlReader = saxParser.getXMLReader();

                            ExampleHandler exampleHandler = new ExampleHandler();
                            xmlReader.setContentHandler(exampleHandler);

                            xmlReader.parse(new InputSource(url.openStream()));

                            ParsedExampleDataSet parsedExampleDataSet = exampleHandler.getParsedData();
                            String[] d = parsedExampleDataSet.toString().split("#");

                            town = database.getTownById(num);

                            Bitmap[] bitmap = new Bitmap[3];
                            for (int j = 0; j < 3; j++) {
                                bitmap[j] = null;
                                HttpURLConnection conn = null;
                                BufferedInputStream buf_stream = null;
                                try {
                                    int k = 0;
                                    if (j == 0) k = 1;
                                    if (j == 1) k = 9;
                                    if (j == 2) k = 15;
                                    conn = (HttpURLConnection) new URL("http://yandex.st/weather/1.2.1/i/icons/48x48/" + d[k] + ".png").openConnection();
                                    conn.setDoInput(true);
                                    conn.setRequestProperty("Connection", "Keep-Alive");
                                    conn.connect();
                                    buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
                                    bitmap[j] = BitmapFactory.decodeStream(buf_stream);
                                    buf_stream.close();
                                    conn.disconnect();
                                } catch (MalformedURLException ex) {
                                } catch (IOException ex) {
                                }
                            }
                            database.updateTown(town, d[0] + mResources.getString(R.string.deg), d[2], d[3], d[4], d[5], R.drawable.ic_launcher, bitmap[0],
                                d[8] + mResources.getString(R.string.deg), d[10], d[11], d[6], d[7], R.drawable.ic_launcher, bitmap[1],
                                d[14] + mResources.getString(R.string.deg), d[16], d[17], d[12], d[13], R.drawable.ic_launcher, bitmap[2]);
                        }
                        return "";
                    } catch (Exception e) {
                       return "";
                    }
                } else {
                    return "";
                }
            } catch(Exception e) {
                return "";
            }
        } else if (task.equals("yandex")) {
            database.dropYandex();
            try {
                URL url = new URL("http://weather.yandex.ru/static/cities.xml");
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();

                XMLReader xmlReader = saxParser.getXMLReader();

                TownsHandler townsHandler = new TownsHandler();
                xmlReader.setContentHandler(townsHandler);

                xmlReader.parse(new InputSource(url.openStream()));

                ParsedExampleDataSet parsedExampleDataSet = townsHandler.getParsedData();

                String parseResult = parsedExampleDataSet.toString();
                townsArray = parseResult.split("#");
                for (int i = 0; i < townsArray.length - 2; i += 2) {
                    database.addYaTown(townsArray[i], townsArray[i + 1]);
                }
                return "";
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}