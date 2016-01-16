package com.example.karenlee.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;
import android.widget.ListView;

import com.example.karenlee.app.db.SongBPMContract;
import com.example.karenlee.app.db.SongBPMDbHelper;

public class RetrieveSongsActivity extends AppCompatActivity {

    private ArrayList<Song> songList;
    static final String TAG = "RETRIEVE_SONGS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate and populate the song list
        songList = new ArrayList<Song>();
        getSongList();

        Log.d(TAG, "SIZE IS = " + songList.size());

        // Helper class used to put data to the DB
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());
        dbHelper.addSongs(songList);

        Intent intent = new Intent(this, GetSongsActivity.class);
        startActivity(intent);
        finish();
    }

    // Get the song list from the device
    public void getSongList() {
        // Get the music contents
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // iterate over the results to check whether we have valid data
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }
}