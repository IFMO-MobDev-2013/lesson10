package com.mikhov.Weather.Async;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TownsHandler extends DefaultHandler {
    private boolean city;
    int id;

    private ParsedExampleDataSet parsedExampleDataSet = new ParsedExampleDataSet();

    public TownsHandler() {

    }

    public ParsedExampleDataSet getParsedData() {
        return this.parsedExampleDataSet;
    }

    @Override
    public void startDocument() throws SAXException {
        this.parsedExampleDataSet = new ParsedExampleDataSet();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String namespaseURL, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("city")) {
            String attr = atts.getValue("id");
            id = Integer.parseInt(attr);
            this.city = true;
        }
    }

    @Override
    public void endElement(String namespaseURL, String localName, String qName) throws SAXException {
        if (localName.equals("city")) {
            this.city = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (city) {
            parsedExampleDataSet.setExtractedString(id + "#" + new String(ch, start, length) + "#");
        }
    }
}
