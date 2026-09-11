package example.prada.lab.pradaoutlook.store

import android.database.Cursor
import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener
import example.prada.lab.pradaoutlook.model.POEvent
import java.util.Calendar

interface IEventStore {
    fun getEvents(): Cursor
    fun countEvents(): Int
    fun countEvents(t1: Long, t2: Long): Int
    fun getFirstEventTime(): Calendar
    fun getLatestEventTime(): Calendar
    fun addEvents(events: Collection<POEvent>?)
    fun addListener(listener: IEventDataUpdatedListener?)
    fun removeListener(listener: IEventDataUpdatedListener?)
    fun removeAllRecords()
}
