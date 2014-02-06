package com.example.WeatherOnline;

import android.content.ContentValues;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 05.02.14
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class Converter {

    public static void putBitmapInConvertValue(ContentValues value, Bitmap bitmap, String rowName){
        int[] tmp = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(tmp, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        value.put(rowName, fromIntArrayToByteArray(tmp));
    }

    public static byte[] returnByteFromBitmap(Bitmap bitmap){
        int[] tmp = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(tmp, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return fromIntArrayToByteArray(tmp);
    }

    public static byte[] fromIntArrayToByteArray(int[] array){
        ByteBuffer bb = ByteBuffer.allocate(array.length * 4);
        IntBuffer intBuffer = bb.asIntBuffer();
        intBuffer.put(array);
        return bb.array();
    }

    public static int[] fromByteArrayToIntArray(byte[] bytes){
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        IntBuffer intBuffer = bb.asIntBuffer();
        bb.put(bytes);
        int[] ans = new int[intBuffer.remaining()];
        intBuffer.get(ans);
        return ans;
    }
}
