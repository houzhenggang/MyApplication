
package com.miracle.sphonesocialwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SphoneTimeService extends Service {

    private int[] mAmPm = new int[] {
            R.drawable.dock_mode_clock_am, R.drawable.dock_mode_clock_pm
    };
    private int[] mTime = new int[] {
            R.drawable.dock_mode_clock_0,
            R.drawable.dock_mode_clock_1,
            R.drawable.dock_mode_clock_2,
            R.drawable.dock_mode_clock_3,
            R.drawable.dock_mode_clock_4,
            R.drawable.dock_mode_clock_5,
            R.drawable.dock_mode_clock_6,
            R.drawable.dock_mode_clock_7,
            R.drawable.dock_mode_clock_8,
            R.drawable.dock_mode_clock_9,
    };
    private int[] mWeekDay = new int[] {
            R.string.htc_sun,
            R.string.htc_mon,
            R.string.htc_tue,
            R.string.htc_wed,
            R.string.htc_thu,
            R.string.htc_fri,
            R.string.htc_sat,
    };
    private int mHourlayoutType = 2;
    private int mMinuterlayoutType = 2;
    private int mLastHour;
    private int mLastMinutes;
    private boolean mFirstRun = false;
    private Calendar mCalendar;
    private Context mWidgetContext;
    public static AppWidgetManager mAppWidgetManager;
    public static int sAppWidgetIds[];

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            UpdateTimeUI();
        }
    };

    @Override
    public void onCreate() {
        mFirstRun = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mBroadcastReceiver, intentFilter);
        super.onCreate();
    }

    private void UpdateTimeUI() {
        mWidgetContext = getApplicationContext();
        mAppWidgetManager = AppWidgetManager.getInstance(mWidgetContext);
        mCalendar = Calendar.getInstance(TimeZone.getDefault());
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        boolean b24 = DateFormat.is24HourFormat(getApplicationContext());
        RemoteViews views = new RemoteViews(mWidgetContext.getPackageName(),
                R.layout.sphone_social_appwidget);

        Date date = mCalendar.getTime();
        int hour = date.getHours();
        int minutes = date.getMinutes();

        if (b24) {
            views.setViewVisibility(R.id.am_pm, View.GONE);
        } else {
            views.setViewVisibility(R.id.am_pm, View.VISIBLE);
            if (hour > 12) {
                hour -= 12;
                views.setImageViewResource(R.id.am_pm, mAmPm[1]);
            } else if (hour == 0) {
                hour = 12;
                views.setImageViewResource(R.id.am_pm, mAmPm[0]);
            } else if (hour == 12) {
                views.setImageViewResource(R.id.am_pm, mAmPm[1]);
            } else {
                views.setImageViewResource(R.id.am_pm, mAmPm[0]);
            }
        }
        if (hour < 10) {
            RemoteViews layout1 = new RemoteViews(mWidgetContext.getPackageName(),
                    R.layout.sphone_social_time_hour_one_noanim_layout1);
            views.removeAllViews(R.id.layout1);
            views.addView(R.id.layout1, layout1);
            RemoteViews layout2 = new RemoteViews(mWidgetContext.getPackageName(),
                    R.layout.sphone_social_time_hour_one_noanim_layout2);
            views.removeAllViews(R.id.layout2);
            views.addView(R.id.layout2, layout2);
            RemoteViews layout3 = new RemoteViews(mWidgetContext.getPackageName(),
                    R.layout.sphone_social_time_hour_one_noanim_layout3);
            views.removeAllViews(R.id.layout3);
            views.addView(R.id.layout3, layout3);
            RemoteViews layout4 = new RemoteViews(mWidgetContext.getPackageName(),
                    R.layout.sphone_social_time_hour_one_noanim_layout4);
            views.removeAllViews(R.id.layout4);
            views.addView(R.id.layout4, layout4);
        }

        if (mLastHour != hour) {
            if (mHourlayoutType == 1) {
                if (hour >= 10) {
                    RemoteViews layout1 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_layout1);
                    views.removeAllViews(R.id.layout1);
                    views.addView(R.id.layout1, layout1);
                    RemoteViews layout2 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_layout2);
                    views.removeAllViews(R.id.layout2);
                    views.addView(R.id.layout2, layout2);
                    RemoteViews layout3 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_layout3);
                    views.removeAllViews(R.id.layout3);
                    views.addView(R.id.layout3, layout3);
                    RemoteViews layout4 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_layout4);
                    views.removeAllViews(R.id.layout4);
                    views.addView(R.id.layout4, layout4);
                } else {
                    RemoteViews layout1 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_one_layout1);
                    views.removeAllViews(R.id.layout1);
                    views.addView(R.id.layout1, layout1);
                    RemoteViews layout2 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_one_layout2);
                    views.removeAllViews(R.id.layout2);
                    views.addView(R.id.layout2, layout2);
                    RemoteViews layout3 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_one_layout3);
                    views.removeAllViews(R.id.layout3);
                    views.addView(R.id.layout3, layout3);
                    RemoteViews layout4 = new RemoteViews(mWidgetContext.getPackageName(),
                            R.layout.sphone_social_time_hour_one_layout4);
                    views.removeAllViews(R.id.layout4);
                    views.addView(R.id.layout4, layout4);
                }
            } else {
                mHourlayoutType = 1;
            }
        }
        if (hour >= 10) {
            int last = hour - 1;
            views.setImageViewResource(R.id.image1, mTime[hour / 10]);
            views.setImageViewResource(R.id.image2, mTime[hour % 10]);
            views.setImageViewResource(R.id.image3, mTime[last / 10]);
            views.setImageViewResource(R.id.image4, mTime[last % 10]);
            views.setImageViewResource(R.id.image5, mTime[last / 10]);
            views.setImageViewResource(R.id.image6, mTime[last % 10]);
            views.setImageViewResource(R.id.image7, mTime[hour / 10]);
            views.setImageViewResource(R.id.image8, mTime[hour % 10]);
        } else {
            int last = hour - 1;
            if (hour == 1 && !b24) {
                last = 12;
            } else if (hour == 0 && b24) {
                last = 23;
            }
            if (mFirstRun) {
                last = hour;
            }
            if (last > 10) {
                views.setImageViewResource(R.id.image2, mTime[hour]);
                views.setImageViewResource(R.id.image4, mTime[last % 10]);
                views.setImageViewResource(R.id.image6, mTime[last % 10]);
                views.setImageViewResource(R.id.image8, mTime[hour]);
            } else {
                views.setImageViewResource(R.id.image2, mTime[hour]);
                views.setImageViewResource(R.id.image4, mTime[last]);
                views.setImageViewResource(R.id.image6, mTime[last]);
                views.setImageViewResource(R.id.image8, mTime[hour]);
            }
        }
        mLastHour = hour;
        if (mLastMinutes != minutes) {
            if (mMinuterlayoutType == 1) {
                RemoteViews layout1 = new RemoteViews(mWidgetContext.getPackageName(),
                        R.layout.sphone_social_time_minuter_layout1);
                views.removeAllViews(R.id.right_layout1);
                views.addView(R.id.right_layout1, layout1);
                RemoteViews layout2 = new RemoteViews(mWidgetContext.getPackageName(),
                        R.layout.sphone_social_time_minuter_layout2);
                views.removeAllViews(R.id.right_layout2);
                views.addView(R.id.right_layout2, layout2);
                RemoteViews layout3 = new RemoteViews(mWidgetContext.getPackageName(),
                        R.layout.sphone_social_time_minuter_layout3);
                views.removeAllViews(R.id.right_layout3);
                views.addView(R.id.right_layout3, layout3);
                RemoteViews layout4 = new RemoteViews(mWidgetContext.getPackageName(),
                        R.layout.sphone_social_time_minuter_layout4);
                views.removeAllViews(R.id.right_layout4);
                views.addView(R.id.right_layout4, layout4);
            } else {
                mMinuterlayoutType = 1;
            }
        }
        if (minutes >= 10) {
            int last = minutes - 1;
            views.setImageViewResource(R.id.right_image1, mTime[minutes / 10]);
            views.setImageViewResource(R.id.right_image2, mTime[minutes % 10]);
            views.setImageViewResource(R.id.right_image3, mTime[last / 10]);
            views.setImageViewResource(R.id.right_image4, mTime[last % 10]);
            views.setImageViewResource(R.id.right_image5, mTime[last / 10]);
            views.setImageViewResource(R.id.right_image6, mTime[last % 10]);
            views.setImageViewResource(R.id.right_image7, mTime[minutes / 10]);
            views.setImageViewResource(R.id.right_image8, mTime[minutes % 10]);
        } else {
            int last = minutes - 1;
            if (minutes == 0) {
                last = 59;
            }
            if (last == 59) {
                views.setImageViewResource(R.id.right_image1, mTime[0]);
                views.setImageViewResource(R.id.right_image2, mTime[minutes]);
                views.setImageViewResource(R.id.right_image3, mTime[last / 10]);
                views.setImageViewResource(R.id.right_image4, mTime[last % 10]);
                views.setImageViewResource(R.id.right_image5, mTime[last / 10]);
                views.setImageViewResource(R.id.right_image6, mTime[last % 10]);
                views.setImageViewResource(R.id.right_image7, mTime[0]);
                views.setImageViewResource(R.id.right_image8, mTime[minutes]);
            } else {
                views.setImageViewResource(R.id.right_image1, mTime[0]);
                views.setImageViewResource(R.id.right_image2, mTime[minutes]);
                views.setImageViewResource(R.id.right_image3, mTime[0]);
                views.setImageViewResource(R.id.right_image4, mTime[last]);
                views.setImageViewResource(R.id.right_image5, mTime[0]);
                views.setImageViewResource(R.id.right_image6, mTime[last]);
                views.setImageViewResource(R.id.right_image7, mTime[0]);
                views.setImageViewResource(R.id.right_image8, mTime[minutes]);
            }
        }
        if (mFirstRun) {
            mFirstRun = false;
        }

        int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat dateFm = new SimpleDateFormat(mWidgetContext.getString(R.string.htc_date));
        String bottomDate = (dateFm.format(date) + "," + mWidgetContext
                .getString(mWeekDay[weekDay - 1]));

        views.setTextViewText(R.id.bottom_date, bottomDate);
        mLastMinutes = minutes;
        ComponentName componentName = new ComponentName(mWidgetContext,
                SphoneTimeAppWidgetProvider.class);
        mAppWidgetManager.updateAppWidget(componentName, views);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        if (sAppWidgetIds.length > 0) {
            Intent intent = new Intent(this, SphoneTimeService.class);
            this.startService(intent);
        } else {
            super.onDestroy();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFirstRun = true;
        UpdateTimeUI();
        flags =  START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

}
