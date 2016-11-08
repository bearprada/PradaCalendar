package example.prada.lab.pradaoutlook.store;

import android.content.Context;

/**
 * Created by prada on 10/31/16.
 */

public class EventStoreFactory {
    /**
     * Getting the store instance which store should be the singleton.
     * @param ctx
     * @return the event store's implementation
     */
    public static IEventStore getInstance(Context ctx) {
        return ContentProviderEventStore.getInstance(ctx);
    }
}
