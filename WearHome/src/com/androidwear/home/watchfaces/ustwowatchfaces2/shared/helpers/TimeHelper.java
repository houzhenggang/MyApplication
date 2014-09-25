package com.androidwear.home.watchfaces.ustwowatchfaces2.shared.helpers;

public class TimeHelper {

    public static float getDegreesFromHour(float f) {
        return 360F * (f / 12F) - 90F;
    }

    public static float getDegreesFromMillisecond(float f) {
        return 360F * (f / 60000F) - 90F;
    }

    public static float getDegreesFromMinute(float f) {
        return 360F * (f / 60F) - 90F;
    }

    public static float getDegreesFromSecond(float f) {
        return 360F * (f / 60F) - 90F;
    }

    public static String getTwoDigitNumber(int i) {
        Object aobj[] = new Object[1];
        aobj[0] = Integer.valueOf(i);
        return String.format("%02d", aobj);
    }
}
