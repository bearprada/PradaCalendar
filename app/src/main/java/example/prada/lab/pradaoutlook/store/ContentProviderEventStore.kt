package example.prada.lab.pradaoutlook.store

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import bolts.Task
import com.getbase.android.db.provider.ProviderAction
import example.prada.lab.pradaoutlook.EventContentProvider
import example.prada.lab.pradaoutlook.db.OutlookDbHelper
import example.prada.lab.pradaoutlook.model.POEvent
import java.util.Calendar
import java.util.concurrent.Callable

open class ContentProviderEventStore private constructor(context: Context) : BaseEventStore() {
    private val resolver = context.contentResolver

    override fun getEvents(): Cursor = ProviderAction.query(EventContentProvider.EVENT_URI)
        .projection(*OutlookDbHelper.COLUMNS)
        .orderBy("${OutlookDbHelper.EVENT_START_TIME} ASC")
        .perform(resolver)

    override fun countEvents(): Int = ProviderAction.query(EventContentProvider.EVENT_URI)
        .projection(OutlookDbHelper.EVENT_ID).perform(resolver).count

    override fun countEvents(t1: Long, t2: Long): Int {
        require(t1 >= 0 && t2 >= 0) { "the timestamp should be positive value, but t1 = $t1, t2 = $t2" }
        return ProviderAction.query(EventContentProvider.EVENT_URI)
            .projection(OutlookDbHelper.EVENT_ID)
            .where<String>("${OutlookDbHelper.EVENT_START_TIME} >= $t1 AND ${OutlookDbHelper.EVENT_START_TIME} <= $t2")
            .perform(resolver).count
    }

    override fun getFirstEventTime(): Calendar = findBoundary(OutlookDbHelper.EVENT_START_TIME, false) { it.getFrom() }
    override fun getLatestEventTime(): Calendar = findBoundary(OutlookDbHelper.EVENT_END_TIME, true) { it.getTo() }

    private fun findBoundary(column: String, descending: Boolean, value: (POEvent) -> java.util.Date): Calendar {
        val cursor = ProviderAction.query(EventContentProvider.EVENT_URI)
            .orderBy("$column ${if (descending) "DESC" else "ASC"}").perform(resolver)
        check(cursor.count > 0) { "it can't find any record" }
        cursor.moveToFirst()
        return Calendar.getInstance().apply { time = value(POEvent.createFromCursor(cursor)) }
    }

    private fun insertEvent(event: POEvent): Uri = ProviderAction.insert(EventContentProvider.EVENT_URI)
        .values(event.getContentValues()).perform(resolver)

    override fun addEvents(events: Collection<POEvent>?) {
        if (events == null) throw NullPointerException("the events list should be null")
        for (event in events) event.setId(ContentUris.parseId(insertEvent(event)))
        Task.call(Callable<Void?> {
            for (listener in mListeners) listener.onEventsInsert(getEvents())
            null
        }, Task.UI_THREAD_EXECUTOR)
    }

    override fun removeAllRecords() {
        ProviderAction.delete(EventContentProvider.EVENT_URI).where<String>("1").perform(resolver)
    }

    companion object {
        private var store: ContentProviderEventStore? = null

        @JvmStatic fun getInstance(context: Context): ContentProviderEventStore {
            if (store == null) store = ContentProviderEventStore(context.applicationContext)
            return store!!
        }
    }
}
