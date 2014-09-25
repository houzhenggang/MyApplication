package com.androidwear.home.watchfaces.ustwowatchfaces2.customstyle.util;

public final class CustomWatchFaceConstants {
    public static final CharSequence AM_PM_FORMAT;
    public static final CharSequence DAY_OF_WEEK_AND_DATE_FORMAT;
    public static final CharSequence DAY_OF_WEEK_AND_DATE_SHORT_FORMAT;
    public static final CharSequence DAY_OF_WEEK_AND_DATE_SHORT_FORMAT_FOR_KOREA;
    public static final CharSequence DAY_OF_WEEK_AND_MONTH_DATE_FORMAT;
    public static final CharSequence DAY_OF_WEEK_AND_MONTH_DATE_FORMAT_FOR_KOREA;
    public static final CharSequence DAY_OF_WEEK_AND_MONTH_DATE_SHORT_FORMAT;
    public static final CharSequence DAY_OF_WEEK_ONLY_FORMAT;
    public static final CharSequence DAY_OF_WEEK_ONLY_SHORT_FORMAT;
    public static final CharSequence DAY_OF_WEEK_ONLY_SHORT_FORMAT_FOR_KOREA;
    public static final CharSequence MONTH_AND_DAY_FORMAT;
    public static final CharSequence MONTH_AND_DAY_FORMAT_FOR_KOREA;
    public static final CharSequence MONTH_AND_DAY_SHORT_FORMAT;
    public static final CharSequence MONTH_AND_DAY_SHORT_FORMAT_FOR_KOREA;
    public static final CharSequence TIME_12_HOUR_FORMAT = "hh:mm";
    public static final CharSequence TIME_12_HOUR_ONLY_FORMAT;
    public static final CharSequence TIME_24_HOUR_FORMAT;
    public static final CharSequence TIME_24_HOUR_ONLY_FORMAT;
    public static final CharSequence TIME_MINUTE_ONLY_FORMAT;
    public static final CharSequence TIME_ZERO_REMOVED_12_HOUR_FORMAT = "h:mm";
    public static final CharSequence TIME_ZERO_REMOVED_12_HOUR_ONLY_FORMAT = "h";

    static {
        TIME_24_HOUR_FORMAT = "HH:mm";
        TIME_12_HOUR_ONLY_FORMAT = "hh";
        TIME_24_HOUR_ONLY_FORMAT = "HH";
        TIME_MINUTE_ONLY_FORMAT = "mm";
        AM_PM_FORMAT = "a";
        DAY_OF_WEEK_ONLY_FORMAT = "EEEE";
        DAY_OF_WEEK_ONLY_SHORT_FORMAT = "EEE";
        DAY_OF_WEEK_ONLY_SHORT_FORMAT_FOR_KOREA = "EEEE";
        DAY_OF_WEEK_AND_DATE_FORMAT = "EEEE, d";
        DAY_OF_WEEK_AND_DATE_SHORT_FORMAT = "EEE d";
        DAY_OF_WEEK_AND_DATE_SHORT_FORMAT_FOR_KOREA = "d EEEE";
        DAY_OF_WEEK_AND_MONTH_DATE_FORMAT = "EEEE, MMMM d";
        DAY_OF_WEEK_AND_MONTH_DATE_FORMAT_FOR_KOREA = "MMMM d, EEEE";
        DAY_OF_WEEK_AND_MONTH_DATE_SHORT_FORMAT = "EEE, MMM d";
        MONTH_AND_DAY_FORMAT = "MMMM d";
        MONTH_AND_DAY_FORMAT_FOR_KOREA = "MMMM d";
        MONTH_AND_DAY_SHORT_FORMAT = "MMM d";
        MONTH_AND_DAY_SHORT_FORMAT_FOR_KOREA = "MMM d";
    }
}
