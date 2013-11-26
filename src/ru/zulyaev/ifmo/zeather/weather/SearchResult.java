package ru.zulyaev.ifmo.zeather.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author seidhe
 */
public class SearchResult {
    private final List<City> cities;

    SearchResult(SearchResponse response) {
        List<City> temp = new ArrayList<City>();
        if (response.search_api != null) {
            for (SearchResponse.ResultElement result : response.search_api.result) {
                temp.add(new City(result));
            }
        }
        this.cities = Collections.unmodifiableList(temp);
    }

    public List<City> getCities() {
        return cities;
    }
}
