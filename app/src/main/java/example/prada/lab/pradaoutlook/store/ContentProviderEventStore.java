package example.prada.lab.pradaoutlook.store;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.getbase.android.db.provider.ProviderAction;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import example.prada.lab.pradaoutlook.EventContentProvider;
import example.prada.lab.pradaoutlook.db.OutlookDbHelper;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/31/16.
 */

public class ContentProviderEventStore extends BaseEventStore {

    private final ContentResolver mResolver;

    public ContentProviderEventStore(Context ctx) {
        mResolver = ctx.getContentResolver();
    }

    // TODO add the content resolver listener

    @NonNull
    @Override
    public List<POEvent> queryEvents(long t1, long t2) {
        Cursor c = ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.ID,
                        OutlookDbHelper.EVENT_TITLE,
                        OutlookDbHelper.EVENT_LABEL,
                        OutlookDbHelper.EVENT_START_TIME,
                        OutlookDbHelper.EVENT_END_TIME)
            .where(OutlookDbHelper.EVENT_START_TIME + " > " + t1 + " OR "+
                   OutlookDbHelper.EVENT_END_TIME + " < " + t2)
            .perform(mResolver);
        // TODO cursor -> event objects
        return null;
    }

    @Override
    public int countEvents(long t1, long t2) {
        return ProviderAction.query(EventContentProvider.EVENT_URI)
                             .projection(OutlookDbHelper.ID)
                             .where(OutlookDbHelper.EVENT_START_TIME + " > " + t1 + " OR "+
                                    OutlookDbHelper.EVENT_END_TIME + " < " + t2)
                             .perform(mResolver)
                             .getCount();
    }

    @Override
    public boolean hasEvents(long t1, long t2) {
        return countEvents(t1, t2) > 0;
    }

    @Override
    public Calendar getFirstEventTime() {
        return null;
    }

    @Override
    public Calendar getLatestEventTime() {
        return null;
    }

    @Override
    public void addEvent(POEvent event) {

    }

    @Override
    public void addEvents(Collection<POEvent> events) {

    }
}
