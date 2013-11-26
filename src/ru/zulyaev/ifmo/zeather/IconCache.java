package ru.zulyaev.ifmo.zeather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author seidhe
 */
public class IconCache {
    private final ConcurrentMap<String, ReadWriteLock> lockMap = new ConcurrentHashMap<String, ReadWriteLock>();
    private final File cacheDir;

    public IconCache(Context context) {
        this.cacheDir = context.getExternalCacheDir();
    }

    private ReadWriteLock getLock(String url) {
        if (!lockMap.containsKey(url)) {
            lockMap.putIfAbsent(url, new ReentrantReadWriteLock());
        }
        return lockMap.get(url);
    }

    public Bitmap getBitmap(String url) throws IOException {
        ReadWriteLock lock = getLock(url);

        lock.readLock().lock();
        try {
            Bitmap cached = loadFromDisk(url);
            if (cached != null) {
                return cached;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        Log.d("IconCache", "start " + url);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
            saveToDisk(url, bitmap);
            return bitmap;
        } finally {
            lock.writeLock().unlock();
            Log.d("IconCache", "end " + url);
        }
    }

    private String getFilename(String url) {
        return new BigInteger(url.getBytes()).toString(Character.MAX_RADIX);
    }

    private Bitmap loadFromDisk(String url) {
        File file = new File(cacheDir, getFilename(url));
        try {
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private void saveToDisk(String url, Bitmap bitmap) {
        File file = new File(cacheDir, getFilename(url));
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Log.w(IconCache.class.toString(), "Couldn't create file", e);
        }
    }
}
