package com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers;

import android.text.format.Time;

public class WatchCurrentTime {

    public WatchCurrentTime() {
        mTime = new Time();
    }

    private static int get12HourFrom24Hour(int i) {
        int j = i % 12;
        if (j == 0)
            j = 12;
        return j;
    }

    public static WatchCurrentTime getCurrent() {
        WatchCurrentTime watchcurrenttime = new WatchCurrentTime();
        getCurrent(watchcurrenttime);
        return watchcurrenttime;
    }

    public static void getCurrent(WatchCurrentTime watchcurrenttime) {
        watchcurrenttime.getTime().setToNow();
        watchcurrenttime.setDayOfMonth(watchcurrenttime.getTime().monthDay);
        int i = watchcurrenttime.getTime().hour;
        watchcurrenttime.set24Hour(i);
        watchcurrenttime.set12Hour(get12HourFrom24Hour(i));
        watchcurrenttime.setMinute(watchcurrenttime.getTime().minute);
        watchcurrenttime.setSecond(watchcurrenttime.getTime().second);
        watchcurrenttime.setMillisecond(System.currentTimeMillis() % 1000L);
    }

    private void set12Hour(float f) {
        m12Hour = f;
    }

    private void set24Hour(float f) {
        m24Hour = f;
    }

    private void setDayOfMonth(float f) {
        mDayOfMonth = f;
    }

    private void setMillisecond(float f) {
        mMillisecond = f;
    }

    private void setMinute(float f) {
        mMinute = f;
    }

    private void setSecond(float f) {
        mSecond = f;
    }

    public float get12Hour() {
        return m12Hour;
    }

    public float get24Hour() {
        return m24Hour;
    }

    public float getDayOfMonth() {
        return mDayOfMonth;
    }

    public float getHourDegreesContinuous() {
        return TimeHelper.getDegreesFromHour(get12Hour() + getMinute() / 60F);
    }

    public float getMillisecond() {
        return mMillisecond;
    }

    public float getMinute() {
        return mMinute;
    }

    public float getMinuteDegrees() {
        return TimeHelper.getDegreesFromMinute(getMinute());
    }

    public float getMinuteDegreesContinuous() {
        return TimeHelper.getDegreesFromMinute(getMinute() + getSecond() / 60F);
    }

    public float getSecond() {
        return mSecond;
    }

    public float getSecondDegrees() {
        return TimeHelper.getDegreesFromSecond(getSecond());
    }

    public float getSecondDegreesContinuous() {
        return TimeHelper.getDegreesFromMillisecond(1000F * getSecond()
                + getMillisecond());
    }

    public Time getTime() {
        return mTime;
    }

    private float m12Hour;
    private float m24Hour;
    private float mDayOfMonth;
    private float mMillisecond;
    private float mMinute;
    private float mSecond;
    private Time mTime;
}
