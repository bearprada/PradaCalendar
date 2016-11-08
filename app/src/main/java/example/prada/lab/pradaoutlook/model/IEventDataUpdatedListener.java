package example.prada.lab.pradaoutlook.model;

import android.database.Cursor;


/**
 * Created by prada on 10/31/16.
 */

public interface IEventDataUpdatedListener {

    /**
     * this callback will trigger when the store has inserted the event through {@IEventStore.addEvents()}
     * @param events the cursor indicates the whole events in the store.
     */
    void onEventsInsert(Cursor events);
}
