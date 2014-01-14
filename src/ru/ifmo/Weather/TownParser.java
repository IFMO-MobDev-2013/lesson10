package ru.ifmo.Weather;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 20.11.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 23.10.13
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */

class TownParser {
    private static RootElement prepare(ArrayList<Location> a) throws Exception{
        final Location currentLocation = new Location();
        final ArrayList<Location> towns = a;
        RootElement root = new RootElement("search_api");
        android.sax.Element item = root.getChild("result");

        item.setEndElementListener(new EndElementListener(){
            public void end() {
                towns.add(currentLocation.makeCopy());
                currentLocation.clear();
            }
        });
        for (int i = 0; i < Location.tags.length; i++){
            if (Location.tags[i] == null) continue;
            final int k = i;
            item.requireChild(Location.tags[i]).setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    currentLocation.param[k] = body;
                }
            });
        }

        return root;
    }
    public static ArrayList<Location> parse(String URLAdress) throws Exception {
        final ArrayList<Location> data = new ArrayList<Location>();
        InputStream in = null;
        InputStreamReader inr = null;
        try {
            URL feedUrl = new URL(URLAdress);
            URLConnection conn = feedUrl.openConnection();
            in = conn.getInputStream();
            RootElement root = prepare(data);

            inr = new InputStreamReader(in);
            Xml.parse(inr, root.getContentHandler());
        } finally
        {
            try{
                if (in != null) in.close();
            } catch (Throwable ex){
            }
            try{
                if (inr != null) inr.close();
            } catch (Throwable ex){
            }
        }

        return data;
    }
}