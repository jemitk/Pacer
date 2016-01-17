package com.example.karenlee.app;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.example.karenlee.app.db.SongBPMContract;
import com.example.karenlee.app.db.SongBPMDbHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BPMMusicFinderActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private MusicService musicSrv;
    static final String TAG = "BPM_MUSIC_FINDER";
    private int numSongs;
    private int songIndex = 0;
    private int tapCounter = 0;
    private long startTime = 0;
    private ArrayList<Song> songs = new ArrayList<>();
    private Intent playIntent;
    static final String EXTRA_BPM = "com.example.karenlee.extras.EXTRA_BPM";
    private boolean musicBound=false;

    private void setController() {
        // set the controller up
        // we make a helper function since we need to set it up more than once in the life cycle of the app
        MusicController controller = new MusicController(this);

        // click listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        // controller.setAnchorView(findViewById(R.id.));
        controller.setEnabled(true);
    }

    private void playNext() {

    }

    private void playPrev() {

    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            Log.i(TAG, "Music service has been set:" + musicSrv);
            //pass list
            musicSrv.setList(songs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bpm_music_finder);
        // get the songs from setupActivity
        songs = (ArrayList<Song>) getIntent().getSerializableExtra(SetupActivity.EXTRA_SONGS);

        findViewById(R.id.tapbutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "User has touched the button");
                //If this is the first tap for the song
                if (tapCounter == 0) {
                    startTime = System.currentTimeMillis();
                }
                //If this is the last tap for the song
                if (tapCounter == 3) {

                    long endTime = System.currentTimeMillis();
                    long timeSpan = endTime - startTime;
                    long bpm = 600000 / timeSpan;
                    Log.i(TAG, "Estimated BPM of song: " + bpm);

                    songs.get(songIndex).setBpm(bpm);

                    tapCounter = 0;
                    songIndex++;
                    if (songIndex >= songs.size() - 1) {
                        // we're all done with the songs!
                        Log.i(TAG, "Finished all songs, returning to setup.");
                        putToDb();
                        goToMain();
                    }
                    playSong(songIndex);
                } else {
                    //All other cases
                    tapCounter++;
                }
            }
        });

        String songString = "";
        for (Song s : songs) {
            songString += s.toString() + "\n";
        }
        Log.d(TAG, "Songs currently in db: " + songString);
        numSongs = songs.size();
        setController();
        prepareMusicSplash();
    }

    public void prepareMusicSplash(){
        Log.i(TAG, "Sending Intent to PrepareMusicSplash.");
        Intent prepareIntent = new Intent(this, PrepareMusicSplash.class);
        startActivity(prepareIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null){
            Log.i(TAG, "creating a play intent!");
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.i(TAG, "player service started!");
        } else
        playSong(songIndex);
    }

    public void playSong(int index){
        musicSrv.setSong(index);
        musicSrv.playSong();
    }

    public void putToDb() {
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());
        dbHelper.addSongs(songs);

        Log.i(TAG, "Songs currently in the db: " + dbHelper.getSongs().toString());
    }

    public void goToMain(){
        Log.i(TAG, "Finishing Activity, sending Intent to FinishUploadSplash.");
        Intent lastSplash = new Intent(this, FinishUploadSplash.class);
        startActivity(lastSplash);
        finish();
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
