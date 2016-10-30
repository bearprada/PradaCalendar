package example.prada.lab.pradaoutlook.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by prada on 10/29/16.
 */

public class MockEventStore implements IEventStore {
    private static MockEventStore sStore;
    private final List<POEvent> mTestData = new ArrayList<>();

    public static MockEventStore getInstance(Context ctx) {
        if (sStore == null) {
            sStore = new MockEventStore(ctx);
        }
        return sStore;
    }

    private MockEventStore(Context ctx) {
        java.util.Random random = new java.util.Random();
        // generate the 100 events between last year to now
        final int NUM_EVENTS = 100;
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.MONTH, c1.get(Calendar.MONTH) - 1);
        Calendar c2 = Calendar.getInstance();
        long t1 = c1.getTimeInMillis();
        long t2 = c2.getTimeInMillis();
        long gap = (t2 - t1) / NUM_EVENTS;
        for (int i = 0; i < NUM_EVENTS ; i++) {
            long eventT1 = t1 + (i * gap);
            // long eventT2 = eventT1 + random.nextInt(1000);
            long eventT2 = eventT1 + 3600 * 1000; // add 1 hr
            mTestData.add(new POEvent("Event-" + random.nextInt(10000), "TODO", new Date(eventT1), new Date(eventT2)));
        }
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
            if (e.getFrom().getTime() >= t2 && index[1] == -1) {
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

    private boolean internalHasEvents(int start, int end) {
        if (end <= start) {
            return false;
        }
        return start != end && start != -1 && end != -1;
    }
}
