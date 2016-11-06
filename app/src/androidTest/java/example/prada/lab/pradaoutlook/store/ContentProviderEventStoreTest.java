package example.prada.lab.pradaoutlook.store;

import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 11/5/16.
 */

@RunWith(AndroidJUnit4.class)
public class ContentProviderEventStoreTest {

    private IEventStore mStore;

    @Before
    public void createStore() {
        mStore = EventStoreFactory.getInstance(InstrumentationRegistry.getTargetContext());
        mStore.removeAllRecords();
        // TODO add the data
    }

    @After
    public void eraseStore() {
        mStore.removeAllRecords();
    }

    @Test
    public void testAddAndQueryEvent() {
        List<POEvent> events = createEventList(1);
        mStore.addEvents(events);
        assertEquals(1, mStore.countEvents());
        Cursor cursor = mStore.getEvents();
        cursor.moveToFirst();
        POEvent event = POEvent.createFromCursor(cursor);
        assertNotNull(event);
        assertEquals(events.get(0), event);
    }

    @Test
    public void testAddAndQuery10Event() {
        List<POEvent> events = createEventList(10);
        mStore.addEvents(events);
        assertEquals(10, mStore.countEvents());
        Cursor cursor = mStore.getEvents();
        cursor.moveToFirst();
        int idx = 1;
        Date prevDate = null;
        do {
            POEvent event = POEvent.createFromCursor(cursor);
            // make sure the time sequence is correct
            assertNotNull(event);
            assertEquals("Event" + idx, event.getTitle());
            assertEquals("Label" + idx, event.getLabel());

            if (prevDate != null) {
                assertTrue(prevDate.getTime() <= event.getFrom().getTime());
            }
            prevDate = event.getFrom();
            idx++;
        } while (cursor.moveToNext());
    }

    @Test
    public void testAddEventsWithWrongParameters() {
        mStore.addEvents(new ArrayList<POEvent>());
        assertEquals(0, mStore.countEvents());

        try {
            mStore.addEvents(null);
        } catch (NullPointerException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void testInsertEventsInRandomOrder() {
        int NUM = 200;
        List<POEvent> events = createEventList(NUM);
        Collections.shuffle(events);
        mStore.addEvents(events);
        assertEquals(NUM, mStore.countEvents());
        Cursor cursor = mStore.getEvents();
        assertEquals(NUM, cursor.getCount());
        verifyData(cursor);
    }

    @Test
    public void testGetFirstAndLatestEventTime() {
        int NUM = 100;
        List<POEvent> events = createEventList(NUM);
        Date firstTime = events.get(0).getFrom();
        Date lastTime = events.get(events.size() - 1).getTo();
        mStore.addEvents(events);
        Calendar first = mStore.getFirstEventTime();
        Calendar last = mStore.getLatestEventTime();
        assertNotNull(first);
        assertNotNull(last);
        assertTrue(first.getTimeInMillis() <= last.getTimeInMillis());
        assertEquals(firstTime.getTime(), first.getTimeInMillis());
        assertEquals(lastTime.getTime(), last.getTimeInMillis());
    }

    @Test
    public void testGetFirstAndLatestEventTimeWithEmptyStore() {
        mStore.addEvents(new ArrayList<POEvent>());
        try {
            mStore.getFirstEventTime();
        } catch (IllegalStateException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
        try {
            mStore.getLatestEventTime();
        } catch (IllegalStateException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void testHasEvents() {
        int NUM = 66;
        List<POEvent> events = createEventList(NUM);
        Date t1 = events.get(0).getFrom();
        Date t2 = events.get(1).getFrom();
        Date tn = events.get(events.size() - 1).getFrom();
        mStore.addEvents(events);
        assertTrue(mStore.hasEvents(t1.getTime(), t2.getTime()));
        assertTrue(mStore.hasEvents(t2.getTime(), tn.getTime()));
    }

    @Test
    public void testHasEventsWithEmptyStore() {
        mStore.addEvents(new ArrayList<POEvent>());
        Date t1 = new Date();
        t1.setYear(1700);
        Date t2 = new Date();
        t2.setYear(3000);
        assertFalse(mStore.hasEvents(t1.getTime(), t2.getTime()));
    }

    @Test
    public void testHasEventsWithWrongParameters() {
        try {
            mStore.hasEvents(-1, -1);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            mStore.hasEvents(-1, System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            mStore.hasEvents(System.currentTimeMillis(), -1);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void testListenerWithEmptyList() {
        // TODO
    }
    @Test
    public void testListenerOperation() {
//        final int NUM = 10;
//        final CountDownLatch lock = new CountDownLatch(1);
//        IEventDataUpdatedListener listener = new IEventDataUpdatedListener() {
//            @Override
//            public void onEventsInsert(Cursor cursor) {
//                assertEquals(NUM, cursor.getCount());
//                verifyData(cursor);
//                lock.countDown();
//            }
//        };
//        mStore.addListener(listener);
//        List<POEvent> events = createEventList(NUM);
//        mStore.addEvents(events);
//        synchronized (lock) {
//            try {
//                lock.await();
//            } catch (InterruptedException e) {
//                fail(e.getMessage());
//            }
//        }
//        mStore.removeListener(listener);
    }

    private List<POEvent> createEventList(int num) {
        List<POEvent> events = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            Date d1 = new Date();
            d1.setYear(2000);
            d1.setMonth(i);
            Date d2 = new Date();
            d1.setYear(2000);
            d2.setMonth(i + 1);
            POEvent event = new POEvent("Event" + i, "Label" + i, d1, d2);
            events.add(event);
        }
        return events;
    }

    private void verifyData(Cursor cursor) {
        cursor.moveToFirst();
        int idx = 1;
        Date prevDate = null;
        do {
            POEvent event = POEvent.createFromCursor(cursor);
            // make sure the time sequence is correct
            assertNotNull(event);
            assertEquals("Event" + idx, event.getTitle());
            assertEquals("Label" + idx, event.getLabel());

            if (prevDate != null) {
                assertTrue(prevDate.getTime() <= event.getFrom().getTime());
            }
            prevDate = event.getFrom();
            idx++;
        } while (cursor.moveToNext());

    }
}
