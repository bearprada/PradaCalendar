package example.prada.lab.pradaoutlook.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 11/9/16.
 *
 * it's a mock data generator class, so we will not put the test case for the test object
 */
public class MockEventsGenerator {

    private static final String[] NAMES = new String[] {
        "Prada", "Sunny", "Arnaud Vallat", "Brsawler", "Jorge", "Alex", "Lisa", "Chris", "Satya", "Gaga"
    };

    /** Once a week **/
    private static final String[] OOO_ACTIVITIES = new String[] {
        "%s Work from home", "%s in SF", "%s in TW", "%s in LA", "%s in China", "%s in Seattle"
    };

    /** Once a week **/
    private static final String[] OFFICE_ACTIVITES = new String[] {
        "Code review",
        "Developer Meeting",
        "Team Meeting",
        "Demo Time"
    };

    // generate the events between last 3 months to the next 3 months
    public static List<POEvent> generateEvents() {
        java.util.Random random = new java.util.Random();

        List<POEvent> data = new ArrayList<>();

        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.DAY_OF_YEAR, c1.get(Calendar.DAY_OF_YEAR) - 90);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.DAY_OF_YEAR, c2.get(Calendar.DAY_OF_YEAR) + 90);
        c1.set(Calendar.HOUR_OF_DAY, 0);

        // insert the out-of-office events
        for (int i = 0; i < 24; i++) {
            Calendar c = Calendar.getInstance();
            c.setTime(c1.getTime());
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + (7 * i) + random.nextInt(4));
            c.set(Calendar.HOUR_OF_DAY, 0);
            Date t1 = c.getTime();
            Date t2 = new Date(c.getTimeInMillis() + Utility.MILL_SECONDS_A_DAY);
            String title = String.format(OOO_ACTIVITIES[random.nextInt(OOO_ACTIVITIES.length - 1)],
                                         NAMES[random.nextInt(NAMES.length - 1)]);
            data.add(new POEvent(title, POEvent.LABEL_OOO, t1, t2));
        }

        // insert the fake birthday data
        int startDay = c1.get(Calendar.DAY_OF_YEAR);
        for (String name : NAMES) {
            Calendar c = Calendar.getInstance();
            c.setTime(c1.getTime());
            c.set(Calendar.DAY_OF_YEAR, startDay + random.nextInt(180));
            String title = String.format("%s's Birthday", name);
            Date t1 = c.getTime();
            Date t2 = new Date(c.getTimeInMillis() + Utility.MILL_SECONDS_A_DAY);
            data.add(new POEvent(title, POEvent.LABEL_BIRTHDAY, t1, t2));
        }

        // insert the fake office activities
        for (int i = 0; i < 24; i++) {
            Calendar c = Calendar.getInstance();
            c.setTime(c1.getTime());
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + (7 * i));
            c.set(Calendar.HOUR_OF_DAY, 10); // 10 AM

            c.add(Calendar.DAY_OF_WEEK, 1);
            data.add(createOfficeEvent(c, OFFICE_ACTIVITES[0], 1));
            c.add(Calendar.DAY_OF_WEEK, 1);
            data.add(createOfficeEvent(c, OFFICE_ACTIVITES[1], 1.5f));
            c.add(Calendar.DAY_OF_WEEK, 1);
            data.add(createOfficeEvent(c, OFFICE_ACTIVITES[2], 0.5f));
            c.add(Calendar.DAY_OF_WEEK, 1);
            data.add(createOfficeEvent(c, OFFICE_ACTIVITES[3], 2f));
        }
        return data;
    }

    private static POEvent createOfficeEvent(Calendar calendar, String title, float hours) {
        Date t1 = calendar.getTime();
        Date t2 = new Date(
            calendar.getTimeInMillis() + (long)(Utility.MILL_SECONDS_A_HOUR * hours));
        return new POEvent(title, POEvent.LABEL_OFFICE, t1, t2);
    }
}
