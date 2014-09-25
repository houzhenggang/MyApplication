package com.androidwear.home.watchfaces.ustwowatchfaces2.shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers.WatchCurrentTime;

public abstract class WatchFaceView extends View {

    public WatchFaceView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mHandler = new Handler();
        mIsAmbient = false;
        mFaceRect = new RectF();
        mCenterPoint = new PointF();
        mLastTime = null;
        mLastTimeIs24HourFormat = false;
        updateTimeRunnable = new Runnable() {
            public void run() {
                updateTime();
            }
        };
        mContext = context;
        mHandler.post(updateTimeRunnable);
        mWakeLock = ((PowerManager) context
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(1,
                "WatchFaceView");
        mWakeLock.setReferenceCounted(false);
    }

    private void updateTime() {
        WatchCurrentTime watchcurrenttime = WatchCurrentTime.getCurrent();
        if (mLastTime == null) {
            mLastTime = watchcurrenttime;
        }
        boolean is24Hour = DateFormat.is24HourFormat(getContext());
        if (mLastTime.get24Hour() != watchcurrenttime.get24Hour()
                || mLastTimeIs24HourFormat != is24Hour) {
            mLastTimeIs24HourFormat = is24Hour;
        }
        onInitializeTime(watchcurrenttime);
        onUpdateHour(watchcurrenttime);
        onUpdateHourContinuous(watchcurrenttime);
        onUpdateMinuteContinuous(watchcurrenttime);
        onUpdateMinute(watchcurrenttime);
        onUpdateSecondContinuous(watchcurrenttime);
        onUpdateSecond(watchcurrenttime);
        mLastTime = watchcurrenttime;
        mHandler.postDelayed(updateTimeRunnable, 40L);
    }

    public void destroy() {
        mHandler.removeCallbacks(updateTimeRunnable);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mHoldWakelockUntilDrawn) {
            if (Log.isLoggable("WatchFaceView", 3))
                Log.d("WatchFaceView",
                        (new StringBuilder())
                                .append("Releasing wake lock 40 ms from now ")
                                .append(System.currentTimeMillis()).toString());
            mWakeLock.acquire(40L);
            mHoldWakelockUntilDrawn = false;
        }
    }

    public PointF getCenter() {
        return mCenterPoint;
    }

    public float getCenterX() {
        return mCenterPoint.x;
    }

    public float getCenterY() {
        return mCenterPoint.y;
    }

    public float getFaceHeight() {
        return mFaceRect.height();
    }

    public float getFaceRadius() {
        return getFaceWidth() / 2.0F;
    }

    public RectF getFaceRect() {
        return mFaceRect;
    }

    public float getFaceWidth() {
        return mFaceRect.width();
    }

    public boolean isAmbient() {
        return mIsAmbient;
    }

    protected abstract boolean isContinuous();

    protected void onAmbientModeChanged(WatchCurrentTime watchcurrenttime) {
    }

    protected void onDraw(Canvas canvas) {
        if (!mIsAmbient) {
            if (!isContinuous()) {
                postInvalidateDelayed(40L);
                return;
            }
        }
        postInvalidateDelayed(100L);
    }

    protected void onInitializeTime(WatchCurrentTime watchcurrenttime) {
    }

    protected void onSizeChanged(int i, int j, int k, int l) {
        super.onSizeChanged(i, j, k, l);
        mFaceRect = new RectF(0.0F, 0.0F, i, i);
        mCenterPoint = new PointF(mFaceRect.width() / 2.0F,
                mFaceRect.height() / 2.0F);
        postInvalidate();
    }

    protected void onUpdateHour(WatchCurrentTime watchcurrenttime) {
    }

    protected void onUpdateHourContinuous(WatchCurrentTime watchcurrenttime) {
    }

    protected void onUpdateMinute(WatchCurrentTime watchcurrenttime) {
    }

    protected void onUpdateMinuteContinuous(WatchCurrentTime watchcurrenttime) {
    }

    protected void onUpdateSecond(WatchCurrentTime watchcurrenttime) {
    }

    protected void onUpdateSecondContinuous(WatchCurrentTime watchcurrenttime) {
    }

    public void postAmbientModeUpdate() {
        updateTime();
        mHoldWakelockUntilDrawn = true;
        mWakeLock.acquire(1000L);
        postInvalidate();
    }

    public void setAmbient(boolean flag) {
        boolean flag1 = mIsAmbient;
        boolean flag2 = false;
        if (flag != flag1)
            flag2 = true;
        mIsAmbient = flag;
        if (flag2) {
            if (mLastTime != null) {
                onAmbientModeChanged(mLastTime);
                onUpdateHourContinuous(mLastTime);
                onUpdateHour(mLastTime);
                onUpdateMinuteContinuous(mLastTime);
                onUpdateMinute(mLastTime);
                onUpdateSecondContinuous(mLastTime);
                onUpdateSecond(mLastTime);
            }
            postInvalidate();
            if (!mIsAmbient) {
                mHandler.post(updateTimeRunnable);
            }
        }
    }

    private PointF mCenterPoint;
    private Context mContext;
    private RectF mFaceRect;
    private Handler mHandler;
    private boolean mHoldWakelockUntilDrawn;
    private boolean mIsAmbient;
    private WatchCurrentTime mLastTime;
    private boolean mLastTimeIs24HourFormat;
    private android.os.PowerManager.WakeLock mWakeLock;
    private Runnable updateTimeRunnable;

}
