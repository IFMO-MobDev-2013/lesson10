package com.android;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Руслан
 * Date: 21.11.13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class WeatherParser {

    public static City Parse(InputStream inputStream, City city) {
        try {
            DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dBuilderFactory.newDocumentBuilder();
            Document document = dBuilder.parse(inputStream);
            document.getDocumentElement().normalize();
            Node node = document.getElementsByTagName("item").item(0);
            NodeList nodeList = node.getChildNodes();
            int j = 0;
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {

                    Node elem = nodeList.item(i);
                    if ("yweather:condition".equals(elem.getNodeName())) {
                        Element description = (Element) elem;
                        city.weatherNow = description.getAttribute("text");
                        city.weatherNowCode = Integer.parseInt(description.getAttribute("code"));
                        city.tempNow = Double.parseDouble(description.getAttribute("temp"));
                        city.date = description.getAttribute("date");
                    } else {
                        if ("yweather:forecast".equals(elem.getNodeName()) && j < 3) {
                            Element forecast = (Element) elem;
                            city.dates[j] = forecast.getAttribute("date");
                            city.tempLow[j] = Double.parseDouble(forecast.getAttribute("low"));
                            city.tempLow[j] = Double.parseDouble(forecast.getAttribute("high"));
                            city.weather[j] = forecast.getAttribute("text");
                            city.weatherCode[j] = Integer.parseInt(forecast.getAttribute("code"));
                            j++;
                        }

                    }
                }
            }
        } catch (Exception e) {
        }
        return city;
    }
}
