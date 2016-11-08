package example.prada.lab.pradaoutlook.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;

import com.github.sundeepk.compactcalendarview.domain.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.Date;

import example.prada.lab.pradaoutlook.db.OutlookDbHelper;

/**
 * Created by prada on 11/6/16.
 */

@RunWith(AndroidJUnit4.class)
public class POEventTest {
    @Test
    public void testConstructor() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        POEvent e = new POEvent("event1", "label1", d1, d2);
        verifyEvent(e, "event1", "label1", Long.valueOf(-1), d1, d2);
    }

    @Test
    public void testConstructorWithWrongParam() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        String title = "testcaes0event";
        String label = "testcaes0label";

        createIllegalEvent(null, label, d1, d2);
        createIllegalEvent(title, null, d1, d2);
        createIllegalEvent(title, label, null, d2);
        createIllegalEvent(title, label, d1, null);
        createIllegalEvent(title, label, d2, d1);
        createIllegalEvent(null, null, null, null);
    }

    @Test
    public void testSetId() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        POEvent event = new POEvent("Testcase2Event", "Testcase2Label", d1, d2);
        event.setId(Long.MAX_VALUE);
        verifyEvent(event, "Testcase2Event", "Testcase2Label", Long.MAX_VALUE ,d1, d2);

        setWrongEventId(event, 0);
        setWrongEventId(event, -1L);
        setWrongEventId(event, -1 * Long.MIN_VALUE);
    }

    @Test
    public void testToEvent() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        POEvent event = new POEvent("Testcase3Event", "Testcase3Label", d1, d2);

        Event calEvent = event.toEvent();
        assertNotNull(calEvent);
        assertEquals(event, calEvent.getData());
        assertEquals(event.getFrom().getTime(), calEvent.getTimeInMillis());

        assertTrue(Color.alpha(calEvent.getColor()) >= 0);
        assertTrue(Color.red(calEvent.getColor()) >= 0);
        assertTrue(Color.blue(calEvent.getColor()) >= 0);
        assertTrue(Color.green(calEvent.getColor()) >= 0);
    }

    @Test
    public void testCreateFromCursor() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        String title = "Testcase5Event";
        String label = "Testcase5Label";
        long id = 100;
        POEvent e2 = POEvent.createFromCursor(createCursor(id, title, label, d1, d2));
        verifyEvent(e2, title, label, 100L, d1, d2);
    }

    @Test
    public void testCreateFromWrongCursor() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        String title = "Testcase5Event";
        String label = "Testcase5Label";
        long id = 100L;

        createEventWithWrongCursor(null);
        createEventWithWrongCursor(createCursor(-1, title, label, d1, d2));
        createEventWithWrongCursor(createCursor(id, "", label, d1, d2));
        createEventWithWrongCursor(createCursor(id, title, "", d1, d2));
        createEventWithWrongCursor(createCursor(id, title, label, null, d2));
        createEventWithWrongCursor(createCursor(id, title, label, d1, null));
        createEventWithWrongCursor(createCursor(id, title, label, d2, d1));
        createEventWithWrongCursor(createCursor(id, null, null, null, null));
    }

    @Test
    public void testEqualsImpl() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        String title = "Testcase4Event";
        String label = "Testcase4Label";
        POEvent e1 = new POEvent(title, label, d1, d2);
        POEvent e2 = new POEvent(title, label, d1, d2);
        assertEquals(e1, e2);

        POEvent e3 = new POEvent(title, label + "x", d1, d2);
        assertNotEquals(e1, e3);

        POEvent e4 = new POEvent(title + "x", label, d1, d2);
        assertNotEquals(e1, e4);

        Date d3 = new Date(d1.getTime());
        d3.setHours(d3.getHours() + 1);
        POEvent e5 = new POEvent(title, label, d3, d2);
        assertNotEquals(e1, e5);

        Date d4 = new Date(d2.getTime());
        d4.setHours(d4.getHours() + 1);
        POEvent e6 = new POEvent(title, label, d1, d4);
        assertNotEquals(e1, e6);
    }

    @Test
    public void testHashCodeImpl() {
        Date d1 = new Date();
        d1.setYear(2000);
        d1.setMonth(1);
        Date d2 = new Date();
        d2.setYear(2000);
        d2.setMonth(2);
        String title = "Testcase5Event";
        String label = "Testcase5Label";
        POEvent e1 = new POEvent(title, label, d1, d2);
        POEvent e2 = new POEvent(title, label, d1, d2);
        assertEquals(e1.hashCode(), e2.hashCode());

        POEvent e3 = new POEvent(title, label + "x", d1, d2);
        assertNotEquals(e1.hashCode(), e3.hashCode());

        POEvent e4 = new POEvent(title + "x", label, d1, d2);
        assertNotEquals(e1.hashCode(), e4.hashCode());

        Date d3 = new Date(d1.getTime());
        d3.setHours(d3.getHours() + 1);
        POEvent e5 = new POEvent(title, label, d3, d2);
        assertNotEquals(e1.hashCode(), e5.hashCode());

        Date d4 = new Date(d2.getTime());
        d4.setHours(d4.getHours() + 1);
        POEvent e6 = new POEvent(title, label, d1, d4);
        assertNotEquals(e1.hashCode(), e6.hashCode());
    }

    private void createIllegalEvent(String title, String label, Date d1, Date d2) {
        try {
            new POEvent(title, label, d1, d2);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    private void verifyEvent(POEvent e, String title, String label,
                             Long id, Date d1, Date d2) {
        assertNotNull(e);
        assertEquals(d1, e.getFrom());
        assertEquals(d2, e.getTo());
        assertEquals(title, e.getTitle());
        assertEquals(label, e.getLabel());
        assertEquals(id, e.getId());
    }

    private void setWrongEventId(POEvent event, long id) {
        // setting the wrong id
        try {
            event.setId(id);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    private Cursor createCursor(long id, String title, String label, Date d1, Date d2) {
        MatrixCursor cursor = new MatrixCursor(OutlookDbHelper.COLUMNS);
        cursor.newRow()
              .add(id)
              .add(d1 == null ? -1 : d1.getTime())
              .add(d2 == null ? -1 : d2.getTime())
              .add(title)
              .add(label);
        cursor.moveToFirst();
        return cursor;
    }

    private void createEventWithWrongCursor(Cursor cursor) {
        try {
            POEvent.createFromCursor(cursor);
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }
}
