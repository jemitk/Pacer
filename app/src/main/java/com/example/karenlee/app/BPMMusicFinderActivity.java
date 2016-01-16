package com.example.karenlee.app;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.*;

public class BPMMusicFinderActivity extends AppCompatActivity {

    private ArrayList<Double> bpms= new ArrayList<Double>();
    private MusicService musicSrv;
    private int songIndex = 0;
    private int tapCounter = 0;
    private long startTime = 0;
    private ArrayList<Song> songs;
    static final String EXTRA_BPM = "com.example.karenlee.extras.EXTRA_BPM";

    private SongBPMDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new SongBPMDbHelper(getApplicationContext());
        setContentView(R.layout.activity_bpmmusic_finder);
        Intent mainIntent = getIntent();
        songs = (ArrayList<Song>)mainIntent.getSerializableExtra(SetupActivity.EXTRA_SONGS);
    }

    public void playSong(int index){
        musicSrv.setSong(index);
        musicSrv.playSong();
    }

    public void tap(){
        //If this is the first tap for the song

        if (tapCounter == 0){
            startTime = System.currentTimeMillis();
        }
        if (tapCounter == 10){

            long endTime = System.currentTimeMillis();
            long timeSpan = endTime - startTime;
            long bpm = 600000/timeSpan;
            //TODO: save bpm to arraylist
            tapCounter = 0;
            //TODO: next song
        }else{
            tapCounter++;
        }

    }

    public void goToMain(){
        Intent mainIntent = new Intent(this, SetupActivity.class);
        mainIntent.putExtra(EXTRA_BPM, bpms);
        setResult(RESULT_OK, mainIntent);
        finish();
    }


}
