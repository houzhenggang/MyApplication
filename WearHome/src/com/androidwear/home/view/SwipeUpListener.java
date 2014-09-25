package com.androidwear.home.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class SwipeUpListener implements View.OnTouchListener {

	private boolean mGestureDirectionLocked;
	private boolean mPossibleVerticalSwipeUp;
	private float mStartX;
	private float mStartY;
	private int mTouchSlop;

	public SwipeUpListener(Context paramContext) {
		mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
	}

	protected void onSwipeUp() {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mStartX = event.getX();
			mStartY = event.getY();
			mPossibleVerticalSwipeUp = true;
			mGestureDirectionLocked = false;
			break;
		case MotionEvent.ACTION_UP:
			if (mPossibleVerticalSwipeUp) {
				onSwipeUp();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			handlePossibleVerticalSwipe(event);
			break;
		}
		return false;
	}

	private boolean handlePossibleVerticalSwipe(MotionEvent event) {
		if (mGestureDirectionLocked) {
			return mPossibleVerticalSwipeUp;
		}
		float deltaX = Math.abs(mStartX - event.getX());
		float deltaY = Math.abs(mStartY - event.getY());
		float distance = deltaX * deltaX + deltaY * deltaY;

		if (distance > mTouchSlop * mTouchSlop) {
			if (deltaX > deltaY) {
				mPossibleVerticalSwipeUp = false;
			}
			mGestureDirectionLocked = true;
		}
		return mPossibleVerticalSwipeUp;
	}
}
