package example.prada.lab.pradaoutlook;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/29/16.
 */

public class EventStore {
    private static EventStore sStore;

    public static EventStore getInstance(Context ctx) {
        if (sStore == null) {
            sStore = new EventStore(ctx);
        }
        return sStore;
    }
    private EventStore(Context ctx) {
        // TODO
    }

    // TODO I implemented the mock data here for testing
    java.util.Random random = new java.util.Random();
    @NonNull public List<POEvent> queryEvents(long timestamp1, long timestamp2) {
        List<POEvent> events = new ArrayList<>();
        for (int i = 1; i < 2 /*random.nextInt(4)*/; i++) {
            Date from = new Date(timestamp1);
            from.setHours(from.getHours() + i);
            events.add(new POEvent("Event " + random.nextInt(10000), "TODO", from, from));
        }
        return events;
    }

    // FIXME
    public boolean hasEvents(long timestamp1, long timestamp2) {
        return random.nextBoolean();
    }
}
