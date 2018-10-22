package com.manmeet.lenseye.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class HistoryContract {
    public static final String CONTENT_AUTHORITY = "com.manmeet.lenseye.database";
    /*Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.*/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_HISTORY = "history";

    private HistoryContract() {
    }

    public static final class HistoryEntry implements BaseColumns {

        /*The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HISTORY);

        /* The MIME type of the {@link #CONTENT_URI} for a list of history.*/
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        /*The MIME type of the {@link #CONTENT_URI} for a single pet.*/
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        /*Name of database table for history */
        public final static String TABLE_NAME = "history";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_RESULT_LIST = "result_list";
        //public static final String COLUMN_DATE = "date";

    }

}

