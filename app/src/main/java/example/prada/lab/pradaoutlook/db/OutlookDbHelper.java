package example.prada.lab.pradaoutlook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by prada on 10/31/16.
 */

public class OutlookDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "prada_outlook";
    private static final int DATABASE_VERSION = 1;

    public static final String NAME = "events";
    public static final String ID = "_id";
    public static final String EVENT_START_TIME = "start_time";
    public static final String EVENT_END_TIME = "end_time";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_LABEL = "label";


    public OutlookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "create table " + NAME
            + " ("
            + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT"
            + ", " + EVENT_START_TIME + " LONG NOT NULL"
            + ", " + EVENT_END_TIME + " LONG NOT NULL"
            + ", " + EVENT_TITLE + " TEXT NOT NULL"
            + ", " + EVENT_LABEL + " TEXT" // can be null
            + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing for now
    }
}
