package example.prada.lab.pradaoutlook.model

import android.database.Cursor

fun interface IEventDataUpdatedListener {
    fun onEventsInsert(events: Cursor)
}
