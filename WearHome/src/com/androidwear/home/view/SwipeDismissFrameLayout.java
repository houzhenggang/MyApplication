package com.androidwear.home.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class SwipeDismissFrameLayout extends SwipeDismissLayout implements
		SwipeDismissLayout.OnDismissedListener,
		SwipeDismissLayout.OnSwipeProgressChangedListener {

	private int mAnimationTime;
	private ArrayList<DismissCallbacks> mCallbacks;
	private DecelerateInterpolator mCancelInterpolator;
	private AccelerateInterpolator mDismissInterpolator;
	private boolean mStarted;

	public SwipeDismissFrameLayout(Context context) {
		this(context, null);
	}

	public SwipeDismissFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeDismissFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setOnDismissedListener(this);
		setOnSwipeProgressChangedListener(this);
		mCallbacks = new ArrayList<DismissCallbacks>();
		mAnimationTime = getContext().getResources().getInteger(
				android.R.integer.config_longAnimTime);
		mCancelInterpolator = new DecelerateInterpolator(1.5F);
		mDismissInterpolator = new AccelerateInterpolator(1.5F);
	}

	public void removeCallbacks(DismissCallbacks callbacks) {
		mCallbacks.remove(callbacks);
	}

	public void addCallbacks(DismissCallbacks callbacks) {
		mCallbacks.add(callbacks);
	}

	private boolean isDismissalSuppressed() {
		for (DismissCallbacks callback : mCallbacks) {
			if (callback.canDismiss()) {
				return true;
			}
		}
		return false;
	}

	protected boolean canScroll(View v, boolean checkV, float dx, float x,
			float y) {
		if (isDismissalSuppressed()) {
			return super.canScroll(v, checkV, dx, x, y);
		} else {
			return false;
		}
	}

	public void dismiss() {
		for (DismissCallbacks callback : mCallbacks) {
			callback.onSwipeStart();
		}
		onDismissed(this);
	}

	public void reset() {
		animate().cancel();
		setTranslationX(0.0F);
		setAlpha(1.0F);
		mStarted = false;
	}

	public static abstract interface DismissCallbacks {
		public abstract boolean canDismiss();

		public abstract void onDismissed(SwipeDismissFrameLayout layout);

		public abstract void onSwipeCancelled();

		public abstract void onSwipeStart();
	}

	@Override
	public void onSwipeProgressChanged(SwipeDismissLayout layout,
			float progress, float translate) {
		// TODO Auto-generated method stub
		setTranslationX(translate);
		setAlpha(1.0F - (0.5F * progress));
		if (mStarted) {
			return;
		}
		for (DismissCallbacks callback : mCallbacks) {
			callback.onSwipeCancelled();
		}
		mStarted = true;
	}

	@Override
	public void onSwipeCancelled(SwipeDismissLayout layout) {
		// TODO Auto-generated method stub
		mStarted = false;
		animate().translationX(0.0F).alpha(1.0F).setDuration(mAnimationTime)
				.setInterpolator(mCancelInterpolator)
				.withEndAction(new Runnable() {
					public void run() {
						for (DismissCallbacks callback : mCallbacks) {
							callback.onSwipeCancelled();
						}
					}
				});
	}

	@Override
	public void onDismissed(SwipeDismissLayout layout) {
		// TODO Auto-generated method stub
		animate().translationX(getWidth()).alpha(0.0F)
				.setDuration(mAnimationTime)
				.setInterpolator(mDismissInterpolator)
				.withEndAction(new Runnable() {
					public void run() {
						for (DismissCallbacks callback : mCallbacks) {
							callback.onDismissed(SwipeDismissFrameLayout.this);
						}
					}
				});
	}

}
