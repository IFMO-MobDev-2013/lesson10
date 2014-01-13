package ru.skipor.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vladimir Skipor on 1/7/14.
 * Email: vladimirskipor@gmail.com
 */
public class HTTPUtils {
    private final static String TAG = "HTTPUtils";
    private final static String FILE_NAME_REGEX = ".*\\/([^\\/]+)$";
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(FILE_NAME_REGEX);



    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return  (networkInfo != null && networkInfo.isConnected());
    }

    public static String getContent(String URL) throws HttpException {


            HttpClient httpclient = new DefaultHttpClient();

            // Prepare a request object
            HttpGet httpget = new HttpGet(URL);

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                // Examine the response status

                Log.i( TAG,response.getStatusLine().toString());

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to worry about connection release

                if (entity != null) {

                    InputStream instream = entity.getContent();
                    String result= convertStreamToString(instream);
                    instream.close();
                    return result;
                } else {
                    return null;
                }


            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error", e);
                throw new HttpException(TAG, e);
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
                throw new HttpException(TAG, e);

            }
    }

    public static Bitmap getBitmap(String URL) throws HttpException {

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(URL);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status

            Log.i( TAG,response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                InputStream instream = entity.getContent();
                Bitmap result= BitmapFactory.decodeStream(instream);
                instream.close();
                return result;
            } else {
                return null;
            }


        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error", e);
            throw new HttpException(TAG, e);
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            throw new HttpException(TAG, e);

        }


    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error", e);

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
        }
        return sb.toString();
    }

    public static String getFileName(String URL) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(URL);
        if (!matcher.find()) {
            return null;
        } else {
            return URL.substring(matcher.start(1), matcher.end(1));
        }

    }
}
