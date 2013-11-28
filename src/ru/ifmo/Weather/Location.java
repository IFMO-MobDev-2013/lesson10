package ru.ifmo.Weather;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 20.11.13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
class Location {
    final static String[] tags = new String[]{null, "areaName", "country", "latitude", "longitude"};
    String[] param = new String[tags.length];

    final static int ID = 0;
    final static int TOWN = 1;
    final static int COUNTRY = 2;
    final static int LATITUDE = 3;
    final static int LONGITUDE = 4;

    Location makeCopy(){
        Location a = new Location();
        for (int i = 0; i < param.length; i++){
            a.param[i] = this.param[i];
        }
        return a;
    }

    void clear(){
        param = new String[tags.length];
    }

}
