package example.prada.lab.pradaoutlook.store;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.getbase.android.db.provider.ProviderAction;

import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.Callable;

import bolts.Task;
import example.prada.lab.pradaoutlook.EventContentProvider;
import example.prada.lab.pradaoutlook.db.OutlookDbHelper;
import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/31/16.
 */

public class ContentProviderEventStore extends BaseEventStore {

    private final ContentResolver mResolver;

    private static ContentProviderEventStore sStore;

    static ContentProviderEventStore getInstance(Context ctx) {
        if (sStore == null) {
            sStore = new ContentProviderEventStore(ctx);
        }
        return sStore;
    }

    private ContentProviderEventStore(Context ctx) {
        mResolver = ctx.getContentResolver();
    }

    @Override
    public Cursor getEvents() {
        return ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.EVENT_ID,
                        OutlookDbHelper.EVENT_TITLE,
                        OutlookDbHelper.EVENT_LABEL,
                        OutlookDbHelper.EVENT_START_TIME,
                        OutlookDbHelper.EVENT_END_TIME)
            .orderBy(OutlookDbHelper.EVENT_START_TIME + " ASC")
            .perform(mResolver);
    }

    @Override
    public int countEvents() {
        return ProviderAction.query(EventContentProvider.EVENT_URI)
                             .projection(OutlookDbHelper.EVENT_ID)
                             .perform(mResolver)
                             .getCount();
    }

    @Override
    public boolean hasEvents(long t1, long t2) {
        if (t1 < 0 || t2 < 0) {
            throw new IllegalArgumentException("the timestamp should be the positive value, but t1 = " + t1 + ", t2 = " + t2);
        }
        return ProviderAction.query(EventContentProvider.EVENT_URI)
                             .projection(OutlookDbHelper.EVENT_ID)
                             .where(OutlookDbHelper.EVENT_START_TIME + " > " + t1 + " OR "+
                                    OutlookDbHelper.EVENT_END_TIME + " < " + t2)
                             .perform(mResolver)
                             .getCount() > 0;
    }

    @Override
    public Calendar getFirstEventTime() throws NullPointerException {
        Cursor cursor = ProviderAction.query(EventContentProvider.EVENT_URI)
                                            .orderBy(OutlookDbHelper.EVENT_START_TIME + " ASC")
                                            .perform(mResolver);
        if (cursor.getCount() <= 0) {
            throw new IllegalStateException("it can't find any record");
        }
        cursor.moveToFirst();
        POEvent e = POEvent.createFromCursor(cursor);
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getFrom());
        return cal;
    }

    @Override
    public Calendar getLatestEventTime() {
        Cursor cursor = ProviderAction.query(EventContentProvider.EVENT_URI)
                                      .orderBy(OutlookDbHelper.EVENT_END_TIME + " DESC")
                                      .perform(mResolver);
        if (cursor.getCount() <= 0) {
            throw new IllegalStateException("it can't find any record");
        }
        cursor.moveToFirst();
        POEvent e = POEvent.createFromCursor(cursor);
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getTo());
        return cal;
    }

    private Uri insertEvent(POEvent event) {
        return ProviderAction.insert(EventContentProvider.EVENT_URI)
                      .values(event.getContentValues())
                      .perform(mResolver);
    }

    @Override
    public void addEvents(final Collection<POEvent> events) {
        if (events == null) {
            throw new NullPointerException("the events list should be null");
        }
        for (POEvent event : events) {
            Uri uri = insertEvent(event);
            long id = ContentUris.parseId(uri);
            event.setId(id);
        }
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (IEventDataUpdatedListener l : mListeners) {
                    l.onEventsInsert(getEvents());
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void removeAllRecords() {
        int count = ProviderAction.delete(EventContentProvider.EVENT_URI)
                                      .where("1")
                                      .perform(mResolver);
        System.out.println("it has deleted " + count + " rows");
    }
}
