package example.prada.lab.pradaoutlook.store;

import java.util.ArrayList;
import java.util.List;

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;

/**
 * Created by prada on 11/1/16.
 */

public abstract class BaseEventStore implements IEventStore {

    List<IEventDataUpdatedListener> mListeners = new ArrayList<>();

    @Override
    public final void addListener(IEventDataUpdatedListener listener) {
        if (listener == null || mListeners.contains(listener)) {
            return;
        }
        mListeners.add(listener);
    }

    @Override
    public final void removeListener(IEventDataUpdatedListener listener) {
        if (listener == null || mListeners.contains(listener)) {
            return;
        }
        mListeners.remove(listener);
    }
}
