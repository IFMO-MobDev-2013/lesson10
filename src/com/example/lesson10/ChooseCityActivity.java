package com.example.lesson10;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.Vector;

public class ChooseCityActivity extends Activity implements IEventHadler
{
    private CitiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosecity);

        adapter = new CitiesAdapter(this, new Vector<City>());
        adapter.addEventListener(this);

        ListView list_view = (ListView) findViewById(R.id.choosecity_citiesList);
        list_view.setAdapter(adapter);
    }

    public void loadAll(View v)
    {
        XmlLoader loader = new XmlLoader("http://monplay.tw1.ru/cities.xml");
        loader.addEventListener(this);
        loader.start();
        Toast.makeText(this, "Starting update. It can takes a few minutes...", 7000).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.cities.clear();
        Cursor sth = Database.gi().query("select * from cities where chosen = 0");
        while (sth.moveToNext())
        {
            City city = City.findById(Integer.parseInt(sth.getString(0)));
            city.name = sth.getString(1);
            city.chosen = Integer.parseInt(sth.getString(2)) != 0;
            city.temperature = Integer.parseInt(sth.getString(3));
            adapter.cities.add(city);
        }
        Collections.sort(adapter.cities);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void updateList(Document xml)
    {

        Element root =  xml.getDocumentElement();
        NodeList list = xml.getElementsByTagName("city");
        Database.gi().exec("delete from cities where chosen = 0");
        //Console.print("Update List");
        Console.print("Count of elements = " + list.getLength());
        for (int i = 0; i < list.getLength(); ++i)
        {
            Node current = list.item(i);
            if (current.getNodeName().equals(""))
            {
                continue;
            }
            City city = City.findById(Integer.parseInt(((Element) current).getAttribute("id")));
            String name = current.getNodeName();
            String value = current.getFirstChild().getNodeValue();
            city.name = value;
            //Console.print("City id = "+city.getId() + " " + city.name);
            if (!city.chosen)
            {
                city.flush();
                adapter.cities.add(city);
            }
            Console.print("("+i+") "+city.name);
        }
        Collections.sort(adapter.cities);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void handleEvent(Event e)
    {
        Console.print("Event type = " + e.type);
        if (e.type == Event.COMPLETE)
        {
             e.target.removeEventListener(this);
             updateList((Document) e.data.get("document"));
        }
        else if (e.type == TouchEvent.CLICK)
        {
            City.findById((Long)e.data.get("ID_CITY")).chosen = true;
            City.findById((Long)e.data.get("ID_CITY")).flush();
            Toast.makeText(this, "You have added a new city", 3000).show();
        }
    }
}
