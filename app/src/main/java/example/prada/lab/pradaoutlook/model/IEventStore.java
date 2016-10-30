package example.prada.lab.pradaoutlook.model;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.List;

/**
 * Created by prada on 10/30/16.
 */

public interface IEventStore {

    @NonNull List<POEvent> queryEvents(long t1, long t2);

    int countEvents(long t1, long t2);

    boolean hasEvents(long t1, long t2);

    Calendar getFirstEventTime();

    Calendar getLatestEventTime();
}
