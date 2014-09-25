package com.androidwear.home.view;

import android.animation.TimeAnimator;
import android.content.Context;
import android.util.AttributeSet;

public class SoundLevelImageView extends CircledImageView {
	private TimeAnimator mAnimator;
	private int mLastSpeechLevelDisplayed;
	private int mLastSpeechLevelReceived;
	private float mMinRadius;
	private float mRadiusDelta;

	public SoundLevelImageView(Context context) {
		this(context, null);
	}

	public SoundLevelImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SoundLevelImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mMinRadius = -1.0f;
		mAnimator = new TimeAnimator();
		mAnimator.setRepeatCount(-1);
		mAnimator.setDuration(1000L);
		mAnimator.setTimeListener(new TimeAnimator.TimeListener() {
			public void onTimeUpdate(TimeAnimator paramTimeAnimator,
					long paramLong1, long paramLong2) {
				SoundLevelImageView.this.updateRadius();
			}
		});
	}

	private void updateRadius() {
		if (mLastSpeechLevelReceived > mLastSpeechLevelDisplayed){
			mLastSpeechLevelDisplayed = Math.min(mLastSpeechLevelReceived,
					10 + mLastSpeechLevelDisplayed);
		}else{
			mLastSpeechLevelDisplayed = Math
					.max(mLastSpeechLevelReceived, -10 + mLastSpeechLevelDisplayed);
		}
		setCircleRadius(mMinRadius + mRadiusDelta
				* mLastSpeechLevelDisplayed / 100.0F);
	}

	public void setRmsDb(float db) {
		mLastSpeechLevelReceived = (10 * (int) (100.0F * (Math.min(
				Math.max(db, -2.0F), 10.0F) + 2.0F) / 12.0F) / 10);
	}

	public void startSoundLevelAnimation(
			AnimationManager animationManager, float maxRadius) {
		mMinRadius = getCircleRadius();
		mRadiusDelta = (maxRadius - mMinRadius);
		animationManager.startAnimation(mAnimator);
	}

	public void stopSoundAnimation() {
		mAnimator.cancel();
		if (mMinRadius != -1.0f){
			setCircleRadius(mMinRadius);
			mMinRadius = -1.0F;
		}
	}
}
