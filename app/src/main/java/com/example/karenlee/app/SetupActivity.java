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

import com.example.karenlee.app.db.SongBPMDbHelper;

public class SetupActivity extends AppCompatActivity {
    static final String TAG = "SETUP_ACTIVITY";

    private boolean isSetup = false;
    private ArrayList<Song> localSongList;
    private ArrayList<Song> addedSongList = new ArrayList<Song>();
    static final String EXTRA_SONGS = "com.example.karenlee.extras.EXTRA_SONGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        // retrieve the ListView instance using the ID we gave it in the main layout
        //songView = (ListView)findViewById(R.id.song_list);
        // instantiate the list
        localSongList = new ArrayList<>();
        getSongList();

        // sort the data alphabetically
        Collections.sort(localSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        compareToDb(localSongList);
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Added songs are " + addedSongList.toString() + ", isSetup is " + isSetup);
        // If there are no songs to add to the database,
        if (addedSongList.size() == 0 || isSetup) {
            Log.i(TAG, "Songs already in DB; finishing the activity");
            finish();
        } else {
            Log.i(TAG, "Starting Bpm");
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
                    localSongList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    public void goToBPM(){
        Log.i(TAG, addedSongList.toString());
        Intent bpmIntent = new Intent(this, BPMMusicFinderActivity.class);
        bpmIntent.putExtra(EXTRA_SONGS, addedSongList);
        startActivity(bpmIntent);
    }

    public void goToRunning() {
        Intent runIntent = new Intent(this, RunActivity.class);
        startActivity(runIntent);
    }

    public void compareToDb(ArrayList<Song> localSongList) {
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());

        // TODO: for debugging purposes only. deletes all of the data in db
        // dbHelper.resetDb();
        //Log.i(TAG, "Reset database");
        //Log.i(TAG, "After resetting the database, the items in db are: " + dbHelper.getSongs().toString());

        ArrayList<Song> dbSongList = dbHelper.getSongs();

        ArrayList<Song> deleteSongList = new ArrayList<Song>();

        // Get the add song list
        for (Song localSong : localSongList) {
            if (!dbSongList.contains(localSong))
                addedSongList.add(localSong);
        }

        Log.i(TAG, "songs in local machine - " + localSongList.toString());
        Log.i(TAG, "songs in database - " + dbHelper.getSongs().toString());
        Log.i(TAG, "songs that should be added - " + addedSongList.toString());
        // get the delete song list
        for (Song dbSong: dbSongList) {
            if (!localSongList.contains(dbSong))
                deleteSongList.add(dbSong);
        }

        if (deleteSongList.size() != 0) {
            dbHelper.deleteSongs(deleteSongList);
        }

        // no need to add songs;
        // the addedSongList gets sent to the BPMMusicFinderActivity
        // and the BPMMusicFinderActivity adds the songs to the db with the bpm
        //dbHelper.addSongs(addedSongList);
    }
}
