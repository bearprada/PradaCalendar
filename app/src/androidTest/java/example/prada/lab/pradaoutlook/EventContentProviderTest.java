package example.prada.lab.pradaoutlook;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.getbase.android.db.provider.ProviderAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.db.OutlookDbHelper;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 11/7/16.
 */
@RunWith(AndroidJUnit4.class)
public class EventContentProviderTest {
    private ContentResolver mContentResolver;

    @Before
    public void setupContentProvider() {
        mContentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();
        mContentResolver.delete(EventContentProvider.EVENT_URI, "1", null);
    }

    @After
    public void cleanContentProvider() {
        mContentResolver.delete(EventContentProvider.EVENT_URI, "1", null);
    }

    @Test
    public void testDeleteItems() {
        int NUM = 10;
        List<Long> ids = insertEvents(DataGenerator.createEventList(NUM));
        assertEquals(NUM, ids.size());
        assertEquals(NUM, ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.EVENT_ID)
            .perform(mContentResolver)
            .getCount());

        int deletedCount = mContentResolver.delete(EventContentProvider.EVENT_URI, "1", null);
        assertEquals(NUM, deletedCount);
    }

    @Test
    public void testDeleteAnItem() {
        int NUM = 10;
        List<Long> ids = insertEvents(DataGenerator.createEventList(NUM));
        assertEquals(NUM, ids.size());
        assertEquals(NUM, getTotalEvents());

        int count = NUM;
        for (Long id : ids) {
            Uri uri = EventContentProvider.createEventIdUri(id);
            int deletedCount = mContentResolver.delete(uri, null, null);
            assertEquals(1, deletedCount);
            assertFalse(hasEvent(uri));
            assertEquals(--count, getTotalEvents());
        }
    }

    @Test
    public void testDeleteItemWithWrongParameters() {
        assertEquals(0, mContentResolver.delete(EventContentProvider.createEventIdUri(0), null, null));
        assertEquals(0, mContentResolver.delete(EventContentProvider.createEventIdUri(Long.MAX_VALUE), null, null));
        assertEquals(0, mContentResolver.delete(EventContentProvider.createEventIdUri(0), "asdf", new String[]{"a","b","c"}));
    }

    @Test
    public void testInsertItems() {
        int NUM = 100;
        List<POEvent> events = DataGenerator.createEventList(NUM);
        List<Long> ids = insertEvents(events);
        assertEquals(NUM, ids.size());
        assertEquals(NUM, ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.EVENT_ID)
            .perform(mContentResolver)
            .getCount());

        int i = 0;
        for (Long id : ids) {
            Uri uri = EventContentProvider.createEventIdUri(id);
            Cursor c = mContentResolver.query(uri, OutlookDbHelper.COLUMNS, null, null, null);
            c.moveToFirst();
            assertEquals(1, c.getCount());
            assertEquals(events.get(i), POEvent.createFromCursor(c));
            i++;
        }
    }

    @Test
    public void testInsertItemWithInvalidParameters() {
        verifyWrongUri(mContentResolver.insert(EventContentProvider.EVENT_URI, null));

        assertNull(mContentResolver.insert(EventContentProvider.createEventIdUri(1), null));

        ContentValues cv1 = new ContentValues();
        cv1.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv1.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        cv1.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);
        verifyWrongUri(mContentResolver.insert(EventContentProvider.EVENT_URI, cv1));

        ContentValues cv2 = new ContentValues();
        cv2.put(OutlookDbHelper.EVENT_TITLE, "testcase title");
        cv2.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv2.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);
        verifyWrongUri(mContentResolver.insert(EventContentProvider.EVENT_URI, cv2));

        ContentValues cv3 = new ContentValues();
        cv3.put(OutlookDbHelper.EVENT_TITLE, "testcase title");
        cv3.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv3.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        verifyWrongUri(mContentResolver.insert(EventContentProvider.EVENT_URI, cv3));
    }

    @Test
    public void testUpdateItems() {

        ContentValues cv = new ContentValues();
        cv.put(OutlookDbHelper.EVENT_TITLE, "testcase Title");
        cv.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        cv.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);

        Uri uri = mContentResolver.insert(EventContentProvider.EVENT_URI, cv);
        long id = ContentUris.parseId(uri);
        assertTrue(id > 0);

        cv.put(OutlookDbHelper.EVENT_TITLE, "updated title");
        cv.put(OutlookDbHelper.EVENT_LABEL, "updated label");
        cv.put(OutlookDbHelper.EVENT_START_TIME, 1000);
        cv.put(OutlookDbHelper.EVENT_END_TIME, 2000);
        assertEquals(1, mContentResolver.update(uri, cv, null, null));

        verifyContentValues(cv, getContentValues(mContentResolver.query(uri, null, null, null, null)));

        ContentValues cv2 = new ContentValues();
        cv2.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv2.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        cv2.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);
        assertEquals(1, mContentResolver.update(EventContentProvider.createEventIdUri(id), cv2, null, null));
        verifyContentValues(cv2, getContentValues(mContentResolver.query(uri, null, null, null, null)));

        ContentValues cv3 = new ContentValues();
        cv3.put(OutlookDbHelper.EVENT_TITLE, "testcase Title");
        cv3.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv3.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);
        assertEquals(1, mContentResolver.update(EventContentProvider.createEventIdUri(id), cv3, null, null));
        verifyContentValues(cv3, getContentValues(mContentResolver.query(uri, null, null, null, null)));

        ContentValues cv4 = new ContentValues();
        cv4.put(OutlookDbHelper.EVENT_TITLE, "testcase Title");
        cv4.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv4.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        assertEquals(1, mContentResolver.update(EventContentProvider.createEventIdUri(id), cv4, null, null));
        verifyContentValues(cv4, getContentValues(mContentResolver.query(uri, null, null, null, null)));
    }

    @Test
    public void testUpdateItemWithInvalidParameters() {
        ContentValues cv = new ContentValues();
        cv.put(OutlookDbHelper.EVENT_TITLE, "testcase Title");
        cv.put(OutlookDbHelper.EVENT_LABEL, "testcase Label");
        cv.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        cv.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);

        Uri uri = mContentResolver.insert(EventContentProvider.EVENT_URI, cv);
        long id = ContentUris.parseId(uri);
        assertTrue(id > 0);

        cv.put(OutlookDbHelper.EVENT_TITLE, "updated title");
        cv.put(OutlookDbHelper.EVENT_LABEL, "updated label");
        cv.put(OutlookDbHelper.EVENT_START_TIME, 1000);
        cv.put(OutlookDbHelper.EVENT_END_TIME, 2000);

        assertEquals(0, mContentResolver.update(EventContentProvider.EVENT_URI, cv, null, null));
        assertEquals(0, mContentResolver.update(EventContentProvider.createEventIdUri(-1), cv, null, null));
        assertEquals(0, mContentResolver.update(uri, null, null, null));

        // insert the invalid ContentValues
        assertEquals(0, mContentResolver.update(uri, new ContentValues(), null, null));

        ContentValues cv3 = new ContentValues();
        cv3.put(OutlookDbHelper.EVENT_ID, -1);
        cv3.put(OutlookDbHelper.EVENT_TITLE, "testcase Title3");
        cv3.put(OutlookDbHelper.EVENT_LABEL, "testcase Label3");
        cv3.put(OutlookDbHelper.EVENT_START_TIME, System.currentTimeMillis());
        cv3.put(OutlookDbHelper.EVENT_END_TIME, System.currentTimeMillis() + 1000);
        assertEquals(0, mContentResolver.update(uri, cv3, null, null));

    }

    private void verifyWrongUri(Uri uri) {
        assertNotNull(uri);
        long id = ContentUris.parseId(uri);
        assertEquals(-1, id);
    }

    private int getTotalEvents() {
        return ProviderAction
            .query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.EVENT_ID)
            .perform(mContentResolver)
            .getCount();
    }

    private boolean hasEvent(Uri uri) {
        return ProviderAction
            .query(uri)
            .projection(OutlookDbHelper.EVENT_ID)
            .perform(mContentResolver)
            .getCount() > 0;
    }

    private List<Long> insertEvents(List<POEvent> events) {
        List<Long> ids = new ArrayList<>();
        for (POEvent e : events) {
            Uri uri = mContentResolver.insert(EventContentProvider.EVENT_URI, e.getContentValues());
            long id = ContentUris.parseId(uri);
            ids.add(id);
            e.setId(id);
        }
        return ids;
    }

    private ContentValues getContentValues(Cursor cursor) {
        cursor.moveToFirst();
        ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, values);
        return values;
    }

    private void verifyContentValues(ContentValues cv1, ContentValues cv2) {
//        assertEquals(cv1.size(), cv2.size());
        for (String key : cv1.keySet()) {
            assertEquals(cv1.get(key).toString(), cv2.get(key).toString());
        }
    }
}
