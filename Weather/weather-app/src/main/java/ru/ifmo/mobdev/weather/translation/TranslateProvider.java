package ru.ifmo.mobdev.weather.translation;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Nick on 29.11.13.
 */
public class TranslateProvider {
    private static final String KEY = "trnsl.1.1.20131002T150025Z.d95b6a9ec1b011d6.7161fc9f2e5f26c56c62d41ad1f49444b3524c19";
    private static final String URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private static final String FORMAT = "http";
    public static final String BAD_TRANSLATE = "bad translate";
    private static final int GOOD_REQUEST = 200;

    private static final HttpClient client = new DefaultHttpClient();

    public String getTranslate(String cityName, String lang) throws IOException {

        HttpGet request = new HttpGet(URL + '?' + URLEncodedUtils.format(Arrays.asList(
                new BasicNameValuePair("key", KEY),
                new BasicNameValuePair("text", cityName),
                new BasicNameValuePair("format", FORMAT),
                new BasicNameValuePair("lang", lang)

        ), "utf-8"));

        HttpResponse response = client.execute(request);
        try {
            String jsonResponse;
            BufferedReader buffer= new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            jsonResponse = buffer.readLine();
            JSONObject jObject = new JSONObject(jsonResponse);
            int code = jObject.getInt("code");
            if (code != GOOD_REQUEST)
                return BAD_TRANSLATE;

            String result = jObject.getString("text");
            return result.substring(2, result.length() - 2);

        } catch (RuntimeException | JSONException e) {
            Log.e("Logging", "something with connection");
        }

        return BAD_TRANSLATE;
    }
}
