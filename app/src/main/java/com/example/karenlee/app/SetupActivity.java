package com.example.karenlee.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;

public class SetupActivity extends AppCompatActivity {
    static final String TAG = "SETUP_ACTIVITY";

    private boolean isSetup = false;
    private ArrayList<Song> songList;
    static final String EXTRA_SONGS = "com.example.karenlee.extras.EXTRA_SONGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        // retrieve the ListView instance using the ID we gave it in the main layout
        //songView = (ListView)findViewById(R.id.song_list);
        // instantiate the list
        songList = new ArrayList<>();
        getSongList();

        // sort the data alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Starting the activity, isSetup boolean is " + isSetup);
        if (isSetup) {
            Log.i(TAG, "Finishing the activity");
            finish();
        } else {
            goToBPM();
            isSetup = true;
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // get the list of songs
    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        // retrieve the URI for external music files
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // iterate over the results
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.IS_MUSIC);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                boolean thisIsMusic = Integer.parseInt(musicCursor.getString(isMusicColumn)) != 0;

                if (thisIsMusic)
                    songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    public void goToBPM(){
        Log.i(TAG, songList.toString());
        Intent bpmIntent = new Intent(this, BPMMusicFinderActivity.class);
        bpmIntent.putExtra(EXTRA_SONGS, songList);
        startActivity(bpmIntent);
    }
}
