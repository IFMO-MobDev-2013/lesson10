package com.mikhov.Weather.Async;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExampleHandler extends DefaultHandler {
    private boolean fact, temperature, weather, day, part, image, sunrise, sunset, pressure;
    int days, parts;

    private ParsedExampleDataSet parsedExampleDataSet = new ParsedExampleDataSet();

    public ParsedExampleDataSet getParsedData() {
        return this.parsedExampleDataSet;
    }

    @Override
    public void startDocument() throws SAXException {
        this.parsedExampleDataSet = new ParsedExampleDataSet();
        days = 0;
        parts = 0;
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String namespaseURL, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("fact")) {
            this.fact = true;
        } else if (localName.equals("temperature")) {
            this.temperature = true;
        } else if (localName.equals("weather_type")) {
            this.weather = true;
        } else if (localName.equals("day")) {
            this.day = true;
            days++;
        } else if (localName.equals("day_part")) {
            this.part = true;
            parts++;
        } else if (localName.equals("sunrise")) {
            this.sunrise = true;
        } else if (localName.equals("sunset")) {
            this.sunset = true;
        } else if (localName.equals("pressure")) {
            this.pressure = true;
        } else if (localName.equals("image-v3")) {
            this.image = true;
        }
    }

    @Override
    public void endElement(String namespaseURL, String localName, String qName) throws SAXException {
        if (localName.equals("fact")) {
            this.fact = false;
        } else if (localName.equals("temperature")) {
            this.temperature = false;
        } else if (localName.equals("weather_type")) {
            this.weather = false;
        } else if (localName.equals("day")) {
            this.day = false;
        } else if (localName.equals("day_part")) {
            this.part = false;
        } else if (localName.equals("sunrise")) {
            this.sunrise = false;
        } else if (localName.equals("sunset")) {
            this.sunset = false;
        } else if (localName.equals("pressure")) {
            this.pressure = false;
        } else if (localName.equals("image-v3")) {
            this.image = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (fact && temperature) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (fact && image) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (fact && weather) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (fact && pressure) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunrise && days == 1) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunset && days == 1) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunrise && days == 2) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunset && days == 2) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && temperature && days == 2 && parts == 11) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && image && days == 2 && parts == 11) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && weather && days == 2 && parts == 11) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && pressure && days == 2 && parts == 11) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunrise && days == 3) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && sunset && days == 3) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && temperature && days == 3 && parts == 17) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && image && days == 3 && parts == 17) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && weather && days == 3 && parts == 17) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length) + "#");
        } else if (day && part && pressure && days == 3 && parts == 17) {
            parsedExampleDataSet.setExtractedString(new String(ch, start, length));
        }
    }
}
