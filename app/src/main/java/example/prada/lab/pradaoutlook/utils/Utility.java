package example.prada.lab.pradaoutlook.utils;

import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by prada on 10/29/16.
 */

public class Utility {

    private static final long MILL_SECONDS_A_DAY = 24 * 60 * 60 * 1000;

    public static long getDaysBetween(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return 0;
        }
        // normalize the days
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        long t1 = cal1.getTimeInMillis();
        long t2 = cal2.getTimeInMillis();
        if (t1 >= t2) {
            return 0;
        }
        return ((t2 - t1) / MILL_SECONDS_A_DAY);
    }

    public static @NonNull String getDurationString(long millSeconds) {
        long seconds = millSeconds / 1000;
        if (seconds < 60) { // it's less then 1 min.
            return "";
        }
        int day = (int) Math.floor(seconds / (3600 * 24));
        int hrs = (int) Math.floor(seconds / 3600) % 24;
        int min = (int) Math.floor(seconds % 3600) / 60;
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("d");
        }
        if (hrs > 0) {
            sb.append(hrs).append("h");
        }
        if (min > 0) {
            sb.append(min).append("m");
        }
        return sb.toString();
    }

    public static String convertDayStr(int day) {
        switch(day) {
            case Calendar.SUNDAY:
                return "SUNDAY";
            case Calendar.MONDAY:
                return "MONDAY";
            case Calendar.TUESDAY:
                return "TUESDAY";
            case Calendar.WEDNESDAY:
                return "WEDNESDAY";
            case Calendar.THURSDAY:
                return "THURSDAY";
            case Calendar.FRIDAY:
                return "FRIDAY";
            case Calendar.SATURDAY:
                return "SATURDAY";
            default:
                throw new IllegalArgumentException("the day should be between 1 - 7, but it's " + day);
        }
    }

    public static String convertMonthStr(int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "JANUARY";
            case Calendar.FEBRUARY:
                return "FEBRUARY";
            case Calendar.MARCH:
                return "MARCH";
            case Calendar.APRIL:
                return "APRIL";
            case Calendar.MAY:
                return "MAY";
            case Calendar.JUNE:
                return "JUNE";
            case Calendar.JULY:
                return "JULY";
            case Calendar.AUGUST:
                return "AUGUST";
            case Calendar.SEPTEMBER:
                return "SEPTEMBER";
            case Calendar.OCTOBER:
                return "OCTOBER";
            case Calendar.NOVEMBER:
                return "NOVEMBER";
            case Calendar.DECEMBER:
                return "DECEMBER";
            default:
                throw new IllegalArgumentException("the month should be between 0 - 11, but it's " + month);
        }
    }
}
