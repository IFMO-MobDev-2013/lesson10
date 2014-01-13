package ru.skipor.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vladimir Skipor on 1/8/14.
 * Email: vladimirskipor@gmail.com
 */
public class InternalSrorageUtils {
    private final static String TAG = "InternalSrorageUtils";
    private static int pixelXYToColorTake = 2;

    public static void saveBitmap(Context context, String fileName, Bitmap bitmapImage) {

        FileOutputStream fileOutputStream;

        deleteFileIfExists(context, fileName);
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error", e);
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        }
    }

    public static void deleteFileIfExists(Context context, String fileName) {
        if (fileExists(context, fileName)) {
            context.deleteFile(fileName);
        }
    }

    public static String downloadAndSaveBitmap(Context context, String URL) throws HttpException {
        FileOutputStream fileOutputStream;
        String fileName = HTTPUtils.getFileName(URL);
//        deleteFileIfExists(context, fileName);

        if (!fileExists(context, fileName)) {
            saveBitmap(context, fileName, HTTPUtils.getBitmap(URL));
        }
        return fileName;


    }

    public static Bitmap loadBitmap(Context context, String fileName) {
        Log.d(TAG, "loading " + fileName);

        try {
            final FileInputStream fileInputStream = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
            return bitmap;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error", e);
            return null;
        }

    }

    public static boolean fileExists(Context context, String fileName) {
        try {
            final FileInputStream fileInputStream = context.openFileInput(fileName);
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static int getBackgroundColor(Bitmap bitmap) {
        return bitmap.getPixel(pixelXYToColorTake, pixelXYToColorTake);
    }
}


