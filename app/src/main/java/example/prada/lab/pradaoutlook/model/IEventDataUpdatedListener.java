package example.prada.lab.pradaoutlook.model;

import android.database.Cursor;


/**
 * Created by prada on 10/31/16.
 */

public interface IEventDataUpdatedListener {
    void onEventsInsert(Cursor events);
}
