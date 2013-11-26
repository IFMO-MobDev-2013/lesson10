package ru.zulyaev.ifmo.zeather.weather;

class SearchResponse {
    SearchApiElement search_api;

    static class SearchApiElement {
        ResultElement[] result;
    }

    static class ResultElement {
        ValueElement<String>[] areaName;
        ValueElement<String>[] country;
        String latitude;
        String longitude;
        String population;
        ValueElement<String>[] region;
        ValueElement<String>[] weatherUrl;
    }
}
