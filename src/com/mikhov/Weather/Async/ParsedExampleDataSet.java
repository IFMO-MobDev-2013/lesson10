package com.mikhov.Weather.Async;

public class ParsedExampleDataSet {
    private String extractedString = "";

    public String getExtractedString() {
        return this.extractedString;
    }

    public void setExtractedString(String in_extractedString) {
        this.extractedString += in_extractedString;
    }

    public String toString() {
        return this.extractedString;
    }
}
