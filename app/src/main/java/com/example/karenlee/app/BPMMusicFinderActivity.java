package com.example.karenlee.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.ArrayList;

public class BPMMusicFinderActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private MusicService musicSrv;
    static final String TAG = "BPM_MUSIC_FINDER";
    static final int TAP_MEASURES = 2;
    private int numSongs;
    private TapCounter counter;
    private ArrayList<Song> songs = new ArrayList<>();
    private Intent playIntent;
    static final String EXTRA_BPM = "com.example.karenlee.extras.EXTRA_BPM";
    private boolean musicBound=false;
    static final String EXTRA_SONGS = "com.example.karenlee.extras.EXTRA_SONGS";
    private boolean addNew = true;
    static final String EXTRA_ADD_NEW = "com.example.karenlee.extras.EXTRA_UPDATE_OR_ADD";

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
        counter = new TapCounter(TAP_MEASURES);

        setContentView(R.layout.activity_bpm_music_finder);
        ((TextView)findViewById(R.id.textCount)).setText(TAP_MEASURES * 4 + " times");

        // get the songs from setupActivity
        songs = (ArrayList<Song>) getIntent().getSerializableExtra(EXTRA_SONGS);
        addNew = getIntent().getBooleanExtra(EXTRA_ADD_NEW, true);

        findViewById(R.id.tapbutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v(TAG, "User has touched the button");
                counter.tap(System.currentTimeMillis());
                TextView tapCount = ((TextView)findViewById(R.id.textCount));

                // TODO: Make this a little cleaner, it's pretty iffy right now
                tapCount.setText(Integer.parseInt(tapCount.getText().toString().split(" ")[0])-1 + " times");

                //If this is the last tap for the song
                if (counter.isReady()) {
                    double bpm = counter.bpmEstimate();
                    Log.i(TAG, "Estimated BPM of song: " + bpm);

                    songs.get(counter.getCycleCount()-1).setBpm(bpm);

                    if (counter.getCycleCount() >= songs.size()) {
                        // we're all done with the songs!
                        (Toast.makeText(getApplicationContext(), "All done!", Toast.LENGTH_LONG)).show();
                        Log.i(TAG, "Finished all songs, returning to setup.");
                        finishSplash();
                        putToDb();
                        goToMain();
                    } else {
                        // throw up a toast: new song
                        (Toast.makeText(getApplicationContext(), "Next song!", Toast.LENGTH_LONG)).show();
                        ImageView background = ((ImageView) findViewById(R.id.tapBackground));
                        if (counter.getCycleCount() % 2 == 1) {
                            background.setImageResource(R.drawable.bpmtap2);
                        } else {
                            background.setImageResource(R.drawable.bpmtap);
                        }
                        ((TextView)findViewById(R.id.textCount)).setText(TAP_MEASURES * 4 + " times");
                        playSong(counter.getCycleCount());
                    }
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

    public void nextSplash() {
        Log.i(TAG, "Sending Intent to NextSplash.");
        Intent nextIntent = new Intent(this, NextSplash.class);
        startActivity(nextIntent);
    }

    public void finishSplash() {
        Log.i(TAG, "Sending Intent to FinishSplash.");
        Intent finishIntent = new Intent(this, FinishSplash.class);
        startActivity(finishIntent);
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
            playSong(counter.getCycleCount());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    public void playSong(int index){
        musicSrv.setSong(index);
        Song current = musicSrv.getCurrentSong();
        ((TextView) findViewById(R.id.textSongName)).setText(current.getTitle());
        ((TextView) findViewById(R.id.textArtistName)).setText(current.getArtist());
        musicSrv.playSong();
    }

    public void putToDb() {
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());
        if (addNew) {
            dbHelper.addSongs(songs);
        } else {
            dbHelper.updateSongs(songs);
        }

        Log.i(TAG, "Songs currently in the db: " + dbHelper.getSongs().toString());
    }

    public void goToMain(){
        Log.i(TAG, "Finishing Activity, sending Intent to FinishUploadSplash.");
        Intent lastSplash = new Intent(this, FinishUploadSplash.class);
        startActivity(lastSplash);
        musicSrv.stopSelf();
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
    public void seekTo(int pos) {}

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
