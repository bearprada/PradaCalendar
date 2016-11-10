package example.prada.lab.pradaoutlook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import java.util.Date;
import com.github.sundeepk.compactcalendarview.domain.Event;

import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.db.OutlookDbHelper;

/**
 * Created by prada on 10/27/16.
 *
 * this data object represent the event's detail
 */
public class POEvent {

    public static final String LABEL_OOO = "out-of-office";
    public static final String LABEL_OFFICE = "office";
    public static final String LABEL_BIRTHDAY = "birthday";

    private final String mTitle;
    private final String mLabel;
    private final Date mFrom;
    private final Date mTo;
    private Long mId;

    /**
     * create the event object
     *
     * @param title the event's title, it should not be null or empty
     * @param label the event's label, it present the event's category
     * @param from the event's start time, it should not be null, and it should be less then {@link POEvent#mTo}
     * @param to the event's end time, it should not be null, and it should large then {@link POEvent#mFrom}
     * @throws IllegalArgumentException
     */
    public POEvent(String title, String label, Date from, Date to) throws IllegalArgumentException {
        mTitle = title;
        mLabel = label;
        mFrom = from;
        mTo = to;
        mId = -1L;
        verify();
    }

    private POEvent(ContentValues values) throws IllegalArgumentException {
        mId = values.getAsLong(OutlookDbHelper.EVENT_ID);
        mTitle = values.getAsString(OutlookDbHelper.EVENT_TITLE);
        mLabel = values.getAsString(OutlookDbHelper.EVENT_LABEL);
        mFrom = new Date(values.getAsLong(OutlookDbHelper.EVENT_START_TIME));
        mTo = new Date(values.getAsLong(OutlookDbHelper.EVENT_END_TIME));
        verify();
    }

    private void verify() throws IllegalArgumentException {
        if (TextUtils.isEmpty(mTitle)) {
            throw new IllegalArgumentException("title should not be null or empty");
        }
        if (TextUtils.isEmpty(mLabel)) {
            throw new IllegalArgumentException("label should not be null or empty");
        }
        if (mFrom == null || mTo == null) {
            throw new IllegalArgumentException("the date range should not be null, but the from = " + mFrom + ", to = " + mTo);
        }
        if (mFrom.after(mTo)) {
            throw new IllegalArgumentException("the date range is wrong, but the from = " + mFrom + ", to = " + mTo);
        }
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

    public Event toEvent() {
        return new Event(getColor(), getFrom().getTime(), this);
    }

    public @ColorInt int getColor() {
        switch (mLabel == null ? "" : mLabel) {
            case LABEL_OOO:
                return Color.parseColor("#AED581");
            case LABEL_BIRTHDAY:
                return Color.parseColor("#F06292");
            case LABEL_OFFICE:
                return Color.parseColor("#7986CB");
            default:
                return Color.parseColor("#666666");
        }
    }

    public @DrawableRes int getLabelResourceId() {
        switch (mLabel == null ? "" : mLabel) {
            case LABEL_OOO:
                return R.drawable.label_ooo;
            case LABEL_BIRTHDAY:
                return R.drawable.label_birthday;
            case LABEL_OFFICE:
                return R.drawable.label_office;
            default:
                return R.drawable.label_others;
        }
    }

    /**
     * create the event object from the cursor
     * @param cursor the cursor object that point to the data we want to convert.
     * @return the data object with mapped data from the cursor
     */
    public static POEvent createFromCursor(Cursor cursor) {
        if (cursor == null) {
            throw new IllegalArgumentException("the cursor should not be null");
        }
        ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, values);
        return new POEvent(values);
    }

    /**
     * create the content value object for the content provider operation.
     * @return a content value object has the event's data
     */
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(OutlookDbHelper.EVENT_TITLE, mTitle);
        cv.put(OutlookDbHelper.EVENT_LABEL, mLabel);
        cv.put(OutlookDbHelper.EVENT_START_TIME, mFrom.getTime());
        cv.put(OutlookDbHelper.EVENT_END_TIME, mTo.getTime());
        return cv;
    }

    public Long getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        POEvent poEvent = (POEvent) o;

        if (mTitle != null ? !mTitle.equals(poEvent.mTitle) : poEvent.mTitle != null) return false;
        if (mLabel != null ? !mLabel.equals(poEvent.mLabel) : poEvent.mLabel != null) return false;
        if (mFrom != null ? !mFrom.equals(poEvent.mFrom) : poEvent.mFrom != null) return false;
        if (mTo != null ? !mTo.equals(poEvent.mTo) : poEvent.mTo != null) return false;
        return mId != null ? mId.equals(poEvent.mId) : poEvent.mId == null;

    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (mLabel != null ? mLabel.hashCode() : 0);
        result = 31 * result + (mFrom != null ? mFrom.hashCode() : 0);
        result = 31 * result + (mTo != null ? mTo.hashCode() : 0);
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        return result;
    }

    /**
     * updating the event id, the id should be the same as the id in the SQLite database
     *
     * @param id the row id that provided by SQLite database
     * @throws IllegalArgumentException when {@code id <= 0}
     */
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("the event id should be over 0");
        }
        mId = id;
    }
}
