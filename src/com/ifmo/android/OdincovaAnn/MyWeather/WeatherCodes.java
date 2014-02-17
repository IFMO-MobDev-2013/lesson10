package com.ifmo.android.OdincovaAnn.MyWeather;

public class WeatherCodes {
    final static int[] codes = {395, 392, 389, 386, 377, 374, 371, 368, 365,
            362, 359, 356, 353, 350, 338, 335, 332, 329, 326, 323, 320, 317,
            314, 311, 308, 305, 302, 299, 296, 293, 284, 281, 266, 263, 260,
            248, 230, 227, 200, 185, 182, 179, 176, 143, 122, 119, 116, 113};
    final static int[] description = {R.string._395, R.string._392, R.string._389, R.string._386, R.string._377, R.string._374,
            R.string._371, R.string._368, R.string._365, R.string._362, R.string._359, R.string._356, R.string._353,
            R.string._350, R.string._338, R.string._335, R.string._332, R.string._329, R.string._326, R.string._323,
            R.string._320, R.string._317, R.string._314, R.string._311, R.string._308, R.string._305, R.string._302,
            R.string._299, R.string._296, R.string._293, R.string._284, R.string._281, R.string._266, R.string._263,
            R.string._260, R.string._248, R.string._230, R.string._227, R.string._200, R.string._185, R.string._182,
            R.string._179, R.string._176, R.string._143, R.string._122, R.string._119, R.string._116, R.string._113};
    final static int[] image = {1, 1, 2, 2, 3, 3, 4, 5, 6, 7, 8, 9, 10, 11, 4, 4, 12, 12, 5, 5, 7, 7, 7, 7, 8, 8, 9, 9,
            10, 10, 8, 11, 9, 9, 13, 13, 14, 14, 2, 9, 7, 5, 10, 13, 15, 16, 17, 18};

    public int getIndex(int code) {
        for (int i = 0; i < codes.length; i++) {
            if (code == codes[i]) {
                return i;
            }
        }
        return -1;
    }
}
