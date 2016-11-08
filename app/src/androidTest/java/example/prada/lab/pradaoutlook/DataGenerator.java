package example.prada.lab.pradaoutlook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 11/8/16.
 */

public class DataGenerator {

    public static List<POEvent> createEventList(int num) {
        List<POEvent> events = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.YEAR, 2000);
            c1.set(Calendar.MONTH, i);
            Calendar c2 = Calendar.getInstance();
            c2.set(Calendar.YEAR, 2000);
            c2.set(Calendar.MONTH, i + 1);
            POEvent event = new POEvent("Event" + i, "Label" + i, c1.getTime(), c2.getTime());
            events.add(event);
        }
        return events;
    }

    public static List<POEvent> createEventList(int startDay, int endDay) {

        Calendar baseDate = Calendar.getInstance();
        baseDate.set(Calendar.YEAR, 2020);
        baseDate.set(Calendar.MONTH, 1);
        baseDate.set(Calendar.DAY_OF_MONTH, 1);

        List<POEvent> events = new ArrayList<>();
        for (int i = startDay; i <= endDay ; i++) {
            baseDate.set(Calendar.DAY_OF_YEAR, i);
            Date d1 = baseDate.getTime();
            Date d2 = new Date(d1.getTime());
            d2.setMinutes(d2.getMinutes() + 1);
            POEvent event = new POEvent("Event" + i, "Label" + i, d1, d2);
            events.add(event);
        }
        return events;
    }
}
