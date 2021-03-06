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

import example.prada.lab.pradaoutlook.DataGenerator;
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
    }

    @After
    public void eraseStore() {
        mStore.removeAllRecords();
    }

    @Test
    public void testAddAndQueryEvent() {
        List<POEvent> events = DataGenerator.createEventList(1);
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
        List<POEvent> events = DataGenerator.createEventList(10);
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
        List<POEvent> events = DataGenerator.createEventList(NUM);
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
        List<POEvent> events = DataGenerator.createEventList(NUM);
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
        int NUM = 100;
        List<POEvent> events = DataGenerator.createEventList(NUM);
        Date t1 = events.get(0).getFrom();
        Date t2 = events.get(1).getFrom();
        Date tn = events.get(events.size() - 1).getFrom();
        mStore.addEvents(events);
        assertEquals(NUM, mStore.countEvents(t1.getTime(), tn.getTime()));
        assertEquals(2, mStore.countEvents(t1.getTime(), t2.getTime()));
    }

    @Test
    public void testHasEventsWithEmptyStore() {
        mStore.addEvents(new ArrayList<POEvent>());
        Date t1 = new Date();
        t1.setYear(1700);
        Date t2 = new Date();
        t2.setYear(3000);
        assertEquals(0, mStore.countEvents(t1.getTime(), t2.getTime()));
    }

    @Test
    public void testHasEventsWithWrongParameters() {
        try {
            mStore.countEvents(-1, -1);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            mStore.countEvents(-1, System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            mStore.countEvents(System.currentTimeMillis(), -1);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test()
    public void testListenerOperation() {
        // FIXME because the android testing framework has test shading by default, so the test case
        // with asynchronous operation will effect another testcase. so I just leave this test first.
        //
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
//        List<POEvent> events = DataGenerator.createEventList(NUM);
//        mStore.addEvents(events);
//        try {
//            lock.await();
//        } catch (InterruptedException e) {
//            fail(e.getMessage());
//        }
//        mStore.removeListener(listener);
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
