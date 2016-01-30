package com.example.karenlee.app.db;

import static com.example.karenlee.app.db.SongBPMContract.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.karenlee.app.Song;

import java.util.ArrayList;

/**
 * The helper database class. Create an instance of this to access the song-bpm db mapping.
 *
 * Two main methods you will use are:
 *  instance.getWritableDatabase();
 *  instance.getReadableDatabase();
 */
public class SongBPMDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SongBPM.db";

    public static SongBPMDbHelper mInstance = null;

    public static SongBPMDbHelper getInstance(Context activityContext) {
        // get the application context
        if (mInstance == null)
            mInstance = new SongBPMDbHelper(activityContext.getApplicationContext());
        return mInstance;
    }
    public SongBPMDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: ???
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // private helper method to create the value.
    // db should be the writable database.
    public void addSong(SQLiteDatabase db, Song song) {
        ContentValues values;

        values = new ContentValues();
        values.put(SongBPMContract.SongBPMEntry.COLUMN_NAME_ARTIST, song.getArtist());
        values.put(SongBPMContract.SongBPMEntry.COLUMN_NAME_TITLE, song.getTitle());
        values.put(SongBPMContract.SongBPMEntry.COLUMN_NAME_SONG_ID, song.getID());
        values.put(SongBPMContract.SongBPMEntry.COLUMN_NAME_BPM, song.getBpm());

        // Insert the new row
        db.insert(SongBPMContract.SongBPMEntry.TABLE_NAME,
                SongBPMContract.SongBPMEntry.COLUMN_NAME_BPM,
                values);
    }

    // don't know where to close the database; after every insertion?
    public void addSongs(ArrayList<Song> songList) {
        // Get the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // create values for each song, and put it into the db
        for (Song song : songList) {
            addSong(db, song);
        }

        db.close();
    }

    /**
     * Given a list of songs, remove their current entries in the database, and add a new one.
     * @param songList The list of songs with some aspect modified
     */
    public void updateSongs(ArrayList<Song> songList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Song s : songList) {
            deleteSongWithId(db, s);
            addSong(db, s);
        }
    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + SongBPMEntry.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song(Integer.parseInt(cursor.getString(1)),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4));
                // Adding song to list
                songList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        // return song list
        return songList;
    }

    public Song getBpmSong(double bpm) {
        ArrayList<Song> songList = getSongs();
        if (songList.size() == 0) {
            throw new IllegalStateException("No songs in the database");
        } else {
            Song closest = songList.get(0);

            for (Song s : songList) {
                if (Math.abs(s.getBpm() - bpm) < Math.abs(closest.getBpm() - bpm)) {
                    closest = s;
                }
            }
            return closest;
        }
    }

    // cannot be used separately.
    private void deleteSongWithId(SQLiteDatabase db, Song song) {
        String selection = SongBPMEntry.COLUMN_NAME_SONG_ID + "=?";
        String[] selectionArgs = {Long.toString(song.getID())};
        db.delete(SongBPMEntry.TABLE_NAME, selection, selectionArgs);
    }

    // cannot be used separately. a private function
    private void deleteSong(SQLiteDatabase db, Song song) {
        // Define where clause
        String selection = "?=? AND ?=?";
        String[] selectionArgs = {SongBPMEntry.COLUMN_NAME_ARTIST,
                "\'" + song.getArtist() + "\'",
                SongBPMEntry.COLUMN_NAME_TITLE,
                "\'" + song.getTitle() + "\'"};
        Log.i("SongBPMDbHelper", "song artist is - " + song.getArtist());
        Log.i("SongBPMDbHelper", "song title is - " + song.getTitle());
        // Issue SQL statement.
        db.delete(SongBPMEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteSongs(ArrayList<Song> songs) {
        SQLiteDatabase db = this.getWritableDatabase();

        // delete each song from the file
        for (Song song : songs) {
            deleteSongWithId(db, song);
        }

        db.close();
    }

    public void resetDb() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(SongBPMEntry.TABLE_NAME, "", new String[0]);

        db.close();
    }
}