package example.prada.lab.pradaoutlook.store;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;

/**
 * Created by prada on 10/30/16.
 */

public interface IEventStore {

    Cursor getEvents();

    int countEvents();

    boolean hasEvents(long t1, long t2);

    Calendar getFirstEventTime() throws IllegalStateException;

    Calendar getLatestEventTime() throws IllegalStateException;

    void addEvents(Collection<POEvent> events);

    void addListener(IEventDataUpdatedListener listener);

    void removeListener(IEventDataUpdatedListener listener);

    // TODO this api only for the test case
    void removeAllRecords();
}
