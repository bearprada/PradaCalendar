package example.prada.lab.pradaoutlook.store;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.getbase.android.db.provider.ProviderAction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
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

    // TODO add the content resolver listener

    @NonNull
    @Override
    public List<POEvent> queryEvents(long t1, long t2) {
        Cursor cursor = ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.ID,
                        OutlookDbHelper.EVENT_TITLE,
                        OutlookDbHelper.EVENT_LABEL,
                        OutlookDbHelper.EVENT_START_TIME,
                        OutlookDbHelper.EVENT_END_TIME)
            .orderBy(OutlookDbHelper.DEFAULT_ORDER_BY)
            .where(OutlookDbHelper.EVENT_START_TIME + " > " + t1 + " OR "+
                   OutlookDbHelper.EVENT_END_TIME + " < " + t2)
            .perform(mResolver);
        List<POEvent> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            list.add(POEvent.createFromCursor(cursor));
        }
        while (cursor.moveToNext()) {
            list.add(POEvent.createFromCursor(cursor));
        }
        cursor.close();
        return list;
    }

    @NonNull
    @Override
    public POEvent queryEvent(long t1, long t2, int index) {
        Cursor cursor = ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.ID,
                        OutlookDbHelper.EVENT_TITLE,
                        OutlookDbHelper.EVENT_LABEL,
                        OutlookDbHelper.EVENT_START_TIME,
                        OutlookDbHelper.EVENT_END_TIME)
            .orderBy(OutlookDbHelper.DEFAULT_ORDER_BY)
            .where(OutlookDbHelper.EVENT_START_TIME + " > " + t1 + " OR "+
                   OutlookDbHelper.EVENT_END_TIME + " < " + t2)
            .perform(mResolver);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            if (cursor.getCount() < index || !cursor.moveToPosition(index)) {
                return null;
            }
            return POEvent.createFromCursor(cursor);
        } finally {
            cursor.close();
        }
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
    public Calendar getFirstEventTime() throws NullPointerException {
        Cursor cursor = ProviderAction.query(EventContentProvider.EVENT_URI)
                                            .orderBy(OutlookDbHelper.EVENT_START_TIME + " DESC")
                                            .perform(mResolver);
        if (cursor.getCount() <= 0) {
            throw new NullPointerException("it can't find any record");
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
                                      .orderBy(OutlookDbHelper.EVENT_END_TIME + " ASC")
                                      .perform(mResolver);
        if (cursor.getCount() <= 0) {
            throw new NullPointerException("it can't find any record");
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
    public void addEvent(final POEvent event) {
        insertEvent(event);
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (IEventDataUpdatedListener l : mListeners) {
                    l.onEventInsert(event);
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void addEvents(final Collection<POEvent> events) {
        for (POEvent event : events) {
            Uri uri = insertEvent(event);
        }
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (IEventDataUpdatedListener l : mListeners) {
                    l.onEventsInsert(events);
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
