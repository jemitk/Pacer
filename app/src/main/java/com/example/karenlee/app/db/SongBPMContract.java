package com.example.karenlee.app.db;

import android.provider.BaseColumns;

/**
 * The contract of database containing song id, title, artist, and beats per minute.
 */
public final class SongBPMContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SongBPMContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SongBPMEntry implements BaseColumns {
        public static final String TABLE_NAME = "bpmmapping";
        public static final String COLUMN_NAME_SONG_ID = "songid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_BPM = "bpm";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String REAL_TYPE = " REAL";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SongBPMEntry.TABLE_NAME + " (" +
                    SongBPMEntry._ID + " INTEGER PRIMARY KEY," +
                    SongBPMEntry.COLUMN_NAME_SONG_ID + TEXT_TYPE + COMMA_SEP +
                    SongBPMEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    SongBPMEntry.COLUMN_NAME_ARTIST + TEXT_TYPE + COMMA_SEP +
                    SongBPMEntry.COLUMN_NAME_BPM + REAL_TYPE +
            " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SongBPMEntry.TABLE_NAME;
}
