package example.prada.lab.pradaoutlook.model

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.github.sundeepk.compactcalendarview.domain.Event
import example.prada.lab.pradaoutlook.R
import example.prada.lab.pradaoutlook.db.OutlookDbHelper
import java.util.Date

open class POEvent(title: String?, label: String?, from: Date?, to: Date?) {
    private val mTitle: String
    private val mLabel: String
    private val mFrom: Date
    private val mTo: Date
    private var mId: Long? = -1L

    init {
        require(!title.isNullOrEmpty()) { "title should not be null or empty" }
        require(!label.isNullOrEmpty()) { "label should not be null or empty" }
        require(from != null && to != null) { "the date range should not be null, but the from = $from, to = $to" }
        require(!from.after(to)) { "the date range is wrong, but the from = $from, to = $to" }
        mTitle = title
        mLabel = label
        mFrom = from
        mTo = to
    }

    private constructor(values: ContentValues) : this(
        values.getAsString(OutlookDbHelper.EVENT_TITLE), values.getAsString(OutlookDbHelper.EVENT_LABEL),
        Date(values.getAsLong(OutlookDbHelper.EVENT_START_TIME)), Date(values.getAsLong(OutlookDbHelper.EVENT_END_TIME))) {
        mId = values.getAsLong(OutlookDbHelper.EVENT_ID)
    }

    open fun getTitle(): String = mTitle
    open fun getLabel(): String = mLabel
    open fun getFrom(): Date = mFrom
    open fun getTo(): Date = mTo
    open fun getId(): Long? = mId
    open fun toEvent(): Event = Event(getColor(), mFrom.time, this)

    @ColorInt open fun getColor(): Int = Color.parseColor(when (mLabel) {
        LABEL_OOO -> "#AED581"
        LABEL_BIRTHDAY -> "#F06292"
        LABEL_OFFICE -> "#7986CB"
        else -> "#666666"
    })

    @DrawableRes open fun getLabelResourceId(): Int = when (mLabel) {
        LABEL_OOO -> R.drawable.label_ooo
        LABEL_BIRTHDAY -> R.drawable.label_birthday
        LABEL_OFFICE -> R.drawable.label_office
        else -> R.drawable.label_others
    }

    fun getContentValues() = ContentValues().apply {
        put(OutlookDbHelper.EVENT_TITLE, mTitle)
        put(OutlookDbHelper.EVENT_LABEL, mLabel)
        put(OutlookDbHelper.EVENT_START_TIME, mFrom.time)
        put(OutlookDbHelper.EVENT_END_TIME, mTo.time)
    }

    fun setId(id: Long) {
        require(id > 0) { "the event id should be over 0" }
        mId = id
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is POEvent && mTitle == other.mTitle && mLabel == other.mLabel && mFrom == other.mFrom && mTo == other.mTo && mId == other.mId)

    override fun hashCode(): Int = (((mTitle.hashCode() * 31 + mLabel.hashCode()) * 31 + mFrom.hashCode()) * 31 + mTo.hashCode()) * 31 + (mId?.hashCode() ?: 0)

    companion object {
        const val LABEL_OOO = "out-of-office"
        const val LABEL_OFFICE = "office"
        const val LABEL_BIRTHDAY = "birthday"

        @JvmStatic fun createFromCursor(cursor: Cursor?): POEvent {
            requireNotNull(cursor) { "the cursor should not be null" }
            return POEvent(ContentValues().also { DatabaseUtils.cursorRowToContentValues(cursor, it) })
        }
    }
}
