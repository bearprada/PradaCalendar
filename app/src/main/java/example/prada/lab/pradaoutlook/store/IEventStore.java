package example.prada.lab.pradaoutlook.store;

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

    @NonNull List<POEvent> queryEvents(long t1, long t2);

    @NonNull POEvent queryEvent(long t1, long t2, int index);

    int countEvents(long t1, long t2);

    boolean hasEvents(long t1, long t2);

    Calendar getFirstEventTime();

    Calendar getLatestEventTime();

    void addEvent(POEvent event);

    void addEvents(Collection<POEvent> events);

    void addListener(IEventDataUpdatedListener listener);

    void removeListener(IEventDataUpdatedListener listener);
}
