package com.androidwear.home;

import android.service.dreams.DreamService;
import android.view.KeyEvent;
import android.view.MotionEvent;

public abstract interface DreamDelegate {
	public abstract boolean dispatchKeyEvent(DreamService dreamService,
			KeyEvent event);

	public abstract boolean dispatchTouchEvent(DreamService dreamService,
			MotionEvent event);

	public abstract void onAttachedToWindow(DreamService dreamService);

	public abstract void onCreate(DreamService dreamService);

	public abstract void onDestroy(DreamService dreamService);

	public abstract void onDetachedFromWindow(DreamService dreamService);

	public abstract void onDreamingStarted(DreamService dreamService);

	public abstract void onDreamingStopped(DreamService dreamService);
}
