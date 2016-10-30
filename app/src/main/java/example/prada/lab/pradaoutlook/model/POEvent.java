package example.prada.lab.pradaoutlook.model;

import java.util.Date;

/**
 * Created by prada on 10/27/16.
 */
public class POEvent {
    private final String mTitle;
    private final String mLabel;
    private final Date mFrom;
    private final Date mTo;

    public POEvent(String title, String label, Date from, Date to) {
        mTitle = title;
        mLabel = label;
        mFrom = from;
        mTo = to;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLabel() {
        return mLabel;
    }

    public Date getFrom() {
        return mFrom;
    }

    public Date getTo() {
        return mTo;
    }

    // TODO Test caes, expected format : 2h30m
    public String getDurationString() {
        long seconds = (mTo.getTime() - mFrom.getTime()) / 1000;
        if (seconds < 60) { // it's less then 1 min.
            return null;
        }
        int hour = (int) (seconds / 3600);
        int min = (int) ((seconds % 3600) / 60);
        if (hour <= 0 && min > 0) {
            return min + "m";
        }
        if (min <= 0 && hour > 0) {
            return hour + "h";
        }
        return hour + "h" + min + "m";
    }
}
