package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.util.CustomWatchFaceConstants;

public class DigitalImageClock extends RelativeLayout {
    private String TAG = "DigitalImageClock";
    private boolean isActivityRunning;
    private boolean isTimerRunning;
    private CharSequence mAmPm;
    private TextView mAmPmMarker;
    private boolean mAmbient;
    private boolean mAttached = false;
    private TypedArray mClockColors;
    private Context mContext;
    private CharSequence mDayOFWeek;
    private TypedArray mH1_icons;
    private TypedArray mH2_icons;
    private final Handler mHandler = new Handler();
    private boolean mHoldWakelockUntilDrawn;
    private ImageView mImageClock;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if ("android.intent.action.TIMEZONE_CHANGED".equals(paramIntent
                    .getAction())) {
                String str = paramIntent.getStringExtra("time-zone");
                DigitalImageClock.this.createTime(str);
            }

            if (!("com.google.android.wearable.home.action.WEARABLE_TIME_TICK"
                    .equals(paramIntent.getAction()))) {
                DigitalImageClock.this.holdWakelockUntilDrawn();
                DigitalImageClock.this.onTimeChanged();
                DigitalImageClock.this.postInvalidate();
            }
        }
    };
    private TypedArray mM1_icons;
    private TypedArray mM2_icons;
    private CharSequence mMonthAndDay;
    private boolean mSmallSizeStyleEnabled;
    private TextView mSubDayOfWeek;
    private TextView mSubMonthAndDay;
    private boolean mSubSizeStyleEnabled;
    private Calendar mTime;
    private String mTimeZone;
    private TypedArray mTypedArrays;
    private View mView;
    private PowerManager.WakeLock mWakeLock;

    public DigitalImageClock(Context paramContext,
            AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mContext = paramContext;
        this.mView = ((LayoutInflater) paramContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.digital_image_clock_layout, this);
        this.mImageClock = ((ImageView) this.mView
                .findViewById(R.id.number_images_clock));
        this.mAmPmMarker = ((TextView) this.mView.findViewById(R.id.ampmMarker));
        this.mSubDayOfWeek = ((TextView) this.mView
                .findViewById(R.id.subDayOfWeek));
        this.mSubMonthAndDay = ((TextView) this.mView
                .findViewById(R.id.subMonthAndDay));
        this.mTypedArrays = this.mContext.obtainStyledAttributes(
                paramAttributeSet, R.styleable.watchFace);
        this.mSmallSizeStyleEnabled = this.mTypedArrays.getBoolean(0, false);
        this.mSubSizeStyleEnabled = this.mTypedArrays.getBoolean(1, false);
        initialize(paramAttributeSet);
        createTime(this.mTimeZone);
    }

    private void createWakeLock() {
        this.mWakeLock = ((PowerManager) this.mContext
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(1,
                this.TAG);
        this.mWakeLock.setReferenceCounted(false);
    }

    private void getClockResource() {
        int i = this.mTypedArrays.getResourceId(16,
                R.array.watch_modi_theme_pop_yellow);
        if ((this.mAttached) && (this.mAmbient)) {
            i = this.mTypedArrays.getResourceId(17,
                    R.array.watch_modi_theme_pop_yellow);
        }
        Resources localResources = getResources();
        TypedArray localTypedArray = localResources.obtainTypedArray(i);
        this.mH1_icons = localResources.obtainTypedArray(localTypedArray
                .getResourceId(0, 0));
        this.mH2_icons = localResources.obtainTypedArray(localTypedArray
                .getResourceId(1, 1));
        this.mM1_icons = localResources.obtainTypedArray(localTypedArray
                .getResourceId(2, 2));
        this.mM2_icons = localResources.obtainTypedArray(localTypedArray
                .getResourceId(3, 3));
        this.mClockColors = localResources.obtainTypedArray(localTypedArray
                .getResourceId(4, 4));
    }

    private Drawable getFirstHourDigit(int paramInt) {
        Drawable localDrawable = this.mH1_icons.getDrawable(0);
        if ((paramInt >= 1) && (paramInt <= 2)) {
            localDrawable = this.mH1_icons.getDrawable(paramInt);
        }
        return localDrawable;
    }

    private Drawable getFirstMinuteDigit(int paramInt) {
        Drawable localDrawable = this.mM1_icons.getDrawable(0);
        if ((paramInt >= 1) && (paramInt <= 5)) {
            localDrawable = this.mM1_icons.getDrawable(paramInt);
        }
        return localDrawable;
    }

    private Drawable getSecondHourDigit(int paramInt) {
        Drawable localDrawable = this.mH2_icons.getDrawable(0);
        if ((paramInt >= 1) && (paramInt <= 9)) {
            localDrawable = this.mH2_icons.getDrawable(paramInt);
        }
        return localDrawable;
    }

    private Drawable getSecondMinuteDigit(int paramInt) {
        Drawable localDrawable = this.mM2_icons.getDrawable(0);
        if ((paramInt >= 1) && (paramInt <= 9)) {
            localDrawable = this.mM2_icons.getDrawable(paramInt);
        }
        return localDrawable;
    }

    private void initialize(AttributeSet paramAttributeSet) {
        createWakeLock();
        getClockResource();
        seBGColor();
        setAmPmMarker();
        setVisibilityOfDayOfWeekAndMonth();
        this.isTimerRunning = true;
    }

    private void seBGColor() {
        int i = this.mClockColors.getColor(0,
                R.color.watch_modi_pop_yellow_background);
        this.mImageClock.setBackgroundColor(i);
    }

    private void setAmPmMarker() {
        Resources localResources = getResources();
        int i = this.mClockColors.getColor(1, R.color.watch_white);
        this.mAmPmMarker.setTextColor(i);
        if (this.mSmallSizeStyleEnabled) {
            float f2 = this.mTypedArrays.getDimensionPixelSize(10,
                    (int) localResources
                            .getDimension(R.dimen.modi_small_ampm_text));
            RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(
                    -2, -2);
            localLayoutParams2.setMargins((int) localResources
                    .getDimension(R.dimen.modi_small_ampm_margin_left),
                    (int) localResources
                            .getDimension(R.dimen.modi_small_ampm_margin_top),
                    0, 0);
            this.mAmPmMarker.setLayoutParams(localLayoutParams2);
            this.mAmPmMarker.setTextSize(f2);
        }
        if (this.mSubSizeStyleEnabled) {
            float f1 = this.mTypedArrays.getDimensionPixelSize(10,
                    (int) localResources
                            .getDimension(R.dimen.modi_sub_ampm_text));
            RelativeLayout.LayoutParams localLayoutParams1 = new RelativeLayout.LayoutParams(
                    -2, -2);
            localLayoutParams1.setMargins((int) localResources
                    .getDimension(R.dimen.modi_sub_ampm_margin_left),
                    (int) localResources
                            .getDimension(R.dimen.modi_sub_ampm_margin_top), 0,
                    0);
            this.mAmPmMarker.setLayoutParams(localLayoutParams1);
            this.mAmPmMarker.setTextSize(f1);
        }
    }

    private void setVisibilityOfDayOfWeekAndMonth() {
        if (this.mSubSizeStyleEnabled) {
            return;
        }
        this.mSubDayOfWeek.setVisibility(View.GONE);
        this.mSubMonthAndDay.setVisibility(View.GONE);
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
        getClockResource();
        seBGColor();
        onTimeChanged();
        postInvalidate();
        invalidate();
    }

    private void stopTimerIfNecessary() {
        if (shouldTimerBeRunning())
            return;
        this.isTimerRunning = false;
    }

    private void updateImageClock(Calendar paramCalendar) {
        Resources localResources = getResources();
        boolean bool = is24HourModeEnabled();
        int i;
        String str;
        if (bool) {
            i = Integer.parseInt((String) DateFormat.format(
                    CustomWatchFaceConstants.TIME_24_HOUR_ONLY_FORMAT,
                    this.mTime));
        } else {
            i = Integer.parseInt((String) DateFormat.format(
                    CustomWatchFaceConstants.TIME_12_HOUR_ONLY_FORMAT,
                    this.mTime));
        }
        int j = Integer.parseInt((String) DateFormat.format(
                CustomWatchFaceConstants.TIME_MINUTE_ONLY_FORMAT, this.mTime));
        int k = i / 10;
        int l = i % 10;
        int i1 = j / 10;
        int i2 = j % 10;
        LayerDrawable localLayerDrawable = (LayerDrawable) localResources
                .getDrawable(R.drawable.digital_image_clock_layers);
        localLayerDrawable.setDrawableByLayerId(R.id.hour_first,
                getFirstHourDigit(k));
        localLayerDrawable.setDrawableByLayerId(R.id.hour_second,
                getSecondHourDigit(l));
        localLayerDrawable.setDrawableByLayerId(R.id.minute_first,
                getFirstMinuteDigit(i1));
        localLayerDrawable.setDrawableByLayerId(R.id.minute_second,
                getSecondMinuteDigit(i2));
        this.mImageClock.setImageDrawable(localLayerDrawable);
        DateFormat.format(CustomWatchFaceConstants.AM_PM_FORMAT, paramCalendar);
        if (paramCalendar.get(Calendar.AM_PM) != 0) {
            str = localResources.getString(R.string.pm_indicator);
        } else {
            str = localResources.getString(R.string.am_indicator);
        }
        if ((str != null) && (!str.equals(this.mAmPm))) {
            this.mAmPm = str;
            this.mAmPmMarker.setText(this.mAmPm);
        }
        if (!bool) {
            this.mAmPmMarker.setVisibility(View.VISIBLE);
        } else {
            this.mAmPmMarker.setVisibility(View.GONE);
        }
        if (this.mSmallSizeStyleEnabled) {
            CharSequence localCharSequence1 = DateFormat.format(
                    CustomWatchFaceConstants.MONTH_AND_DAY_FORMAT,
                    paramCalendar);
            if ((localCharSequence1 != null)
                    && (!(localCharSequence1.equals(this.mMonthAndDay)))) {
                this.mMonthAndDay = localCharSequence1;
                this.mSubMonthAndDay.setText(this.mMonthAndDay);
            }
            CharSequence localCharSequence2 = DateFormat.format(
                    CustomWatchFaceConstants.DAY_OF_WEEK_ONLY_FORMAT,
                    paramCalendar);
            if ((localCharSequence2 != null)
                    && (!(localCharSequence2.equals(this.mDayOFWeek)))) {
                this.mDayOFWeek = localCharSequence2;
                this.mSubDayOfWeek.setText(this.mDayOFWeek);
            }
        }
        if (this.mHoldWakelockUntilDrawn) {
            this.mWakeLock.acquire(40L);
            this.mHoldWakelockUntilDrawn = false;
        }
    }

    public void activityPaused() {
        this.isActivityRunning = false;
        stopTimerIfNecessary();
    }

    public void activityResumed() {
        this.isActivityRunning = true;
        startTimerIfNecessary();
    }

    private void createTime(String paramString) {
        if (paramString != null) {
            this.mTime = Calendar
                    .getInstance(TimeZone.getTimeZone(paramString));
        } else {
            this.mTime = Calendar.getInstance();
        }
    }

    public void holdWakelockUntilDrawn() {
        if ((!(this.mAmbient)) || (!(this.mAttached)))
            return;
        this.mHoldWakelockUntilDrawn = true;
        this.mWakeLock.acquire(300L);
    }

    public void invalidate() {
        super.invalidate();
        this.mImageClock.invalidate();
    }

    public boolean is24HourModeEnabled() {
        return DateFormat.is24HourFormat(getContext());
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
        onTimeChanged();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!(this.mAttached))
            return;
        this.mContext.unregisterReceiver(this.mIntentReceiver);
        this.mAttached = false;
    }

    public void onTimeChanged() {
        long l = System.currentTimeMillis();
        this.mTime.setTimeInMillis(l);
        updateImageClock(this.mTime);
    }

    public void postInvalidate() {
        super.postInvalidate();
        this.mImageClock.postInvalidate();
    }

    public void setAmbient(boolean paramBoolean) {
        this.mAmbient = paramBoolean;
        getClockResource();
        seBGColor();
        holdWakelockUntilDrawn();
        onTimeChanged();
        postInvalidate();
        invalidate();
    }
}
