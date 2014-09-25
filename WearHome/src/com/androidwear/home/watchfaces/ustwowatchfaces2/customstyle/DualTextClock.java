package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.util.CustomWatchFaceConstants;

public class DualTextClock extends FrameLayout {
    private String TAG = "DualTextClock";
    private boolean bSmallSizeStyleEnabled;
    private boolean bSubSizeStyleEnabled;
    private boolean isActivityRunning;
    private boolean isTimerRunning;
    private CharSequence mAmPm;
    private TextView mAmPmMarker;
    private boolean mAmbient;
    private boolean mAttached = false;
    private ImageView mBackgroundImage;
    private Drawable mBackgroundResource;
    private TextView mClock_hour;
    private TextView mClock_minute;
    private Context mContext;
    private CharSequence mDayOFWeekAndMonth;
    private TextView mDayOfWeek;
    private boolean mDayOfWeekAndMonthVisible = true;
    private final Handler mHandler = new Handler();
    private boolean mHoldWakelockUntilDrawn;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if ("android.intent.action.TIMEZONE_CHANGED".equals(paramIntent
                    .getAction())) {
                String str = paramIntent.getStringExtra("time-zone");
                DualTextClock.this.createTime(str);
            }
            if (!("com.google.android.wearable.home.action.WEARABLE_TIME_TICK"
                    .equals(paramIntent.getAction()))) {
                DualTextClock.this.holdWakelockUntilDrawn();
                DualTextClock.this.onTimeChanged();
                DualTextClock.this.postInvalidate();
            }
        }
    };
    private CharSequence mMonthAndDay;
    private TextView mMonthAndDayView;
    private Calendar mTime;
    private String mTimeZone;
    private TypedArray mTypedArrays;
    private PowerManager.WakeLock mWakeLock;

    public DualTextClock(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mContext = paramContext;
        View localView = ((LayoutInflater) paramContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.dual_text_watch_face, this);
        this.mClock_hour = ((TextView) localView.findViewById(R.id.clock_hour));
        this.mClock_minute = ((TextView) localView
                .findViewById(R.id.clock_minute));
        this.mBackgroundImage = ((ImageView) findViewById(R.id.backgroundimage));
        this.mAmPmMarker = ((TextView) localView.findViewById(R.id.ampmMarker));
        this.mDayOfWeek = ((TextView) localView.findViewById(R.id.dayOfWeek));
        this.mMonthAndDayView = ((TextView) localView
                .findViewById(R.id.monthAndDay));
        initialize(paramAttributeSet);
        createTime(this.mTimeZone);
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
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(1,
                this.TAG);
        this.mWakeLock.setReferenceCounted(false);
    }

    private void initialize(AttributeSet paramAttributeSet) {
        createWakeLock();
        this.mTypedArrays = this.mContext.obtainStyledAttributes(
                paramAttributeSet, R.styleable.watchFace);
        this.mDayOfWeekAndMonthVisible = this.mTypedArrays.getBoolean(40, true);
        this.mBackgroundResource = this.mTypedArrays.getDrawable(37);
        if (this.mBackgroundResource != null)
            this.mBackgroundImage.setImageDrawable(this.mBackgroundResource);
        this.bSubSizeStyleEnabled = this.mTypedArrays.getBoolean(1, false);
        this.bSmallSizeStyleEnabled = this.mTypedArrays.getBoolean(0, false);
        updateLayoutForEachStyle(this.bSubSizeStyleEnabled,
                this.bSmallSizeStyleEnabled);
        setVisibilityOfDayOfWeekAndMonth();
        setWatchTextSize(this.bSubSizeStyleEnabled, this.bSmallSizeStyleEnabled);
        updateWatchColor();
        this.isTimerRunning = true;
    }

    private void onTimeChanged() {
        long l = System.currentTimeMillis();
        this.mTime.setTimeInMillis(l);
        updateWatchTextContent(this.mTime);
    }

    private void setSmallSizeStyleLayout(Resources paramResources) {
        this.mAmPmMarker.setVisibility(View.GONE);
        FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams1
                .setMargins(
                        (int) paramResources
                                .getDimension(R.dimen.small_clock_hour_text_margin_left),
                        (int) paramResources
                                .getDimension(R.dimen.small_clock_hour_text_margin_top),
                        0, 0);
        this.mClock_hour.setGravity(3);
        this.mClock_hour.setLayoutParams(localLayoutParams1);
        FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams2
                .setMargins(
                        (int) paramResources
                                .getDimension(R.dimen.small_clock_minute_text_margin_left),
                        (int) paramResources
                                .getDimension(R.dimen.small_clock_minute_text_margin_top),
                        0, 0);
        this.mClock_minute.setGravity(3);
        this.mClock_minute.setLayoutParams(localLayoutParams2);
    }

    private void setSubSizeStyleLayout(Resources paramResources) {
        FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
                -2, -2);
        localLayoutParams1
                .setMargins(
                        (int) paramResources
                                .getDimension(R.dimen.sub_dual_clock_ampm_text_margin_left),
                        (int) paramResources
                                .getDimension(R.dimen.sub_dual_clock_ampm_text_margin_top),
                        0, 0);
        this.mAmPmMarker.setGravity(3);
        this.mAmPmMarker.setLayoutParams(localLayoutParams1);
        FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams2.setMargins((int) paramResources
                .getDimension(R.dimen.sub_clock_hour_text_margin_left),
                (int) paramResources
                        .getDimension(R.dimen.sub_clock_hour_text_margin_top),
                0, 0);
        this.mClock_hour.setGravity(3);
        this.mClock_hour.setLayoutParams(localLayoutParams2);
        FrameLayout.LayoutParams localLayoutParams3 = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams3
                .setMargins(
                        (int) paramResources
                                .getDimension(R.dimen.sub_clock_minute_text_margin_left),
                        (int) paramResources
                                .getDimension(R.dimen.sub_clock_minute_text_margin_top),
                        0, 0);
        this.mClock_minute.setGravity(3);
        this.mClock_minute.setLayoutParams(localLayoutParams3);
        FrameLayout.LayoutParams localLayoutParams4 = new FrameLayout.LayoutParams(
                -2, -2);
        localLayoutParams4
                .setMargins(
                        (int) paramResources
                                .getDimension(R.dimen.sub_dual_clock_month_text_margin_left),
                        (int) paramResources
                                .getDimension(R.dimen.sub_dual_clock_month_text_margin_top),
                        0, 0);
        this.mMonthAndDayView.setGravity(3);
        this.mMonthAndDayView.setLayoutParams(localLayoutParams4);
        FrameLayout.LayoutParams localLayoutParams5 = new FrameLayout.LayoutParams(
                -2, -2);
        int i = 3 + (int) paramResources
                .getDimension(R.dimen.sub_dual_clock_day_text_margin_top);
        if ("ko".equals(Locale.getDefault().getLanguage())) {
            i = (int) paramResources
                    .getDimension(R.dimen.sub_dual_clock_day_text_margin_top);
        }
        localLayoutParams5.setMargins((int) paramResources
                .getDimension(R.dimen.sub_dual_clock_day_text_margin_left), i,
                0, 0);
        this.mDayOfWeek.setGravity(3);
        this.mDayOfWeek.setLayoutParams(localLayoutParams5);
    }

    private void setVisibilityOfDayOfWeekAndMonth() {
        if (this.mDayOfWeekAndMonthVisible) {
            return;
        }
        this.mDayOfWeek.setVisibility(View.GONE);
        this.mMonthAndDayView.setVisibility(View.GONE);
    }

    private void setWatchTextSize(boolean subSizeStyleEnabled,
            boolean smallSizeStyleEnabled) {
        Resources localResources = getResources();
        float f1 = this.mTypedArrays.getDimensionPixelSize(9,
                (int) localResources.getDimension(R.dimen.dual_clock_text));
        float f2 = this.mTypedArrays
                .getDimensionPixelSize(10, (int) localResources
                        .getDimension(R.dimen.dual_clock_ampm_text));
        float f3 = this.mTypedArrays.getDimensionPixelSize(11,
                (int) localResources.getDimension(R.dimen.dual_clock_day_text));
        if (subSizeStyleEnabled) {
            f1 = (int) localResources.getDimension(R.dimen.sub_dual_clock_text);
            f2 = (int) localResources
                    .getDimension(R.dimen.sub_dual_clock_ampm_text);
            f3 = (int) localResources
                    .getDimension(R.dimen.sub_dual_clock_day_text);
        }
        if (smallSizeStyleEnabled) {
            f1 = (int) localResources
                    .getDimension(R.dimen.small_dual_clock_text);
            f2 = (int) localResources
                    .getDimension(R.dimen.small_dual_clock_ampm_text);
        }
        if (this.mDayOfWeekAndMonthVisible) {
            this.mDayOfWeek.setTextSize(f3);
            this.mMonthAndDayView.setTextSize(f3);
        }
        this.mClock_hour.setTextSize(f1);
        this.mClock_minute.setTextSize(f1);
        this.mAmPmMarker.setTextSize(f2);
    }

    private boolean shouldTimerBeRunning() {
        if ((!(this.mAmbient)) && (this.isActivityRunning) && (this.mAttached)) {
            return true;
        }
        return false;
    }

    private void startTimerIfNecessary() {
        if ((this.isTimerRunning) || (!(shouldTimerBeRunning())))
            return;
        this.isTimerRunning = true;
        updateWatchColor();
        onTimeChanged();
        postInvalidate();
        invalidate();
    }

    private void stopTimerIfNecessary() {
        if ((!(this.isTimerRunning)) || (shouldTimerBeRunning()))
            return;
        this.isTimerRunning = false;
    }

    private void updateBGImage() {
        Resources localResources = getResources();
        if (this.mAmbient) {
            Drawable localDrawable2 = localResources
                    .getDrawable(R.drawable.watch_modi_black_sub_bg);
            if (localDrawable2 != null) {
                this.mBackgroundImage.setImageDrawable(localDrawable2);
            }
        } else {
            Drawable localDrawable1 = localResources
                    .getDrawable(R.drawable.watch_blue_bg);
            if (localDrawable1 != null) {
                this.mBackgroundImage.setImageDrawable(localDrawable1);
            }
            if (this.mBackgroundResource != null) {
                this.mBackgroundImage
                        .setImageDrawable(this.mBackgroundResource);
            }
        }
        this.mBackgroundImage.postInvalidate();
    }

    private void updateLayoutForEachStyle(boolean subSizeStyleEnabled,
            boolean smallSizeStyleEnabled) {
        Resources localResources = getResources();
        if (subSizeStyleEnabled) {
            setSubSizeStyleLayout(localResources);
        }
        if (smallSizeStyleEnabled) {
            setSmallSizeStyleLayout(localResources);
        }
    }

    private void updateWatchColor() {
        Resources localResources = getResources();
        if (this.mAmbient) {
            int l = localResources.getColor(R.color.watch_white);
            if (this.mDayOfWeekAndMonthVisible) {
                this.mDayOfWeek.setTextColor(l);
                this.mMonthAndDayView.setTextColor(l);
            }
            this.mClock_hour.setTextColor(l);
            this.mClock_minute.setTextColor(l);
            this.mAmPmMarker.setTextColor(l);
        } else {
            int i = this.mTypedArrays.getColor(3, -1);
            int j = this.mTypedArrays.getColor(4, -1);
            int k = this.mTypedArrays.getColor(5, -1);
            if (this.mDayOfWeekAndMonthVisible) {
                this.mDayOfWeek.setTextColor(k);
                this.mMonthAndDayView.setTextColor(k);
            }
            this.mClock_hour.setTextColor(i);
            this.mClock_minute.setTextColor(i);
            this.mAmPmMarker.setTextColor(j);
        }
    }

    private void updateWatchTextContent(Calendar paramCalendar) {
        boolean bool = is24HourModeEnabled();
        Resources localResources = getResources();
        String str;
        CharSequence localCharSequence2;
        CharSequence localCharSequence1;
        if (bool) {
            this.mClock_hour.setText(DateFormat.format(
                    CustomWatchFaceConstants.TIME_24_HOUR_ONLY_FORMAT,
                    this.mTime));
        } else {
            this.mClock_hour.setText(DateFormat.format(
                    CustomWatchFaceConstants.TIME_12_HOUR_ONLY_FORMAT,
                    this.mTime));
        }
        this.mClock_minute.setText(DateFormat.format(
                CustomWatchFaceConstants.TIME_MINUTE_ONLY_FORMAT, this.mTime));
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
        if (!bSmallSizeStyleEnabled) {
            if (!bool) {
                this.mAmPmMarker.setVisibility(View.VISIBLE);
            } else {
                this.mAmPmMarker.setVisibility(View.GONE);
            }
        }
        if (this.mDayOfWeekAndMonthVisible) {
            if (!("ko".equals(Locale.getDefault().getLanguage()))) {
                localCharSequence2 = CustomWatchFaceConstants.DAY_OF_WEEK_ONLY_SHORT_FORMAT_FOR_KOREA;
                localCharSequence1 = CustomWatchFaceConstants.MONTH_AND_DAY_SHORT_FORMAT_FOR_KOREA;
            } else {
                localCharSequence1 = CustomWatchFaceConstants.DAY_OF_WEEK_ONLY_SHORT_FORMAT;
                localCharSequence2 = CustomWatchFaceConstants.MONTH_AND_DAY_SHORT_FORMAT;
            }
            CharSequence localCharSequence3 = DateFormat.format(
                    localCharSequence2, paramCalendar);
            CharSequence localCharSequence4 = DateFormat.format(
                    localCharSequence1, paramCalendar);
            if ((localCharSequence4 != null)
                    && (!(localCharSequence4.equals(this.mMonthAndDay)))) {
                this.mMonthAndDay = localCharSequence4;
                this.mMonthAndDayView.setText(this.mMonthAndDay);
            }
            if ((localCharSequence3 != null)
                    && (!(localCharSequence3.equals(this.mDayOFWeekAndMonth)))) {
                this.mDayOFWeekAndMonth = localCharSequence3;
                this.mDayOfWeek.setText(this.mDayOFWeekAndMonth);
            }
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

    public void holdWakelockUntilDrawn() {
        if ((!(this.mAmbient)) || (!(this.mAttached)))
            return;
        this.mHoldWakelockUntilDrawn = true;
        this.mWakeLock.acquire(300L);
    }

    public void invalidate() {
        super.invalidate();
        this.mClock_hour.invalidate();
        this.mClock_minute.invalidate();
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

    public void postInvalidate() {
        super.postInvalidate();
        this.mClock_hour.postInvalidate();
        this.mClock_minute.postInvalidate();
    }

    public void setAmbient(boolean paramBoolean) {
        this.mAmbient = paramBoolean;
        updateWatchColor();
        holdWakelockUntilDrawn();
        onTimeChanged();
        updateBGImage();
        postInvalidate();
        invalidate();
    }
}
