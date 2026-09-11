package example.prada.lab.pradaoutlook.store

import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener

abstract class BaseEventStore : IEventStore {
    @JvmField protected val mListeners = ArrayList<IEventDataUpdatedListener>()

    override fun addListener(listener: IEventDataUpdatedListener?) {
        if (listener != null && !mListeners.contains(listener)) mListeners.add(listener)
    }

    override fun removeListener(listener: IEventDataUpdatedListener?) {
        if (listener != null) mListeners.remove(listener)
    }
}
