package example.prada.lab.pradaoutlook;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import example.prada.lab.pradaoutlook.db.OutlookDbHelper;

/**
 * Created by prada on 10/30/16.
 */

public class EventContentProvider extends ContentProvider {
    private OutlookDbHelper mDbHelper;
    private ContentResolver mResolver;

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri EVENT_URI = Uri.parse("content://" + AUTHORITY + "/events");

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int EVENT = 1;
    private static final int EVENT_WITH_ID = 2;

    static {
        sUriMatcher.addURI(AUTHORITY, "events", EVENT);
        sUriMatcher.addURI(AUTHORITY, "events/#", EVENT_WITH_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new OutlookDbHelper(getContext());
        mResolver = getContext().getContentResolver();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case EVENT:
                return db.query(OutlookDbHelper.NAME, projection, selection, selectionArgs,
                                null, null, sortOrder);
            case EVENT_WITH_ID:
                long id = ContentUris.parseId(uri);
                return db.query(OutlookDbHelper.NAME, projection, OutlookDbHelper.EVENT_ID + "=" + id,
                                null, null, null, sortOrder);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case EVENT:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long eventId = db.insert(OutlookDbHelper.NAME, null, values);
                Uri newUri = ContentUris.withAppendedId(EVENT_URI, eventId);
                mResolver.notifyChange(newUri, null);
                return newUri;
            default:
                return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int totalDeletedRow = 0;
        switch (sUriMatcher.match(uri)) {
            case EVENT:
                try {
                    db.beginTransaction();
                    totalDeletedRow = db.delete(OutlookDbHelper.NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                    break;
                } finally {
                    db.endTransaction();
                    mResolver.notifyChange(uri, null);
                }
            case EVENT_WITH_ID:
                long id = ContentUris.parseId(uri);
                try {
                    db.beginTransaction();
                    totalDeletedRow = db.delete(OutlookDbHelper.NAME, OutlookDbHelper.EVENT_ID + "=" + id, null);
                    db.setTransactionSuccessful();
                    break;
                } finally {
                    db.endTransaction();
                    mResolver.notifyChange(uri, null);
                }
        }
        return totalDeletedRow;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case EVENT_WITH_ID:
                long id = ContentUris.parseId(uri);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                int changeRows = db.update(OutlookDbHelper.NAME, values,
                                           OutlookDbHelper.EVENT_ID + "=" + id, selectionArgs);
                mResolver.notifyChange(uri, null);
                return changeRows;
        }
        return 0;
    }
}
