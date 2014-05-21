package com.innova.loadimage;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.innova.loadimage.ImageCache.OnImageLoadCompleteListener;
import com.innova.onekeyinstall.MyUtil;

public class ImageLoadTask implements Runnable {

    Context mContext = null;
    String mImageUrl = null;
    Bitmap mBitmap = null;
    Handler mHanler = null;
    long mPriority = -1;
    OnImageLoadCompleteListener mListener = null;
    @SuppressWarnings("unused")
    private ImageLoadTask() {
    }

    public ImageLoadTask(Context context, String imageUrl, OnImageLoadCompleteListener listener,
            Handler handler, long priority) {
        this.mContext = context;
        this.mImageUrl = imageUrl;
        this.mListener = listener;
        this.mHanler = handler;
        this.mPriority = priority;
    }
    
    @Override
    public void run() {
        mBitmap = MyUtil.readBitMap(mImageUrl, mContext);
        ImageLoadThreadManager.removeTask(mImageUrl);
        if (null != mBitmap) {
            ImageCache.put(mImageUrl, mBitmap);
            mHanler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.OnImageLoadComplete(mImageUrl, true, mBitmap);
                }                
            });
        } else {
            mHanler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.OnImageLoadComplete(mImageUrl, false, mBitmap);
                }                
            });
        }
    }

}

