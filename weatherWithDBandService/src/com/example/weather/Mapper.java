package com.example.weather;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 28.11.13
 * Time: 2:05
 * To change this template use File | Settings | File Templates.
 */
public class Mapper {
    public static final String TAG = "FUCKENFUCK::Mapper ";
    public static int map(int image_id) {
        Log.w(TAG, "mapping " + new Integer(image_id).toString() );
        int result =0;
        switch (image_id)
        {
            case 395:
                result = R.drawable.i6_10;
                break;

            case 392:
                result = R.drawable.i6_10;
                break;
                
            case 389:
                result = R.drawable.i6_15;
                break;

            case 386:
                result = R.drawable.i6_15;
                break;
                
            case 377:
                result = R.drawable.i6_12;
                break;

            case 374:
                result = R.drawable.i6_12;
                break;

            case 371:
                result = R.drawable.i6_4;
                break;

            case 368:
                result = R.drawable.i6_10;
                break;

            case 365:
                result = R.drawable.i6_9;
                break;

            case 362:
                result = R.drawable.i6_9;
                break;

            case 359:
                result = R.drawable.i6_8;
                break;

            case 356:
                result = R.drawable.i6_8;
                break;

            case 353:
                result = R.drawable.i6_8;
                break;

            case 350:
                result = R.drawable.i6_12;
                break;

            case 338:
                result = R.drawable.i6_4;
                break;

            case 335:
                result = R.drawable.i6_4;
                break;

            case 332:
                result = R.drawable.i6_10;
                break;

            case 329:
                result = R.drawable.i6_10;
                break;

            case 326:
                result = R.drawable.i6_16;
                break;

            case 323:
                result = R.drawable.i6_16;
                break;

            case 320:
                result = R.drawable.i6_9;
                break;

            case 317:
                result = R.drawable.i6_9;
                break;

            case 314:
                result = R.drawable.i6_12;
                break;

            case 311:
                result = R.drawable.i6_12;
                break;

            case 308:
                result = R.drawable.i6_7;
                break;

            case 305:
                result = R.drawable.i6_14;
                break;

            case 302:
                result = R.drawable.i6_7;
                break;

            case 299:
                result = R.drawable.i6_14;
                break;

            case 296:
                result = R.drawable.i6_7;
                break;

            case 293:
                result = R.drawable.i6_14;
                break;

            case 284:
                result = R.drawable.i6_6;
                break;

            case 281:
                result = R.drawable.i6_6;
                break;

            case 266:
                result = R.drawable.i6_6;
                break;

            case 263:
                result = R.drawable.i6_6;
                break;

            case 260:
                result = R.drawable.i6_6;
                break;

            case 248:
                result = R.drawable.i6_6;
                break;

            case 230:
                result = R.drawable.i6_4;
                break;

            case 227:
                result = R.drawable.i6_4;
                break;

            case 200:
                result = R.drawable.i6_13;
                break;

            case 185:
                result = R.drawable.i6_6;
                break;

            case 182:
                result = R.drawable.i6_9;
                break;

            case 179:
                result = R.drawable.i6_16;
                break;

            case 176:
                result = R.drawable.i6_16;
                break;

            case 143:
                result = R.drawable.i6_6;
                break;

            case 122:
                result = R.drawable.i6_2;
                break;
                
            case 119:
                result = R.drawable.i6_5;
                break;

            case 116:
                result = R.drawable.i6_3;
                break;
                
            case 113:
                result = R.drawable.i6_2;
                break;
            default:
                result = R.drawable.icon;
                break;
        }
        return result;
    }
}
