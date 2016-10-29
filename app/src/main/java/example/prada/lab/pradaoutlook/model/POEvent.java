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

    public long getDuration() {
        return mTo.getTime() - mFrom.getTime();
    }
}
