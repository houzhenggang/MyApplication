package com.androidwear.home.watchfaces;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.SimpleAnimatorListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.androidwear.home.R;
import com.androidwear.home.view.HomeView;

public class WatchFacePicker extends FrameLayout {

	private ComponentName mCurComponentName = null;
	private boolean mAnimatingHide;
	private HomeView mHomeView = null;
	private ViewPager mViewPager;
	private WatchFaceAdapter mAdapter;
	private final Point mInitialTouch = new Point();
	private final Point mCenter = new Point();

	public WatchFacePicker(Context context, ComponentName componentName, HomeView homeView) {
		super(context);
		mCurComponentName = componentName;
		mHomeView = homeView;
        this.mViewPager = new CarouselViewPager(context) {
            public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
                return (WatchFacePicker.this.mAnimatingHide)
                        || (super.onInterceptTouchEvent(paramMotionEvent));
            }

            public boolean onTouchEvent(MotionEvent paramMotionEvent) {
                return (WatchFacePicker.this.mAnimatingHide)
                        || (super.onTouchEvent(paramMotionEvent));
            }
        };
		mAdapter = new WatchFaceAdapter(context, mHomeView);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setClipChildren(false);
		mViewPager.setPageMargin(getViewPagerPageMargin());
		addView(mViewPager, -1, -1);
		int i = getHorizontalPadding();
		int j = getVerticalPadding();
		setPadding(i, j, i, j);
		setClipToPadding(false);
		setClipChildren(false);
		setBackgroundColor(getResources().getColor(R.color.watchface_picker_bg_color));
		int item = mAdapter.getIndexOf(componentName);
		mViewPager.setCurrentItem(item, false);
	}

	private int getViewPagerPageMargin() {
		return getResources().getDimensionPixelOffset(R.dimen.watchface_picker_page_margin);
	}

	private int getHorizontalPadding() {
		return getResources().getDimensionPixelOffset(R.dimen.watchface_picker_horizontal_padding);
	}

	private int getVerticalPadding() {
		return getResources().getDimensionPixelOffset(R.dimen.watchface_picker_vertical_padding);
	}

	private int getExtraBottomPadding(){
	    return getResources().getDimensionPixelOffset(R.dimen.watchface_picker_extra_bottom_padding);
	}

	public void destroy(){
		mAdapter.destroy();
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
	    mCenter.x = (w / 2);
	    mCenter.y = (h / 2);
	}
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        switch (paramMotionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
            this.mInitialTouch.x = (int) paramMotionEvent.getX();
            this.mInitialTouch.y = (int) paramMotionEvent.getY();
            break;
        default:
            break;
        }
        paramMotionEvent.offsetLocation(this.mCenter.x - this.mInitialTouch.x,
                this.mCenter.y - this.mInitialTouch.y);
        return this.mViewPager.dispatchTouchEvent(paramMotionEvent);
    }

	public void showWithAnimation(){
        View localView = (View) getParent();
        int i = localView.getWidth();
        int j = localView.getHeight();
        int k = getHorizontalPadding();
        int l = getVerticalPadding();
        int i1 = i - (k * 2);
        int i2 = j - (l * 2) - getExtraBottomPadding();
        float f1 = i / i1;
        float f2 = j / i2;
        float f3;
        if (f1 < f2) {
            f3 = f1;
        } else {
            f3 = f2;
        }
        setScaleX(1.2F);
        setScaleY(1.2F);
        setPivotX(i / 2);
        setPivotY(l + i2 / 2);
        setAlpha(0.0F);
        setVisibility(View.VISIBLE);
        animate().alpha(1.0F).scaleX(1.0F).scaleY(1.0F).setDuration(300L);
	}

    public void hideWithAnimation(
            final OnHideAnimationCompleteCallback paramOnHideAnimationCompleteCallback) {
        this.mAnimatingHide = true;
        animate().cancel();
        animate().alpha(0.0F).setDuration(300L)
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationComplete(Animator animator) {
                        super.onAnimationComplete(animator);
                        paramOnHideAnimationCompleteCallback.onHideComplete();
                    }
                });
    }

    public static abstract interface OnHideAnimationCompleteCallback {
        public abstract void onHideComplete();
    }
}
