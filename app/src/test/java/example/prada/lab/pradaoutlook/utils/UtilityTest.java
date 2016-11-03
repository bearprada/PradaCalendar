package example.prada.lab.pradaoutlook.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Calendar;

/**
 * Created by prada on 11/1/16.
 */

public class UtilityTest {

    @Test
    public void testDaysBetweenLeafYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.DAY_OF_YEAR, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2017);
        c2.set(Calendar.DAY_OF_YEAR, 1);
        int days = Utility.getDaysBetween(c1, c2);
        Assert.assertEquals(366, days);
    }

    // FIXME
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
        Assert.assertEquals(730479, Utility.getDaysBetween(c1, c2));
        // 365243 vs 365237 > 6   (365250...+7...+13)
        // 730485 vs 730479 > 6   (730499...+14...+20)
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
        Assert.assertEquals(136, Utility.getDaysBetween(c1, c2));
    }

    @Test
    public void testDaysBetweenHalfYear() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2000);
        c1.set(Calendar.MONTH, Calendar.JULY);
        c1.set(Calendar.DAY_OF_MONTH, 15); // 366 - (31+29+31+30+31+30+15) = 169
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2001);
        c2.set(Calendar.MONTH, Calendar.JULY);
        c2.set(Calendar.DAY_OF_MONTH, 15); // (31+28+31+30+31+30+15) = 196
        int days = Utility.getDaysBetween(c1, c2);
        Assert.assertEquals(365, days);
    }

    @Test
    public void testDaysBetweenWrongArgs() throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, 2016);
        c1.set(Calendar.DAY_OF_YEAR, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, 2017);
        c2.set(Calendar.DAY_OF_YEAR, 1);
        int days = Utility.getDaysBetween(c2, c1);
        Assert.assertEquals(0, days);
    }

    @Test
    public void testConvertMonthStr() throws Exception {
        for (int i = 0; i <= 11; i++) {
            String str = Utility.convertMonthStr(i);
            Assert.assertNotNull(str);
            Assert.assertTrue(!"".equals(str));
        }
    }

    @Test
    public void testConvertMonthStrWrongArgs() throws Exception {
        try {
            Utility.convertMonthStr(Integer.MIN_VALUE);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }

        try {
            Utility.convertMonthStr(12);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }

        try {
            Utility.convertMonthStr(-1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }
    }

    @Test
    public void testConvertDayStr() throws Exception {
        for (int i = 1; i <= 7; i++) {
            String str = Utility.convertDayStr(i);
            Assert.assertNotNull(str);
            Assert.assertTrue(!"".equals(str));
        }
    }

    @Test
    public void testConvertDayStrWrongArgs() throws Exception {
        try {
            Utility.convertDayStr(Integer.MIN_VALUE);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }

        try {
            Utility.convertDayStr(8);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }

        try {
            Utility.convertDayStr(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // pass
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }
    }
}
