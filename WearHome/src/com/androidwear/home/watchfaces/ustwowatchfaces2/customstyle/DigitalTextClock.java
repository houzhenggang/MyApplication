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
import android.os.Handler;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidwear.home.R;
import com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.util.CustomWatchFaceConstants;

public class DigitalTextClock extends FrameLayout {
    private String TAG = "DigitalTextClock";
    private boolean bFormat24Requested;
    private boolean isActivityRunning;
    private boolean isTimerRunning;
    private CharSequence mAmPm;
    private TextView mAmPmMarker;
    private boolean mAmbient;
    private boolean mAttached = false;
    private TextView mClock;
    private Context mContext;
    private CharSequence mDayOFWeekAndMonth;
    private TextView mDayOfWeekAndMonthAndDay;
    private final Handler mHandler = new Handler();
    private boolean mHoldWakelockUntilDrawn;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if ("android.intent.action.TIMEZONE_CHANGED".equals(paramIntent
                    .getAction())) {
                String str = paramIntent.getStringExtra("time-zone");
                DigitalTextClock.this.createTime(str);
            }
            if (!("com.google.android.wearable.home.action.WEARABLE_TIME_TICK"
                    .equals(paramIntent.getAction()))) {
                DigitalTextClock.this.onTimeChanged();
                DigitalTextClock.this.postInvalidate();
                DigitalTextClock.this.holdWakelockUntilDrawn();
            }
        }
    };
    private Calendar mTime;
    private String mTimeZone;
    private TypedArray mTypedArrays;
    private PowerManager.WakeLock mWakeLock;

    public DigitalTextClock(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mContext = paramContext;
        View localView = ((LayoutInflater) paramContext
                .getSystemService("layout_inflater")).inflate(
                R.layout.digital_text_watch_face, this);
        this.mClock = ((TextView) localView.findViewById(R.id.normal_clock));
        this.mDayOfWeekAndMonthAndDay = ((TextView) localView
                .findViewById(R.id.dayOfWeekAndMonthAndDay));
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
        updateWatchColor();
        this.isTimerRunning = true;
    }

    private void onTimeChanged() {
        long l = System.currentTimeMillis();
        this.mTime.setTimeInMillis(l);
        updateWatchTextContent(this.mTime);
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

    private void updateWatchColor() {
        Resources localResources = getResources();
        int l;
        int i;
        int j;
        int k;
        if (this.mAmbient) {
            l = localResources.getColor(R.color.watch_white);
            i = this.mTypedArrays.getColor(3, -1);
            j = this.mTypedArrays.getColor(3, -1);
            k = l;
        } else {
            i = this.mTypedArrays.getColor(3, -1);
            j = this.mTypedArrays.getColor(4, -1);
            k = this.mTypedArrays.getColor(5,
                    localResources.getColor(R.color.watch_white));
        }
        this.mClock.setTextColor(i);
        if (this.mAmPmMarker != null) {
            this.mAmPmMarker.setTextColor(j);
        }
        this.mDayOfWeekAndMonthAndDay.setTextColor(k);

    }

    private void updateWatchTextContent(Calendar paramCalendar) {
        boolean bool = is24HourModeEnabled();
        int i = paramCalendar.get(11);
        CharSequence localCharSequence1 = DateFormat.format(
                CustomWatchFaceConstants.AM_PM_FORMAT, paramCalendar);
        CharSequence localCharSequence2 = CustomWatchFaceConstants.DAY_OF_WEEK_AND_MONTH_DATE_FORMAT;
        if ("ko".equals(Locale.getDefault().getLanguage()))
            localCharSequence2 = CustomWatchFaceConstants.DAY_OF_WEEK_AND_MONTH_DATE_FORMAT_FOR_KOREA;
        CharSequence localCharSequence3 = DateFormat.format(localCharSequence2,
                paramCalendar);
        if (bool) {
            if (this.bFormat24Requested != bool) {
                if (this.mAmPmMarker != null)
                    this.mAmPmMarker.setVisibility(View.GONE);
                Resources localResources = getResources();
                FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
                        -1, -1);
                localLayoutParams
                        .setMargins(
                                0,
                                (int) localResources
                                        .getDimension(R.dimen.digital_normal_clock_text_margin_top),
                                0, 0);
                this.mClock.setGravity(1);
                this.mClock.setLayoutParams(localLayoutParams);
            }
            this.bFormat24Requested = bool;
            this.mClock.setText(DateFormat.format(
                    CustomWatchFaceConstants.TIME_24_HOUR_FORMAT, this.mTime));
        } else {
            if ((this.bFormat24Requested != bool) && (this.mAmPmMarker != null))
                this.mAmPmMarker.setVisibility(View.VISIBLE);
            this.bFormat24Requested = bool;
            if ((localCharSequence1 != null)
                    && (!(localCharSequence1.equals(this.mAmPm)))) {
                this.mAmPm = localCharSequence1;
                if (this.mAmPmMarker != null)
                    this.mAmPmMarker.setText(this.mAmPm);
            }
            if ((i < 10) || ((i >= 13) && (i < 22)))
                this.mClock
                        .setText(DateFormat
                                .format(CustomWatchFaceConstants.TIME_ZERO_REMOVED_12_HOUR_FORMAT,
                                        this.mTime));
            this.mClock.setText(DateFormat.format(
                    CustomWatchFaceConstants.TIME_12_HOUR_FORMAT, this.mTime));
        }
        if ((localCharSequence3 != null)
                && (!(localCharSequence3.equals(this.mDayOFWeekAndMonth)))) {
            this.mDayOFWeekAndMonth = localCharSequence3;
            this.mDayOfWeekAndMonthAndDay.setText(this.mDayOFWeekAndMonth);
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
        this.mClock.invalidate();
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
        this.mClock.postInvalidate();
    }

    public void setAmbient(boolean paramBoolean) {
        this.mAmbient = paramBoolean;
        updateWatchColor();
        holdWakelockUntilDrawn();
        onTimeChanged();
        postInvalidate();
        invalidate();
    }
}
