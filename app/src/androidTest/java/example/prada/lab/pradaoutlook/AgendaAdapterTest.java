package example.prada.lab.pradaoutlook;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.store.IEventStore;
import example.prada.lab.pradaoutlook.utils.Utility;

/**
 * Created by prada on 11/6/16.
 */

@RunWith(AndroidJUnit4.class)
public class AgendaAdapterTest {

    private IEventStore mStore;
    private AgendaAdapter mAdapter;

    @Before
    public void createStore() {
        mStore = EventStoreFactory.getInstance(InstrumentationRegistry.getTargetContext());
        mStore.removeAllRecords();

        mAdapter = new AgendaAdapter(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void eraseStore() {
        mStore.removeAllRecords();
    }

    @Test
    public void testGetNumOfSectionsAndItems() {
        assertEquals(0, mAdapter.getNumberOfSections());
        assertEquals(0, mAdapter.getNumberOfItemsInSection(0));

        int NUM_OF_DAYS = 20;
        mStore.addEvents(DataGenerator.createEventList(1, NUM_OF_DAYS));
        mAdapter.updateEvents();
        assertEquals(NUM_OF_DAYS, mAdapter.getNumberOfSections());
        for (int i = 0; i < NUM_OF_DAYS ; i++) {
            assertEquals("i = " + i, 1, mAdapter.getNumberOfItemsInSection(i));
        }
    }

    /**
     * because we has empty section for showing the "No Event" message. so the empty section should
     * still return 1 for this case
     */
    @Test
    public void testGetNumOfSectionsAndItemsWithEmptySection() {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2020);
        c1.set(Calendar.DAY_OF_YEAR, 1);

        POEvent e1 = new POEvent("TestCase1Title", "TestCase1Label", c1.getTime(), c1.getTime());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c1.getTime());
        c2.set(Calendar.DAY_OF_MONTH, 30);
        POEvent e2 = new POEvent("TestCase1Title", "TestCase1Label", c2.getTime(), c2.getTime());
        List<POEvent> events = new ArrayList<>();
        events.add(e1);
        events.add(e2);
        mStore.addEvents(events);
        mAdapter.updateEvents();
        assertEquals(30, mAdapter.getNumberOfSections());
        assertEquals(2, mStore.countEvents());
        assertEquals(1, mAdapter.getNumberOfItemsInSection(0));
        assertEquals(1, mAdapter.getNumberOfItemsInSection(29));
        assertEquals(AgendaAdapter.ITEM_TYPE_EVENT, mAdapter.getSectionItemUserType(0, 0));
        assertEquals(AgendaAdapter.ITEM_TYPE_EVENT, mAdapter.getSectionItemUserType(29, 0));

        for (int i = 1; i < 29 ; i++) {
            assertEquals("i = " + i, 1, mAdapter.getNumberOfItemsInSection(i));
            assertEquals("i = " + i, AgendaAdapter.ITEM_TYPE_NO_EVENT, mAdapter.getSectionItemUserType(i, 0));
        }
    }

    @Test
    public void testLargeTimeRange() {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 1700);
        c1.set(Calendar.DAY_OF_YEAR, 1);

        List<POEvent> events = new ArrayList<>();
        events.add(new POEvent("TestCase1Title", "TestCase1Label", c1.getTime(), c1.getTime()));
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 5000);
        c2.set(Calendar.DAY_OF_YEAR, 1);
        events.add(new POEvent("TestCase1Title", "TestCase1Label", c2.getTime(), c2.getTime()));
        mStore.addEvents(events);
        mAdapter.updateEvents();
        assertEquals(Utility.getDaysBetween(c1, c2) + 1, mAdapter.getNumberOfSections());
    }

    @Test
    public void testFindSectionIndex() {
        int NUM_OF_DAYS = 30;
        mStore.addEvents(DataGenerator.createEventList(1, NUM_OF_DAYS));
        mAdapter.updateEvents();
        assertEquals(NUM_OF_DAYS, mAdapter.getNumberOfSections());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        for (int i = 1; i <= NUM_OF_DAYS ; i++) {
            cal.set(Calendar.DAY_OF_YEAR, i);
            assertEquals(i - 1, mAdapter.getSectionIndex(cal.getTime()));
        }
    }

    @Test
    public void testQueryEvents() {
        assertNull(mAdapter.queryEvent(0, 0));
        assertNull(mAdapter.queryEvent(Integer.MAX_VALUE, Integer.MAX_VALUE));

        int NUM_OF_DAYS = 10;
        mStore.addEvents(DataGenerator.createEventList(1, NUM_OF_DAYS));
        mAdapter.updateEvents();
        assertEquals(NUM_OF_DAYS, mAdapter.getNumberOfSections());
        for (int i = 0; i < NUM_OF_DAYS ; i++) {
            POEvent e1 = mAdapter.queryEvent(i, 0);
            POEvent e2 = mAdapter.queryEvent(i, Integer.MAX_VALUE);
            assertNotNull(e1);
            assertNotNull(e2);
            assertEquals(e1, e2);
        }

        assertNull(mAdapter.queryEvent(NUM_OF_DAYS, 0));
        assertNull(mAdapter.queryEvent(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

}
