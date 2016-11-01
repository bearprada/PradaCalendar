package example.prada.lab.pradaoutlook.store;

import android.content.Context;

/**
 * Created by prada on 10/31/16.
 */

public class EventStoreFactory {
    public static IEventStore getInstance(Context ctx) {
        // TODO implement the content provider version later
        return MockEventStore.getInstance();
    }
}
