package example.prada.lab.pradaoutlook.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OutlookDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $NAME ($EVENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $EVENT_START_TIME INTEGER NOT NULL, $EVENT_END_TIME INTEGER NOT NULL, $EVENT_TITLE TEXT NOT NULL, $EVENT_LABEL TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    companion object {
        private const val DATABASE_NAME = "prada_outlook"
        private const val DATABASE_VERSION = 1
        const val NAME = "events"
        const val EVENT_ID = "_id"
        const val EVENT_START_TIME = "start_time"
        const val EVENT_END_TIME = "end_time"
        const val EVENT_TITLE = "title"
        const val EVENT_LABEL = "label"
        @JvmField val COLUMNS = arrayOf(EVENT_ID, EVENT_START_TIME, EVENT_END_TIME, EVENT_TITLE, EVENT_LABEL)
    }
}
