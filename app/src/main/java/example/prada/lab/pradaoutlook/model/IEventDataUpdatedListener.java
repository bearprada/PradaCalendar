package example.prada.lab.pradaoutlook.model;

import java.util.Collection;

/**
 * Created by prada on 10/31/16.
 */

public interface IEventDataUpdatedListener {
    void onEventsInsert(Collection<POEvent> events);
    void onEventInsert(POEvent event);
    void onEventUpdate(POEvent event);
    void onEventDelete(POEvent event);
}
