/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mephone.hellohwlockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

public class DateView extends TextView {
    private static final String TAG = "DateView";

    private boolean mAttachedToWindow;
    private boolean mWindowVisible;
    private boolean mUpdating;
    private Context mContext;

    private HashMap<String, Integer> mAllHoliday = null;
    
    private final static String[] solarTerm = new String[] {
            "小寒", "大寒", "立春",
            "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    };
    private void setAllHoliday() {
        if (mAllHoliday == null) {
            mAllHoliday = new HashMap<String, Integer>();
        } else {
            mAllHoliday.clear();
        }
        mAllHoliday.put("1|1", R.string.solar_calendar_jan_01);
        mAllHoliday.put("2|14", R.string.solar_calendar_feb_14);
        mAllHoliday.put("3|8", R.string.solar_calendar_mar_8);
        mAllHoliday.put("3|12", R.string.solar_calendar_mar_12);
        mAllHoliday.put("4|1", R.string.solar_calendar_apr_01);
        mAllHoliday.put("4|4", R.string.solar_calendar_apr_04);
        mAllHoliday.put("5|1", R.string.solar_calendar_may_01);
        mAllHoliday.put("5|4", R.string.solar_calendar_may_04);
        mAllHoliday.put("6|1", R.string.solar_calendar_jun_01);
        mAllHoliday.put("7|1", R.string.solar_calendar_jul_01);
        mAllHoliday.put("8|1", R.string.solar_calendar_aug_01);
        mAllHoliday.put("9|0", R.string.solar_calendar_sep_10);
        mAllHoliday.put("10|1", R.string.solar_calendar_oct_01);
        mAllHoliday.put("10|31", R.string.solar_calendar_oct_31);
        mAllHoliday.put("12|24", R.string.solar_calendar_dec_24);
        mAllHoliday.put("12|25", R.string.solar_calendar_dec_25);
        mAllHoliday.put("正月初一", R.string.spring_day);
        mAllHoliday.put("正月十五", R.string.lanterns);
        mAllHoliday.put("五月初五", R.string.dragon_boat);
        mAllHoliday.put("七月初七", R.string.qixi);
        mAllHoliday.put("八月十五", R.string.mid_autumn);
        mAllHoliday.put("九月初九", R.string.chongyang);
        mAllHoliday.put("腊月月初八", R.string.laba);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateClock();
        }
    };

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)
                    || Intent.ACTION_TIME_CHANGED.equals(action)
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                mHandler.sendEmptyMessage(1);
            }
        }
    };

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setAllHoliday();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        setUpdates();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        setUpdates();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mWindowVisible = visibility == VISIBLE;
        setUpdates();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        setUpdates();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    public void updateClock() {
        final String dateFormat = getContext().getString(R.string.abbrev_wday_month_day);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String key = month + "|" + day;
        if (mAllHoliday == null) {
            setAllHoliday();
        }
        Integer holidayRes = mAllHoliday.get(key);
        String holiday = "";
        if(holidayRes != null) {
            holiday = getContext().getString(holidayRes);
        }
        String chinaToday = ChinaDate.oneDay(2014, 1, 31);
        holidayRes = mAllHoliday.get(chinaToday);
        if(holidayRes != null) {
            holiday += getContext().getString(holidayRes);
        }
        Log.i("huanghua", "chinaToday:" + chinaToday);
        setText(DateFormat.format(dateFormat, new Date()) + "  " + holiday);
    }

    private boolean isVisible() {
        View v = this;
        while (true) {
            if (v.getVisibility() != VISIBLE) {
                return false;
            }
            final ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View) parent;
            } else {
                return true;
            }
        }
    }

    private void setUpdates() {
        boolean update = mAttachedToWindow && mWindowVisible && isVisible();
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                // Register for Intent broadcasts for the clock and battery
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_TIME_TICK);
                filter.addAction(Intent.ACTION_TIME_CHANGED);
                filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
                mContext.registerReceiver(mIntentReceiver, filter, null, null);
                mHandler.sendEmptyMessage(1);
            } else {
                mContext.unregisterReceiver(mIntentReceiver);
            }
        }
    }
}
