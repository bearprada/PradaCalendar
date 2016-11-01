package example.prada.lab.pradaoutlook.store;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/29/16.
 */

public class MockEventStore extends BaseEventStore {
    private static MockEventStore sStore;
    private final List<POEvent> mTestData = new ArrayList<>();

    static MockEventStore getInstance() {
        if (sStore == null) {
            sStore = new MockEventStore();
        }
        return sStore;
    }

    private MockEventStore() {
    }

    // TODO it's a pool implementation for testing data
    @Override
    @NonNull public List<POEvent> queryEvents(long timestamp1, long timestamp2) {
        int[] index = searchEvents(timestamp1, timestamp2);
        if (!internalHasEvents(index[0], index[1])) {
            return Collections.EMPTY_LIST;
        }
        return mTestData.subList(index[0], index[1]);
    }

    @Override
    public int countEvents(long t1, long t2) {
        return queryEvents(t1, t2).size();
    }

    private int[] searchEvents(long t1, long t2) {
        int[] index = new int[] { -1, -1 };
        for (int i = 0; i < mTestData.size(); i++) {
            POEvent e = mTestData.get(i);
            if (e.getFrom().getTime() >= t1 && index[0] == -1) {
                index[0] = i;
            }
            if (e.getTo().getTime() >= t2 && index[1] == -1) {
                index[1] = i;
            }
            if (e.getFrom().getTime() > t2) {
                break;
            }
        }
        return index;
    }

    @Override
    public boolean hasEvents(long timestamp1, long timestamp2) {
        int[] index = searchEvents(timestamp1, timestamp2);
        return internalHasEvents(index[0], index[1]);
    }

    @Override
    public Calendar getFirstEventTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(mTestData.get(0).getFrom());
        return c;
    }

    @Override
    public Calendar getLatestEventTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(mTestData.get(mTestData.size() - 1).getTo());
        return c;
    }

    @Override
    public void addEvent(POEvent event) {
        mTestData.add(event);
    }

    @Override
    public void addEvents(Collection<POEvent> events) {
        mTestData.addAll(events);
        for (IEventDataUpdatedListener listener : mListeners) {
            listener.onEventsInsert(events);
        }
    }

    private boolean internalHasEvents(int start, int end) {
        if (end <= start) {
            return false;
        }
        return start != end && start != -1 && end != -1;
    }
}
