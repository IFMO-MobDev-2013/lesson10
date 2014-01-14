package com.example.GoodWeather;

import android.os.AsyncTask;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class TranslateCity {
    private final String TEXT = "text";
    private String requestUrl ="https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20130922T163909Z.4b80d345f1cf210a.7a19c4e91fc64850938354743ae84c172909c842&lang=en&text=";
    private String requestUrlRu ="https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20130922T163909Z.4b80d345f1cf210a.7a19c4e91fc64850938354743ae84c172909c842&lang=ru&text=";
    private String RU = "ru";
    public Translate async;
    public String word, translate, lang;
    public TranslateCity(String word, String lang) {
        this.word = word;
        this.lang = lang;
        try {
            if (lang.equals(RU)) requestUrl = requestUrlRu + URLEncoder.encode(word, "UTF-8");
            else requestUrl = requestUrl + URLEncoder.encode(word, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            requestUrl = requestUrl + "ERROR";
        }
        async = new Translate();
    }
    class Translate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                java.net.URL url = new URL(requestUrl);
                HttpsURLConnection httpConnection = (HttpsURLConnection) url.openConnection();
                httpConnection.connect();
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 200) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + '\n');
                    }
                    JSONTokener tokener = new JSONTokener(sb.toString());
                    JSONObject object = (JSONObject) tokener.nextValue();
                    translate = object.getString(TEXT);
                    return translate.substring(2, translate.length() - 2);
                }
            }catch(Exception e) {
            }
            return null;
        }
        protected void onPostExecute(String result) {
        }
    }

}
