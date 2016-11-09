package example.prada.lab.pradaoutlook.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.Calendar;

/**
 * Created by prada on 11/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class UtilityTest {

    @Test
    public void testDaysBetweenLeafYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.DAY_OF_YEAR, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2017);
        c2.set(Calendar.DAY_OF_YEAR, 1);
        assertEquals(366, Utility.getDaysBetween(c1, c2));
    }

    @Test
    public void testDaysBetweenLargeYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 1000);
        c1.set(Calendar.MONTH, Calendar.JULY);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 3000);
        c2.set(Calendar.MONTH, Calendar.JULY);
        c2.set(Calendar.DAY_OF_MONTH, 1);
        assertEquals(730479, Utility.getDaysBetween(c1, c2));
    }

    @Test
    public void testDaysTheSameYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2080);
        c1.set(Calendar.MONTH, Calendar.MARCH);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2080);
        c2.set(Calendar.MONTH, Calendar.JULY);
        c2.set(Calendar.DAY_OF_MONTH, 15);
        assertEquals(136, Utility.getDaysBetween(c1, c2));
    }

    @Test
    public void testDaysBetweenHalfYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000);
        c1.set(Calendar.MONTH, Calendar.JULY);
        c1.set(Calendar.DAY_OF_MONTH, 15);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2001);
        c2.set(Calendar.MONTH, Calendar.JULY);
        c2.set(Calendar.DAY_OF_MONTH, 15);
        assertEquals(365, Utility.getDaysBetween(c1, c2));
    }

    @Test
    public void testDaysBetweenWrongArgs() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.DAY_OF_YEAR, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2017);
        c2.set(Calendar.DAY_OF_YEAR, 1);
        assertEquals(0, Utility.getDaysBetween(c2, c1));
        assertEquals(0, Utility.getDaysBetween(c1, null));
        assertEquals(0, Utility.getDaysBetween(null, c2));
        assertEquals(0, Utility.getDaysBetween(null, null));
    }

    @Test
    public void testConvertMonthStr() throws Exception {
        for (int i = 0; i <= 11; i++) {
            String str = Utility.convertMonthStr(i);
            assertNotNull(str);
            assertTrue(!"".equals(str));
        }
    }

    @Test
    public void testConvertMonthStrWrongArgs() throws Exception {
        try {
            Utility.convertMonthStr(Integer.MIN_VALUE);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            Utility.convertMonthStr(12);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            Utility.convertMonthStr(-1);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void testConvertDayStr() throws Exception {
        for (int i = 1; i <= 7; i++) {
            String str = Utility.convertDayStr(i);
            assertNotNull(str);
            assertTrue(!"".equals(str));
        }
    }

    @Test
    public void testConvertDayStrWrongArgs() throws Exception {
        try {
            Utility.convertDayStr(Integer.MIN_VALUE);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            Utility.convertDayStr(8);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }

        try {
            Utility.convertDayStr(0);
            fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

    @Test
    public void testDurationStr() throws Exception {
        long oneMin = 60 * 1000;
        long oneHour = 60 * 60 * 1000;
        long oneDay = 24 * 60 * 60 * 1000;
        assertEquals("1m", Utility.getDurationString(oneMin));
        assertEquals("1h", Utility.getDurationString(oneHour));
        assertEquals("10h", Utility.getDurationString(10 * oneHour));
        assertEquals("1h20m", Utility.getDurationString(oneHour + (20 * oneMin)));
        assertEquals("1d", Utility.getDurationString(oneDay));
        assertEquals("30d", Utility.getDurationString(30 * oneDay));
        assertEquals("1d10h", Utility.getDurationString(oneDay + (10 * oneHour)));
        assertEquals("1d10h20m", Utility.getDurationString(oneDay + (10 * oneHour) + (20 * oneMin)));
    }

    @Test
    public void testDurationStrWithWrongInput() throws Exception {
        long oneSecond = 1000;
        assertEquals("", Utility.getDurationString(-1));
        assertEquals("", Utility.getDurationString(oneSecond));
        assertEquals("", Utility.getDurationString(oneSecond * 59));
        assertEquals("", Utility.getDurationString(Integer.MIN_VALUE));
    }
}
