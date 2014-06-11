
package com.spreadst.cphonelockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import android.widget.ImageView;

/**
 * Displays the time
 */
public class DigitalClock extends LinearLayout {

    private Calendar mCalendar;
    private String mFormat;
    private ImageView mImgHour1;
    private ImageView mImgHour2;
    private ImageView mImgDot;
    private ImageView mImgMinute1;
    private ImageView mImgMinute2;
    private AmPm mImgAmPm;
    private ContentObserver mFormatChangeObserver;
    private int mAttached = 0;
    private Context mContext;
    private Resources res;

    private final Handler mHandler = new Handler();
    private BroadcastReceiver mIntentReceiver;

    protected static final String PACKAGE_NAME = "com.spreadst.cphonelockscreen";
    private int[] TIME_NUMBER_RESID = new int[10];

    private static class TimeChangedReceiver extends BroadcastReceiver {
        private WeakReference<DigitalClock> mClock;
        private Context mContext;

        public TimeChangedReceiver(DigitalClock clock) {
            mClock = new WeakReference<DigitalClock>(clock);
            mContext = clock.getContext();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Post a runnable to avoid blocking the broadcast.
            final boolean timezoneChanged =
                    intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED);
            final boolean localeChanged =
                    intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED);
            final DigitalClock clock = mClock.get();
            if (clock != null) {
                clock.mHandler.post(new Runnable() {
                    public void run() {
                        if (timezoneChanged) {
                            clock.mCalendar = Calendar.getInstance();
                        }
                        clock.updateTime();
                    }
                });
            } else {
                try {
                    mContext.unregisterReceiver(this);
                } catch (RuntimeException e) {
                    // Shouldn't happen
                }
            }
        }
    };

    private static class FormatChangeObserver extends ContentObserver {
        private WeakReference<DigitalClock> mClock;
        private Context mContext;

        public FormatChangeObserver(DigitalClock clock) {
            super(new Handler());
            mClock = new WeakReference<DigitalClock>(clock);
            mContext = clock.getContext();
        }

        @Override
        public void onChange(boolean selfChange) {
            DigitalClock digitalClock = mClock.get();
            if (digitalClock != null) {
                digitalClock.updateTime();
            } else {
                try {
                    mContext.getContentResolver().unregisterContentObserver(this);
                } catch (RuntimeException e) {
                    // Shouldn't happen
                }
            }
        }
    }

    public DigitalClock(Context context) {
        this(context, null);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        Log.i("huanghua", "DigitalClock");
        try {
            res = context.getPackageManager().getResourcesForApplication(
                    PACKAGE_NAME);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        TIME_NUMBER_RESID[0] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_0", null, null);
        TIME_NUMBER_RESID[1] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_1", null, null);
        TIME_NUMBER_RESID[2] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_2", null, null);
        TIME_NUMBER_RESID[3] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_3", null, null);
        TIME_NUMBER_RESID[4] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_4", null, null);
        TIME_NUMBER_RESID[5] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_5", null, null);
        TIME_NUMBER_RESID[6] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_6", null, null);
        TIME_NUMBER_RESID[7] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_7", null, null);
        TIME_NUMBER_RESID[8] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_8", null, null);
        TIME_NUMBER_RESID[9] = res.getIdentifier(PACKAGE_NAME + ":drawable/number_9", null, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i("huanghua", "onFinishInflate");

        mCalendar = Calendar.getInstance();

        mImgHour1 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/ic_time_hour1", null, null));
        mImgHour2 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/ic_time_hour2", null, null));
        mImgDot = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/ic_time_dot", null, null));
        mImgDot.setImageDrawable(res.getDrawable(res.getIdentifier(
                PACKAGE_NAME + ":drawable/colon", null, null)));
        mImgMinute1 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/ic_time_minute1", null, null));
        mImgMinute2 = (ImageView) findViewById(res.getIdentifier(
                PACKAGE_NAME + ":id/ic_time_minute2", null, null));
        mImgAmPm = new AmPm(this, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAttached++;

        /* monitor time ticks, time changed, timezone */
        if (mIntentReceiver == null) {
            mIntentReceiver = new TimeChangedReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter);
        }

        /* monitor 12/24-hour display preference */
        if (mFormatChangeObserver == null) {
            mFormatChangeObserver = new FormatChangeObserver(this);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.CONTENT_URI, true, mFormatChangeObserver);
        }
        Log.i("huanghua", "onAttachedToWindow");
        updateTime();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mAttached--;

        if (mIntentReceiver != null) {
            mContext.unregisterReceiver(mIntentReceiver);
        }
        if (mFormatChangeObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(
                    mFormatChangeObserver);
        }

        mFormatChangeObserver = null;
        mIntentReceiver = null;
    }

    void updateTime(Calendar c) {
        mCalendar = c;
        updateTime();
    }

    private void updateTime() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        boolean is24Format = android.text.format.DateFormat.is24HourFormat(getContext());
        int nCalendarHour = mCalendar.get(Calendar.HOUR);
        int nCalendarMinute = mCalendar.get(Calendar.MINUTE);
        int nCalendarAmPm = mCalendar.get(Calendar.AM_PM);
        int mHour1 = -1;
        int mHour2 = -1;
        int mMinute1 = -1;
        int mMinute2 = -1;

        if (is24Format) {
            if (nCalendarAmPm == 1) {
                nCalendarHour += 12;
            }

            mHour1 = nCalendarHour / 10;
            mHour2 = nCalendarHour % 10;

            mMinute1 = nCalendarMinute / 10;
            mMinute2 = nCalendarMinute % 10;

            mImgAmPm.setShowAmPm(false);

            if (mHour1 >= 0 && mHour1 <= 9) {
                mImgHour1.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mHour1]));
            }

            if (mHour2 >= 0 && mHour2 <= 9) {
                mImgHour2.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mHour2]));
            }

            if (mMinute1 >= 0 && mMinute1 <= 9) {
                mImgMinute1.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mMinute1]));
            }

            if (mMinute2 >= 0 && mMinute2 <= 9) {
                mImgMinute2.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mMinute2]));
            }
        } else {
            if (nCalendarHour == 0) {
                nCalendarHour = 12;
            }
            mHour1 = nCalendarHour / 10;
            mHour2 = nCalendarHour % 10;

            mMinute1 = nCalendarMinute / 10;
            mMinute2 = nCalendarMinute % 10;

            if (mHour1 >= 0 && mHour1 <= 2) {
                mImgHour1.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mHour1]));
            }

            if (mHour2 >= 0 && mHour2 <= 9) {
                mImgHour2.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mHour2]));
            }

            if (mMinute1 >= 0 && mMinute1 <= 9) {
                mImgMinute1.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mMinute1]));
            }

            if (mMinute2 >= 0 && mMinute2 <= 9) {
                mImgMinute2.setImageDrawable(res.getDrawable(TIME_NUMBER_RESID[mMinute2]));
            }

            mImgAmPm.setShowAmPm(true);
            mImgAmPm.setIsMorning(nCalendarAmPm == 0);
        }
    }

    static class AmPm {
        private TextView mAmPmTextView;
        private String mAmString, mPmString;

        AmPm(View parent, Typeface tf) {
            String[] ampm = new DateFormatSymbols().getAmPmStrings();
            mAmString = ampm[0];
            mPmString = ampm[1];
        }

        void setShowAmPm(boolean show) {
            if (mAmPmTextView != null) {
                mAmPmTextView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }

        void setIsMorning(boolean isMorning) {
            if (mAmPmTextView != null) {
                mAmPmTextView.setText(isMorning ? mAmString : mPmString);
            }
        }
    }
}
