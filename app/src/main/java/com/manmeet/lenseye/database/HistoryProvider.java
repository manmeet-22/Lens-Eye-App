package com.manmeet.lenseye.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class HistoryProvider extends ContentProvider {

    public static final String LOG_TAG = HistoryProvider.class.getSimpleName();

    private static final int HISTORYS = 100;

    /**
     * URI matcher code for the content URI for a single product in the history table
     */
    private static final int HISTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        // The content URI of the form "content://com.example.android.history/history" will map to the
        // integer code {@link #HISTORYS}. This URI is used to provide access to MULTIPLE rows
        // of the history table.
        sUriMatcher.addURI(HistoryContract.CONTENT_AUTHORITY, HistoryContract.PATH_HISTORY, HISTORYS);
        // For example, "content://com.example.android.history/history/3" matches, but
        // "content://com.example.android.history/history" (without a number at the end) doesn't match.
        sUriMatcher.addURI(HistoryContract.CONTENT_AUTHORITY, HistoryContract.PATH_HISTORY + "/#", HISTORY_ID);
    }

    /**
     * Database helper object
     */
    private HistoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new HistoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case HISTORYS:
                cursor = database.query(HistoryContract.HistoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case HISTORY_ID:
                selection = HistoryContract.HistoryEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HistoryContract.HistoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HISTORYS:
                return insertHistory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertHistory(Uri uri, ContentValues values) {
        // Check that the name is not null
        String historyImage = values.getAsString(HistoryContract.HistoryEntry.COLUMN_IMAGE);
        if (historyImage == null) {
            throw new IllegalArgumentException("History requires valid name");
        }

        String historyList = values.getAsString(HistoryContract.HistoryEntry.COLUMN_RESULT_LIST);
        if (historyList == null) {
            throw new IllegalArgumentException("History requires valid name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(HistoryContract.BASE_CONTENT_URI, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HISTORYS:
                return updateHistory(uri, contentValues, selection, selectionArgs);
            case HISTORY_ID:
                selection = HistoryContract.HistoryEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHistory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateHistory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(HistoryContract.HistoryEntry.COLUMN_IMAGE)) {
            String historyImage = values.getAsString(HistoryContract.HistoryEntry.COLUMN_IMAGE);
            if (historyImage == null) {
                throw new IllegalArgumentException("History requires valid name");
            }
        }
        if (values.containsKey(HistoryContract.HistoryEntry.COLUMN_IMAGE)) {
            String historyResultList = values.getAsString(HistoryContract.HistoryEntry.COLUMN_RESULT_LIST);
            if (historyResultList == null) {
                throw new IllegalArgumentException("History requires valid name");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(HistoryContract.HistoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HISTORYS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(HistoryContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case HISTORY_ID:
                // Delete a single row given by the ID in the URI
                selection = HistoryContract.HistoryEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(HistoryContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HISTORYS:
                return HistoryContract.HistoryEntry.CONTENT_LIST_TYPE;
            case HISTORY_ID:
                return HistoryContract.HistoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

