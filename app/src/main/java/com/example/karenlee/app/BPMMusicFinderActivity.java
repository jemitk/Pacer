package com.example.karenlee.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import java.util.*;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.*;

public class BPMMusicFinderActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private ArrayList<Long> bpms= new ArrayList<Long>();
    private int numSongs;
    private MusicService musicSrv;
    static final String TAG = "BPMMUSICFINDER";
    private MusicController controller;
    private int songIndex = 0;
    private int tapCounter = 0;
    private long startTime = 0;
    private ArrayList<Song> songs;
    private Intent playIntent;
    static final String EXTRA_BPM = "com.example.karenlee.extras.EXTRA_BPM";
    private boolean musicBound=false;

    private void setController() {
        // set the controller up
        // we make a helper function since we need to set it up more than once in the life cycle of the app
        controller = new MusicController(this);

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
            //pass list
            musicSrv.setList(songs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private SongBPMDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new SongBPMDbHelper(getApplicationContext());
        setContentView(R.layout.activity_bpmmusic_finder);
        findViewById(R.id.tapbutton).setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //If this is the first tap for the song
                if (tapCounter == 0){
                    startTime = System.currentTimeMillis();
                }
                //If this is the last tap for the song
                if (tapCounter == 10){

                    long endTime = System.currentTimeMillis();
                    long timeSpan = endTime - startTime;
                    long bpm = 600000/timeSpan;
                    //TODO: store directly to db
                    bpms.add(songIndex, bpm);

                    tapCounter = 0;
                    songIndex++;
                    playSong(songIndex);
                }else{
                    //All other cases
                    tapCounter++;
                }
                return false;
            }
        });
        Intent mainIntent = getIntent();
        songs = (ArrayList<Song>)mainIntent.getSerializableExtra(SetupActivity.EXTRA_SONGS);
        numSongs = songs.size();
        setController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            Log.d(TAG, "creating a play intent!");
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.d(TAG, "player service started!");
        }
        playSong(songIndex);
    }

    //TODO: set up the music server
    public void playSong(int index){
        musicSrv.setSong(index);
        musicSrv.playSong();
    }

    public void goToMain(){
        Intent mainIntent = new Intent(this, SetupActivity.class);
        mainIntent.putExtra(EXTRA_BPM, bpms);
        setResult(RESULT_OK, mainIntent);
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
