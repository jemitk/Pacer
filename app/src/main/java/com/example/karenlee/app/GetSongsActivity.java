package com.example.karenlee.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;

import com.example.karenlee.app.db.SongBPMDbHelper;

public class GetSongsActivity extends AppCompatActivity {

    static final String TAG = "GET_SONGS_ACTIVITY";
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Helper class used to put data to the DB
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());
        songList = new ArrayList<Song>();
        songList = dbHelper.getSongs();
        songList.add(new Song(9999, "TEST", "TEST"));

        /**
        String songsString = "";
        for (Song song: songList) {
            songsString += (song.getTitle() + "\n");
        }
         **/
        Log.d(TAG, "SIZE = " + songList.size());
    }

}
