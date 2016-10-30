package example.prada.lab.pradaoutlook.utils;

import java.util.Calendar;

/**
 * Created by prada on 10/29/16.
 */

public class Utility {

    public static int getDaysBetween(Calendar cal1, Calendar cal2) {
        if (cal1.after(cal2)) {
            return 0; // TODO throw exception
        }
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 == year2) {
            return cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
        }
        int days = (getDaysOfYear(year1) - cal1.get(Calendar.DAY_OF_YEAR));
        days += cal2.get(Calendar.DAY_OF_YEAR);
        for (int i = year1 + 1; i < year2; i++) {
            days += getDaysOfYear(i);
        }
        return days;
    }

    private static int getDaysOfYear(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    private static boolean isLeapYear(int year) {
        return year > 0 && (year % 4 == 0);
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
