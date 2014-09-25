package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.View;

import com.androidwear.home.R;

public class BlackAnalogClock1 extends View {
    private static String TAG = "LgAnalogClock1";
    private boolean bFullSizeDayOfWeekImage;
    private boolean isActivityRunning;
    private boolean isTimerRunning;
    private boolean mAmbient;
    private boolean mAttached;
    private boolean mChanged;
    private int mClockAmbientThemeID;
    private TypedArray mClockTheme;
    private int mClockThemeID;
    private Context mContext;
    private TypedArray mDayMonthImageTheme;
    private Drawable mDayOfMonthImage;
    private Drawable mDial;
    private int mDialHeight;
    private int mDialWidth;
    private TypedArray mHandImageTheme;
    private final Handler mHandler;
    private boolean mHoldWakelockUntilDrawn;
    private float mHour;
    private Drawable mHourHand;
    private final BroadcastReceiver mIntentReceiver;
    private Drawable mMinuteHand;
    private float mMinutes;
    private float mSecond;
    private Drawable mSecondHand;
    private Handler mTick;
    private Calendar mTime;
    private String mTimeZone;
    private TypedArray mTypedArrays;
    private PowerManager.WakeLock mWakeLock;

    public BlackAnalogClock1(Context paramContext) {
        this(paramContext, null);
    }

    public BlackAnalogClock1(Context paramContext,
            AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public BlackAnalogClock1(Context paramContext,
            AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mHandler = new Handler();
        this.mTick = new Handler() {
            public void handleMessage(Message paramMessage) {
                switch (paramMessage.what) {
                case 0:
                    BlackAnalogClock1.this.updateTimeSecond();
                    break;
                default:
                }
            }
        };
        this.mIntentReceiver = new BroadcastReceiver() {
            public void onReceive(Context paramContext, Intent paramIntent) {
                if ("android.intent.action.TIMEZONE_CHANGED".equals(paramIntent
                        .getAction())) {
                    String str = paramIntent.getStringExtra("time-zone");
                    BlackAnalogClock1.this.createTime(str);
                }
                if (!("com.google.android.wearable.home.action.WEARABLE_TIME_TICK"
                        .equals(paramIntent.getAction()))) {
                    BlackAnalogClock1.this.holdWakelockUntilDrawn();
                    BlackAnalogClock1.this.postInvalidate();
                    BlackAnalogClock1.this.onTimeChanged();
                }
            }
        };
        this.mContext = paramContext;
        this.mTypedArrays = this.mContext.obtainStyledAttributes(
                paramAttributeSet, R.styleable.watchFace);
        this.mDial = this.mTypedArrays.getDrawable(12);
        this.mHourHand = this.mTypedArrays.getDrawable(13);
        this.mMinuteHand = this.mTypedArrays.getDrawable(14);
        this.mClockThemeID = this.mTypedArrays.getResourceId(18, 0);
        this.mClockAmbientThemeID = this.mTypedArrays.getResourceId(19, 0);
        if ((((this.mDial == null) || (this.mHourHand == null) || (this.mMinuteHand == null)))
                && (this.mClockThemeID == 0)) {
            this.mClockThemeID = R.array.watch_black_analog1_theme;
            this.mClockAmbientThemeID = R.array.watch_black_analog1_ambient_theme;
        }
        initialize();
    }

    private void createTime(String paramString) {
        if (paramString != null) {
            this.mTime = Calendar
                    .getInstance(TimeZone.getTimeZone(paramString));
        } else {
            this.mTime = Calendar.getInstance();
        }
    }

    private void createWakeLock() {
        this.mWakeLock = ((PowerManager) this.mContext
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(1, TAG);
        this.mWakeLock.setReferenceCounted(false);
    }

    private void getClockThemeResource() {
        Resources localResources = getResources();
        if (this.mAmbient) {
            this.mClockTheme = localResources
                    .obtainTypedArray(this.mClockAmbientThemeID);
            this.mHandImageTheme = localResources
                    .obtainTypedArray(this.mClockTheme.getResourceId(0, 0));
            this.mDial = this.mHandImageTheme.getDrawable(0);
            this.mHourHand = this.mHandImageTheme.getDrawable(1);
            this.mMinuteHand = this.mHandImageTheme.getDrawable(2);
        } else {
            try {
                this.mClockTheme = localResources
                        .obtainTypedArray(this.mClockThemeID);
                this.mHandImageTheme = localResources
                        .obtainTypedArray(this.mClockTheme.getResourceId(0, 0));
                this.mDial = this.mHandImageTheme.getDrawable(0);
                this.mHourHand = this.mHandImageTheme.getDrawable(1);
                this.mMinuteHand = this.mHandImageTheme.getDrawable(2);
                if (this.mHandImageTheme.length() >= 4) {
                    this.mSecondHand = this.mHandImageTheme.getDrawable(3);
                }
                this.mDayMonthImageTheme = localResources
                        .obtainTypedArray(this.mClockTheme.getResourceId(1, 1));
            } catch (Resources.NotFoundException localNotFoundException) {
                this.mDayMonthImageTheme = null;
            }
        }
    }

    private Drawable getDayOfMonthImage(int paramInt) {
        Drawable localDrawable = null;
        if ((paramInt >= 1) && (paramInt <= 31)
                && (this.mDayMonthImageTheme != null)) {
            localDrawable = this.mDayMonthImageTheme.getDrawable(paramInt - 1);
        }
        return localDrawable;
    }

    private void onTimeChanged() {
        long l = System.currentTimeMillis();
        this.mTime.setTimeInMillis(l);
        int i = this.mTime.get(10);
        int j = this.mTime.get(12);
        int k = this.mTime.get(13);
        this.mTime.get(7);
        int i1 = this.mTime.get(5);
        if (this.mDayMonthImageTheme != null) {
            this.mDayOfMonthImage = getDayOfMonthImage(i1);
            if ((this.mDayOfMonthImage.getMinimumWidth() == this.mDialWidth)
                    && (this.mDayOfMonthImage.getMinimumHeight() == this.mDialHeight))
                this.bFullSizeDayOfWeekImage = true;
        }
        this.mSecond = k;
        this.mMinutes = (j + k / 60.0F);
        this.mHour = (i + this.mMinutes / 60.0F);
        this.mChanged = true;
    }

    private boolean shouldTimerBeRunning() {
        if ((!(this.mAmbient)) && (this.isActivityRunning) && (this.mAttached)) {
            return true;
        }
        return false;
    }

    private void startTimerIfNecessary() {
        if (!(shouldTimerBeRunning()))
            return;
        this.isTimerRunning = true;
        getClockThemeResource();
        onTimeChanged();
        if (this.mSecondHand != null)
            this.mTick.sendEmptyMessage(0);
        postInvalidate();
        invalidate();
    }

    private void stopTimerIfNecessary() {
        if (shouldTimerBeRunning())
            return;
        this.isTimerRunning = false;
        this.mTick.removeMessages(0);
    }

    public void activityPaused() {
        this.isActivityRunning = false;
        stopTimerIfNecessary();
    }

    public void activityResumed() {
        this.isActivityRunning = true;
        startTimerIfNecessary();
    }

    public void holdWakelockUntilDrawn() {
        if ((!(this.mAmbient)) || (!(this.mAttached)))
            return;
        this.mHoldWakelockUntilDrawn = true;
        this.mWakeLock.acquire(300L);
    }

    public void initialize() {
        createWakeLock();
        getClockThemeResource();
        createTime(this.mTimeZone);
        this.mDialWidth = this.mDial.getIntrinsicWidth();
        this.mDialHeight = this.mDial.getIntrinsicHeight();
        this.isTimerRunning = true;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(this.mAttached)) {
            this.mAttached = true;
            IntentFilter localIntentFilter = new IntentFilter();
            localIntentFilter.addAction("android.intent.action.TIME_TICK");
            localIntentFilter
                    .addAction("com.google.android.wearable.home.action.WEARABLE_TIME_TICK");
            localIntentFilter.addAction("android.intent.action.TIME_SET");
            localIntentFilter
                    .addAction("android.intent.action.TIMEZONE_CHANGED");
            this.mContext.registerReceiver(this.mIntentReceiver,
                    localIntentFilter, null, this.mHandler);
        }
        createTime(this.mTimeZone);
        if (this.mSecondHand != null)
            this.mTick.sendEmptyMessage(0);
        onTimeChanged();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!(this.mAttached))
            return;
        this.mContext.unregisterReceiver(this.mIntentReceiver);
        this.mAttached = false;
        this.mTick.removeMessages(0);
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (this.mHoldWakelockUntilDrawn) {
            this.mWakeLock.acquire(40L);
            this.mHoldWakelockUntilDrawn = false;
        }
        boolean bool = this.mChanged;
        if (bool)
            this.mChanged = false;
        int i = getRight() - getLeft();
        int j = getBottom() - getTop();
        int k = i / 2;
        int l = j / 2;
        Drawable localDrawable1 = this.mDial;
        int i1 = localDrawable1.getIntrinsicWidth();
        int i2 = localDrawable1.getIntrinsicHeight();
        int i3 = 0;
        if ((i > i1) || (j > i2)) {
            i3 = 1;
            float f = Math.min(i / i1, j / i2);
            paramCanvas.save();
            paramCanvas.scale(f, f, k, l);
        }
        if (bool)
            localDrawable1.setBounds(k - (i1 / 2), l - (i2 / 2), k + i1 / 2, l
                    + i2 / 2);
        localDrawable1.draw(paramCanvas);
        paramCanvas.save();
        if ((this.mDayMonthImageTheme != null)
                && (this.mDayOfMonthImage != null)
                && (this.bFullSizeDayOfWeekImage)
                && (this.mDayOfMonthImage != null)) {
            Drawable localDrawable5 = this.mDayOfMonthImage;
            if (bool) {
                int i10 = localDrawable5.getIntrinsicWidth();
                int i11 = localDrawable5.getIntrinsicHeight();
                localDrawable5.setBounds(k - (i10 / 2), l - (i11 / 2), k + i10
                        / 2, l + i11 / 2);
            }
            localDrawable5.draw(paramCanvas);
            paramCanvas.save();
        }
        paramCanvas.save();
        paramCanvas.rotate(360.0F * this.mMinutes / 60.0F, k, l);
        Drawable localDrawable2 = this.mMinuteHand;
        if (bool) {
            int i8 = localDrawable2.getIntrinsicWidth();
            int i9 = localDrawable2.getIntrinsicHeight();
            localDrawable2.setBounds(k - (i8 / 2), l - (i9 / 2), k + i8 / 2, l
                    + i9 / 2);
        }
        localDrawable2.draw(paramCanvas);
        paramCanvas.restore();
        paramCanvas.save();
        paramCanvas.rotate(360.0F * this.mHour / 12.0F, k, l);
        Drawable localDrawable3 = this.mHourHand;
        if (bool) {
            int i6 = localDrawable3.getIntrinsicWidth();
            int i7 = localDrawable3.getIntrinsicHeight();
            localDrawable3.setBounds(k - (i6 / 2), l - (i7 / 2), k + i6 / 2, l
                    + i7 / 2);
        }
        localDrawable3.draw(paramCanvas);
        paramCanvas.restore();
        if ((!(this.mAmbient)) && (this.mSecondHand != null)) {
            paramCanvas.save();
            paramCanvas.rotate(360.0F * this.mSecond / 60.0F, k, l);
            Drawable localDrawable4 = this.mSecondHand;
            if (bool) {
                int i4 = localDrawable4.getIntrinsicWidth();
                int i5 = localDrawable4.getIntrinsicHeight();
                localDrawable4.setBounds(k - (i4 / 2), l - (i5 / 2),
                        k + i4 / 2, l + i5 / 2);
            }
            localDrawable4.draw(paramCanvas);
            paramCanvas.restore();
        }
        if (i3 == 0)
            return;
        paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        int i = View.MeasureSpec.getMode(paramInt1);
        int j = View.MeasureSpec.getSize(paramInt1);
        int k = View.MeasureSpec.getMode(paramInt2);
        int l = View.MeasureSpec.getSize(paramInt2);
        float f1 = 1.0F;
        float f2 = 1.0F;
        if ((i != 0) && (j < this.mDialWidth))
            f1 = j / this.mDialWidth;
        if ((k != 0) && (l < this.mDialHeight))
            f2 = l / this.mDialHeight;
        float f3 = Math.min(f1, f2);
        setMeasuredDimension(
                resolveSizeAndState((int) (f3 * this.mDialWidth), paramInt1, 0),
                resolveSizeAndState((int) (f3 * this.mDialHeight), paramInt2, 0));
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
            int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        this.mChanged = true;
    }

    public void setAmbient(boolean paramBoolean) {
        this.mAmbient = paramBoolean;
        getClockThemeResource();
        holdWakelockUntilDrawn();
        onTimeChanged();
        postInvalidate();
        invalidate();
        if ((this.mAttached) && (!(this.mAmbient))) {
            if (this.mSecondHand != null) {
                this.mTick.removeMessages(0);
                this.mTick.sendEmptyMessage(0);
            }
        }
    }

    public void updateTimeSecond() {
        if ((!(this.mAttached)) || (this.mAmbient)
                || (this.mSecondHand == null))
            return;
        onTimeChanged();
        invalidate();
        this.mTick.sendEmptyMessageDelayed(0, 1000L);
    }
}
