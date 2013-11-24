package com.example.lesson10;


import android.database.Cursor;
import android.database.DatabaseUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Vector;

public class City implements Comparable<City>, IEventDispatcher, IEventHadler
{
    static class Day
    {
        public String date = "0000-00-00";
        public int morning = 0;
        public int day = 0;
        public int evening = 0;
        public int night = 0;
        public int getAverage()
        {
            return (morning + day + evening) / 3;
        }
    }

    private long id_subject = 0;
    public String name = "";
    public int temperature = 1000;
    public String weather_desc = "";
    public String weather_code = "clear";
    public boolean chosen = false;
    public EventDispatcher event_pull;
    private static HashMap<Long, City> cities = null;
    public int hum = 0;
    public float speed = 0;
    public Vector<Day> days;

    private City(long id_subject)
    {
        this.id_subject = id_subject;
        event_pull = new EventDispatcher();
        days = new Vector<Day>(4);
    }

    public long getId()
    {
        return id_subject;
    }

    public void flush()
    {
        Database.gi().exec("update cities set name = " + DatabaseUtils.sqlEscapeString(name) + ", chosen = " + (chosen ? 1 : 0) + ", temperature = " + temperature + " where id_city = " + id_subject);
    }

    public void startUpdate()
    {
        XmlLoader loader = new XmlLoader("http://export.yandex.ru/weather-ng/forecasts/" + getId() + ".xml");
        loader.addEventListener(this);
        loader.start();
    }

    public static City findById(long id_subject)
    {
        if (cities == null)
        {
            cities = new HashMap<Long, City>();
        }
        if (cities.containsKey(id_subject))
        {
             return cities.get(id_subject);
        }
        else
        {
            City city = new City(id_subject);
            cities.put(id_subject, city);

            Cursor c = Database.gi().query("select * from cities where id_city = " + id_subject);
            if (c.getCount() == 0)
            {
                Database.gi().exec("insert into cities values("+ id_subject +", '', 0, 1000)");
            }
            else if (c.moveToNext())
            {
                city.name = c.getString(1);
                city.chosen = Integer.parseInt(c.getString(2)) != 0;
                city.temperature = Integer.parseInt(c.getString(3));
            }
            return city;
        }
    }

    @Override
    public int compareTo(City another)
    {
        return this.name.compareTo(another.name);
    }

    @Override
    public void handleEvent(Event e)
    {
        if (e.type == Event.COMPLETE)
        {
            e.target.removeEventListener(this);

            Document xml = (Document) e.data.get("document");
            Node fact = xml.getDocumentElement().getElementsByTagName("fact").item(0);
            NodeList list = fact.getChildNodes();

            for (int i = 0; i < list.getLength(); ++i)
            {
                Node current = list.item(i);
                if (current.getNodeName().equals(""))
                {
                    continue;
                }
                if (current.getNodeName().equals("temperature"))
                {
                    temperature = Integer.parseInt(current.getFirstChild().getNodeValue());
                }
                if (current.getNodeName().equals("weather_type"))
                {
                    weather_desc = current.getFirstChild().getNodeValue();
                }
                else if (current.getNodeName().equals("weather_condition"))
                {
                    weather_code = ((Element)current).getAttribute("code").replace("-","");
                }
                else if (current.getNodeName().equals("humidity"))
                {
                    hum =  Integer.parseInt(current.getFirstChild().getNodeValue());
                }
                else if (current.getNodeName().equals("wind_speed"))
                {
                    speed =  Float.parseFloat(current.getFirstChild().getNodeValue());
                }
            }

            flush();

            list = xml.getDocumentElement().getElementsByTagName("day");
            for (int i = 0; i < 4; ++i)
            {
                Node current = list.item(i);
                days.add(parseDay(current));
            }
            dispatchEvent(new Event(this, e.type));
        }
    }

    public Day getToday()
    {
        return days.get(0);
    }

    public Day getDay(int offset)
    {
        return days.get(offset);
    }

    private Day parseDay(Node day)
    {
        Day current = new Day();
        String date = ((Element)day).getAttribute("date");
        current.date = date;
        NodeList parts = ((Element) day).getElementsByTagName("day_part");
        for (int i = 0; i < parts.getLength(); ++i)
        {
            Node part = parts.item(i);
            String type = ((Element)part).getAttribute("type");
            int t = Integer.parseInt(part.getChildNodes().item(1).getFirstChild().getNodeValue());
            if (type.equals("morning"))
            {
                current.morning = t;
            }
            else if (type.equals("day"))
            {
                current.day = t;
            }
            else if (type.equals("evening"))
            {
                current.evening = t;
            }
            else if (type.equals("night"))
            {
                current.night = t;
            }
        }
        return current;
    }

    @Override
    public void addEventListener(IEventHadler listener) {
        event_pull.addEventListener(listener);
    }

    @Override
    public void removeEventListener(IEventHadler listener) {
        event_pull.removeEventListener(listener);
    }

    @Override
    public void dispatchEvent(Event e) {
        event_pull.dispatchEvent(e);
    }
}
