package example.prada.lab.pradaoutlook.store;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/30/16.
 */

public interface IEventStore {

    /**
     * Query all of the event objects in the store
     * @return the cursor which indicates the all of events in the store
     */
    @NonNull Cursor getEvents();

    /**
     * Number of events in the store
     * @return the total events count in the store
     */
    int countEvents();

    /**
     * Number of events in the specific duration
     * @param t1 the start of timestamp of the duration
     * @param t2 the end of timestamp of the duration
     * @return the number of events in the specific duration
     * @throws IllegalArgumentException if the t1 is bigger than t1
     */
    int countEvents(long t1, long t2) throws IllegalArgumentException;

    /**
     * the first event time in the store
     * @return the calendar object indicate the first event in the store
     * @throws IllegalStateException the store is empty
     */
    @NonNull Calendar getFirstEventTime() throws IllegalStateException;

    /**
     * the latest event time in the store
     * @return the calendar object indicate the latest event in the store
     * @throws IllegalStateException the store is empty
     */
    @NonNull Calendar getLatestEventTime() throws IllegalStateException;

    /**
     * Inserting the event objects into the store, it should be persistence
     * @param events the events you want to put into the store, it can be unsorted list by {@POEvent.getFrom()}
     */
    void addEvents(Collection<POEvent> events);

    /**
     * adding the listener for getting callback from store when the data is changed
     * @param listener
     */
    void addListener(IEventDataUpdatedListener listener);

    /**
     * removing the listener for getting callback from store when the data is changed
     * @param listener
     */
    void removeListener(IEventDataUpdatedListener listener);

    /**
     * WARNING : this api only for the test case
     * cleaning the all of the events in the store
     */
    void removeAllRecords();
}
