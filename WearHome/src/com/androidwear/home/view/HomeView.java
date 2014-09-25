/**
 * @File StatusBarView.java
 * @Auth Haosu
 * @Date 2014.08.15
 * @Desc The root view of Home.
 */
package com.androidwear.home.view;

import com.androidwear.home.HomeActivity;
import com.androidwear.home.R;
import com.androidwear.home.statusbar.StatusBarController;
import com.androidwear.home.utils.PreferenceUtils;
import com.androidwear.home.watchfaces.WatchFacePicker;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HomeView extends RelativeLayout {

    /* Invalid pointer id */
    private static final int INVALID_POINTER_ID = -1;

    /* Original point coordination */
    private static final Point ORIGINAL_POINT = new Point(0, 0);

    /* The touch slop which determine moved threshold */
    private final int mTouchSlop;

    /* Record the actived pointer id. INVALID_POINTER_ID means no actived point */
    private int mActivedPointerId = INVALID_POINTER_ID;

    /* The point that we pressed down */
    private PointF mPtStart = new PointF(ORIGINAL_POINT);
    /* The point that move start */
    private PointF mPtFrom = new PointF(ORIGINAL_POINT);
    /* The point that move end */
    private PointF mPtTo = new PointF(ORIGINAL_POINT);

    /* Which controll the status bar swipe */
    private StatusBarController mStatusBarController;

    private ImageView mForegroundMask;
    private HomeActivity mHomeActivity = null;
    private FrameLayout mClockLayout = null;
    private View mWatchFace;
    private ComponentName mWatchFaceComponent = null;
    private GestureDetector mWatchFaceLongPressDetector;
    private WatchFacePicker mWatchFacePicker;
    private Vibrator mVibrator;

    public HomeView(Context context) {
        this(context, null);
    }

    public HomeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);

        /* Get the TouchSlop from configuation */
        ViewConfiguration conf = ViewConfiguration.get(context);
        mTouchSlop = conf.getScaledTouchSlop();
        mVibrator = ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE));
        setupViews(context);
    }

    private void setupViews(Context context) {
        /* Inflate the HomeView */
        LayoutInflater.from(context).inflate(R.layout.home_view_layout, this, true);

        /* Intialize mStatusBarController */
        mStatusBarController = new StatusBarController(context, this);

        mForegroundMask = (ImageView) findViewById(R.id.fg_mask);
        mForegroundMask.setAlpha(0.0f);

        mClockLayout = (FrameLayout)findViewById(R.id.clock_view);

		mWatchFaceLongPressDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					public void onLongPress(MotionEvent event) {
						super.onLongPress(event);
						tryToShowWatchFacePicker(true, true);
					}

					public boolean onSingleTapUp(MotionEvent event) {
						return super.onSingleTapUp(event);
					}
				});
    }

    public void setHomeActivity(HomeActivity homeActivity){
    	mHomeActivity = homeActivity;
    	loadPreferredWatchFace();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	mWatchFaceLongPressDetector.onTouchEvent(ev);
        int act = ev.getAction();
        switch (act & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            handlePointerDown(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            handlePointerMove(ev);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            handlePointerUp(ev);
            break;

        default:
            break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	mWatchFaceLongPressDetector.onTouchEvent(ev);
        int act = ev.getAction();
        boolean ret = false;
        switch (act & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            ret = handlePointerDown(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            ret = handlePointerMove(ev);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            ret = handlePointerUp(ev);
            break;

        default:
            break;
        }
        return ret || super.onInterceptTouchEvent(ev);
    }

    private boolean handlePointerUp(MotionEvent ev) {
        /* Point is released, so that set mActivedPointerId to invalid */
        mActivedPointerId = INVALID_POINTER_ID;
        /* Make all swipe views animate back */
        animateBack();
        return false;
    }

    private boolean handlePointerMove(MotionEvent ev) {
        if (mWatchFacePicker != null) {
            return false;
        }
        final int ptIdx = ev.findPointerIndex(mActivedPointerId);
        if (ptIdx == -1) {
            /* If the ActivedPointerId is released or invalid, return. */
            return false;
        }

        final float y = ev.getY(ptIdx);
        final int yMove = (int) Math.abs(y - mPtFrom.y);
        final boolean bYMoved = yMove > mTouchSlop;

        if (bYMoved) {
            /* If Y-axis is moved, perform status bar swipe */
            mPtTo.set(ev.getX(ptIdx), y);
            performStatusBarSwipe(y);
            mPtFrom.set(mPtTo);
        }
        return bYMoved;
    }

    private void performStatusBarSwipe(float y) {
        float yDelta = y - mPtStart.y;
        mStatusBarController.maybeSwipe(yDelta);
    }

    private boolean handlePointerDown(MotionEvent ev) {
        /* Note point pressed coordinates and states */
        mActivedPointerId = ev.getPointerId(0);
        mPtStart.set(ev.getX(), ev.getY());
        mPtFrom.set(mPtStart);
        mPtTo.set(mPtStart);
        return false;
    }

    public void animateBack() {
        /* Make status bar animate back */
        mStatusBarController.animateBack();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mStatusBarController.start(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        mStatusBarController.stop(getContext());
        super.onDetachedFromWindow();
    }

    public void loadPreferredWatchFace(){
    	ComponentName name = PreferenceUtils.getWatchFace(getContext());//new ComponentName("com.androidwear.lock","com.androidwear.lock.MainActivity");//
    	setWatchFace(name);
    }

    private void setWatchFace(ComponentName componentName){
    	mWatchFaceComponent = componentName;
    	View faceView = null;
    	if (componentName != null){
    		faceView = buildWatchFace(componentName);
    	}else{
    		faceView = buildDefaultWatchFace();
    	}
        setWatchFaceView(faceView);
        PreferenceUtils.saveWatchFace(getContext(), componentName);
    }

    private void setWatchFaceView(View faceView){
    	if(mWatchFace != null && mClockLayout.indexOfChild(mWatchFace) != -1){
    		mClockLayout.removeAllViews();
    		Object obj = mWatchFace.getTag();
    		if(obj != null && obj instanceof ComponentName){
    			ComponentName tagComponent = (ComponentName)obj;
    			if(tagComponent != null){
    				String tag = mHomeActivity.getActvityTag(tagComponent);
    				mHomeActivity.destroyActivity(tag, true);
    			}
    		}
    	}
    	mWatchFace = faceView;
    	mClockLayout.addView(faceView);
    }

    private View buildWatchFace(ComponentName component){
    	return mHomeActivity.getActivityView(component);
    }

    private View buildDefaultWatchFace(){
    	 return LayoutInflater.from(getContext()).inflate(R.layout.layout_digitalclock, null, false);
    }

    private void tryToShowWatchFacePicker(boolean isVibrator, boolean animation){
		if (canShowWatchFacePicker()) {
			mWatchFacePicker = new WatchFacePicker(getContext(), mWatchFaceComponent, this);
			mWatchFacePicker.setVisibility(View.GONE);
			mHomeActivity.setTouchEable(false);
			addView(mWatchFacePicker, -1, -1);
			if (isVibrator) {
				mVibrator.vibrate(75L);
			}
			if (animation) {
				mWatchFacePicker.showWithAnimation();
			} else {
				mWatchFacePicker.setVisibility(View.VISIBLE);
			}
		}
    }

    private boolean canShowWatchFacePicker(){
        if (mWatchFacePicker == null
                || mWatchFacePicker.getVisibility() != View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void onSelectWatchFace(ComponentName component){
      setWatchFace(component);
      dismissWatchfacePickerIfNecessary(false);
    }

    private void dismissWatchfacePickerIfNecessary(boolean paramBoolean) {
        if (this.mWatchFacePicker != null) {
            mWatchFacePicker
                    .hideWithAnimation(new WatchFacePicker.OnHideAnimationCompleteCallback() {
                        public void onHideComplete() {
                            mWatchFacePicker.setVisibility(View.GONE);
                            mWatchFacePicker.destroy();
                            HomeView.this.removeView(mWatchFacePicker);
                            mHomeActivity.setTouchEable(true);
                            mWatchFacePicker = null;
                        }
                    });
        }
        PowerManager.WakeLock localWakeLock = ((PowerManager) getContext()
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "reset");
        localWakeLock.acquire();
        localWakeLock.release();
    }
}