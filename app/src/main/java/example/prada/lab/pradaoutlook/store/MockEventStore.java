package example.prada.lab.pradaoutlook.store;

import android.database.Cursor;
import android.database.MatrixCursor;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import example.prada.lab.pradaoutlook.db.OutlookDbHelper;
import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/29/16.
 */

public class MockEventStore extends BaseEventStore {
    private static MockEventStore sStore;
    private final TreeSet<POEvent> mTestData = new TreeSet<>(new Comparator<POEvent>() {
        @Override
        public int compare(POEvent o1, POEvent o2) {
            return (int) (o2.getFrom().getTime() - o1.getFrom().getTime()); // FIXME the rule is wrong
        }
    });

    static MockEventStore getInstance() {
        if (sStore == null) {
            sStore = new MockEventStore();
        }
        return sStore;
    }

    private MockEventStore() {
    }

    @Override
    public Cursor getEvents() {
        return createCursor();
    }

    private Cursor createCursor() {
        MatrixCursor cursor = new MatrixCursor(OutlookDbHelper.COLUMNS);
        for (POEvent e : mTestData) {
            cursor.newRow()
                  .add(e.getId())
                  .add(e.getFrom().getTime())
                  .add(e.getTo().getTime())
                  .add(e.getTitle())
                  .add(e.getLabel());
        }
        cursor.moveToFirst();
        return cursor;
    }

    @Override
    public int countEvents() {
        return mTestData.size();
    }

    @Override
    public boolean hasEvents(long t1, long t2) {
        int start = -1;
        int end = -1;
        int i = 0;
        for(POEvent e : mTestData) {
            if (e.getFrom().getTime() >= t1 && start == -1) {
                start = i;
            }
            if (e.getTo().getTime() >= t2 && end == -1) {
                end = i;
            }
            if (e.getFrom().getTime() > t2) {
                break;
            }
            i++;
        }
        return end > start && start != end && start != -1 && end != -1;
    }

    @Override
    public Calendar getFirstEventTime() {
        if (mTestData.isEmpty()) {
            throw new IllegalStateException("the store doesn't contain any events");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(mTestData.first().getFrom());
        return c;
    }

    @Override
    public Calendar getLatestEventTime() {
        if (mTestData.isEmpty()) {
            throw new IllegalStateException("the store doesn't contain any events");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(mTestData.last().getTo());
        return c;
    }

    @Override
    public void addEvents(Collection<POEvent> events) {
        mTestData.addAll(events);
        Cursor cursor = createCursor();
        for (IEventDataUpdatedListener listener : mListeners) {
            listener.onEventsInsert(cursor);
        }
    }

    @Override
    public void removeAllRecords() {
        mTestData.clear();
    }
}
