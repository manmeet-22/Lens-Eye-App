package com.manmeet.lenseye.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HistoryDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "history.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        Log.i(LOG_TAG, "Table Created");
        String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " ("
                + HistoryContract.HistoryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HistoryContract.HistoryEntry.COLUMN_IMAGE + " TEXT NOT NULL, "
                + HistoryContract.HistoryEntry.COLUMN_RESULT_LIST + " TEXT );";
        Log.i(LOG_TAG, SQL_CREATE_HISTORY_TABLE);
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

