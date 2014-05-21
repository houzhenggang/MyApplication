
package com.innova.loadimage;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class ImageCache {

    private static final String TAG = "ImageCache";

    public interface OnImageLoadCompleteListener {
        public void OnImageLoadComplete(String fileUrl, boolean success,
                Bitmap bitmap);
    }

    public ImageCache() {
    }

    private static final int INITIAL_CAPACITY = 50;

    private static final LinkedHashMap<String, Bitmap> sImageMap = new LinkedHashMap<String, Bitmap>(
            INITIAL_CAPACITY / 2, 0.75f, true) {

        protected boolean removeEldestEntry(
                java.util.Map.Entry<String, Bitmap> eldest) {
            if (size() > INITIAL_CAPACITY) {
                sSoftImageMap.put(eldest.getKey(), new SoftReference<Bitmap>(
                        eldest.getValue()));
                return true;
            }
            return false;
        };
    };
    private static final ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftImageMap = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

    public static void put(String fileUrl, Bitmap bitmap) {
        if (isEmptyOrWhitespace(fileUrl) || bitmap == null) {
            return;
        }
        synchronized (sImageMap) {
            if (sImageMap.get(fileUrl) == null) {
                sImageMap.put(fileUrl, bitmap);
            }
        }
    }

    public static Bitmap get(String fileUrl) {
        synchronized (sImageMap) {
            Bitmap bitmap = (Bitmap) sImageMap.get(fileUrl);
            if (bitmap != null) {
                return bitmap;
            }
            SoftReference<Bitmap> sBitmap = sSoftImageMap.get(fileUrl);
            if (sBitmap != null) {
                bitmap = sBitmap.get();
                if (bitmap == null) {
                    sSoftImageMap.remove(fileUrl);
                } else {
                    return bitmap;
                }
            }
            return null;
        }
    }

    public static void loadImageBitmap(Context context, String imageUrl,
            OnImageLoadCompleteListener listener, Handler handler, long priority) {

        ImageLoadTask task = new ImageLoadTask(context, imageUrl, listener, handler,
                priority);
        // new Thread(task).start();
        ImageLoadThreadManager.submitTask(imageUrl, task);
        Log.e(TAG, "execute a ImageLoadTask !");

    }

    public static boolean isEmptyOrWhitespace(String s) {
        s = makeSafe(s);
        for (int i = 0, n = s.length(); i < n; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String makeSafe(String s) {
        return (s == null) ? "" : s;
    }

}
