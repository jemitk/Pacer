package com.example.karenlee.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {
    private ArrayList<Song> songList;
    static final String EXTRA_SONGS = "com.example.karenlee.extras.EXTRA_SONGS";

    public void uploadsplash(){
        Intent splashIntent = new Intent(this, PrepareMusicSplash.class);
        startActivity(splashIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uploadsplash();

        setContentView(R.layout.activity_setup);

        // retrieve the ListView instance using the ID we gave it in the main layout
        //songView = (ListView)findViewById(R.id.song_list);
        // instantiate the list
        songList = new ArrayList<Song>();
        getSongList();

        // sort the data alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        // set the adapter
        //SongAdapter songAdt = new SongAdapter(this, songList);
        //songView.setAdapter(songAdt);

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        //setController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        goToBPM();
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

    @Override
    protected void onDestroy() {
        //stopService(playIntent);
        //musicSrv=null;
        super.onDestroy();
    }

    public void goToBPM(){
        Intent bpmIntent = new Intent(this, BPMMusicFinderActivity.class);
        bpmIntent.putExtra(EXTRA_SONGS, songList);
        startActivity(bpmIntent);
    }

    public void goToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void goToRunning() {
        Intent runIntent = new Intent(this, RunActivity.class);
        startActivity(runIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent newItem){
        if (requestCode==0){
            if (resultCode==RESULT_OK){
                //TODO: store into db bpm result from newItem.getStringExtra(some other thing here)
                // Go to the main screen
                goToRunning();
            }
        }
    }

}
