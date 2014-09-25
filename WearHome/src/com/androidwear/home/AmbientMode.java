package com.androidwear.home;

import android.service.dreams.DreamService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class AmbientMode extends DreamService {
	private static final String TAG = "AmbientMode";
	private static final boolean DEBUG = true;

	private DreamDelegate mDelegate;

	@Override
	public void onCreate() {
		if (DEBUG)
			Log.d(TAG, "Screensaver created");
		super.onCreate();
		mDelegate = new AmbientDreamDelegate();
		mDelegate.onCreate(this);
	}

	@Override
	public void onAttachedToWindow() {
		if (DEBUG)
			Log.d(TAG, "onAttachedToWindow");
		super.onAttachedToWindow();
		mDelegate.onAttachedToWindow(this);
	}

	@Override
	public void onDetachedFromWindow() {
		if (DEBUG)
			Log.d(TAG, "Screensaver onDetachedFromWindow");
		super.onDetachedFromWindow();
		mDelegate.onDetachedFromWindow(this);
	}

	@Override
	public void onDestroy() {
		if (DEBUG)
			Log.d(TAG, "Screensaver onDestroy");
		super.onDestroy();
		mDelegate.onDestroy(this);
	}

	@Override
	public void onDreamingStarted() {
		if (DEBUG)
			Log.d(TAG, "Screensaver onDreamingStarted");
		super.onDreamingStarted();
		mDelegate.onDreamingStarted(this);
	}

	@Override
	public void onDreamingStopped() {
		if (DEBUG)
			Log.d(TAG, "Screensaver onDreamingStopped");
		super.onDreamingStopped();
		mDelegate.onDreamingStopped(this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(mDelegate.dispatchKeyEvent(this, event)){
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
		if(mDelegate.dispatchTouchEvent(this, event)){
			return true;
		}
		return super.dispatchTouchEvent(event);
	}
}
