package com.androidwear.home.watchfaces;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class WatchFaceOptionWrapper extends FrameLayout {

	public WatchFaceOptionWrapper(Context context) {
		this(context, null);
	}

	public WatchFaceOptionWrapper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WatchFaceOptionWrapper(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return true;
	}
}
