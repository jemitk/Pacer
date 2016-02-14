package com.example.karenlee.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karenlee.app.db.SongBPMDbHelper;
import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;
import com.example.karenlee.app.songlists.ISongListGenerator;
import com.example.karenlee.app.songlists.SimpleSongListGenerator;

import java.util.ArrayList;

public class RunActivity extends AppCompatActivity implements SensorEventListener, MediaController.MediaPlayerControl {

    static final String TAG = "RUN_ACTIVITY";

    static final String EXTRA_LIST_GENERATOR = "com.example.karenlee.app.EXTRA_LIST_GENERATOR";

    /*******************
     * Sensor Variables.
     ******************/

    /** The amount of samples to hold in a snapshot. */
    private static final int SAMPLE_NUM = 1_000;

    /** The aggregator for the bpm guesses. */
    private AccelSensorSnapshot snapshot = new AccelSensorSnapshot(SAMPLE_NUM);

    /** The manager of the sensors. */
    private SensorManager mSensorManager;

    /** The time that this process started. */
    private long start;

    /** Once we have a whole block of samples to get the BPM from, keep that guess up there. */
    private boolean goldenGuess = false;

    /************************
     * Music Player variables
     ************************/

    private ArrayList<Song> dynamicSongList = new ArrayList<>();

    private MusicService musicSrv;

    private ISongListGenerator slGen = new SimpleSongListGenerator();

    private boolean musicBound=false;

    /** Use this to access the database. */
    private SongBPMDbHelper mDbHelper;

    private Intent playIntent;

    //connect to the service
    private ServiceConnection musicConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        mDbHelper = new SongBPMDbHelper(getApplicationContext());

        slGen = (ISongListGenerator) getIntent().getSerializableExtra(EXTRA_LIST_GENERATOR);

        musicConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                //get service
                musicSrv = binder.getService();
                Log.i(TAG, "Music service has been set:" + musicSrv);
                //pass list
                musicSrv.setList(dynamicSongList);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };

        start = System.currentTimeMillis();

        /* The manager that you register all of the sensor objects with, handles their updates. */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success! There's a accelerometer.
            Sensor accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // registers given listener with the accelerometer, gets samples every 1,000 us, or 1 ms.
            mSensorManager.registerListener(this, accelSensor, 10_000);
        }
        else {
            // Failure! No pressure sensor.
            Log.e(TAG, "No accelerometer sensors!");
            ((TextView) findViewById(R.id.textView)).setText("We're sorry, your phone or hardware " +
                    "doesn't contain the proper support for this application.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (snapshot.isFull()) {
            if (!goldenGuess) {
                goldenGuess = true;
                // play the song
                (new Runnable(){
                    @Override
                    public void run() {
                        double bpmExact = snapshot.findBPM();
                        final int bpmGoal = (int) Math.round(bpmExact);
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView) findViewById(R.id.textView)).setText("" + bpmGoal);
                                    }
                                }
                        );
                        try {
                            ArrayList<Song> songList = slGen.fillList(musicSrv, mDbHelper, bpmExact);
                            String songs = "";
                            for (Song s : songList) {
                                songs += s.toString();
                            }
                            Log.v(TAG, "Song list generated by the generator is : " + songs);
                            musicSrv.playSong();
                        } catch (IllegalStateException ex) {
                            Log.e(TAG, "Database was empty, no closest song to the bpm.", ex);
                        }
                    }
                }).run();
            }
            snapshot.reset();
            start = System.currentTimeMillis();
        } else {
            // add another sample to the snapshot!
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            snapshot.addSample((double) (System.currentTimeMillis() - start) / 1_000.0, axisX, axisY, axisZ);

            if (!goldenGuess) {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                double bpm = snapshot.findBPM();
                                if (bpm > 0)
                                    ((TextView) findViewById(R.id.textView)).setText("~" + (int) Math.round(snapshot.findBPM()));
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // The only sensor we can about.
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // The current sensor's state.
            String state = "";
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    state = "high accuracy";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    state = "medium accuracy";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    state = "low accuracy";
                    break;
                case SensorManager.SENSOR_STATUS_NO_CONTACT:
                    state = "cannot contact!";
                    Log.w(TAG, "Cannot contact sensor!");
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    state = "unreliable!";
                    Log.w(TAG, "Sensor is unreliable!");
                    break;
            }
            Log.d(TAG, "Accelerometer's status is now " + state);
        } else {
            Log.v(TAG, "Sensor that changed is " + sensor.getName());
        }
    }

    private void setController() {
        // set the controller up
        // we make a helper function since we need to set it up more than once in the life cycle of the app
        MusicController controller = new MusicController(this);

        // click listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext(findViewById(android.R.id.content));
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev(findViewById(android.R.id.content));
            }
        });

        controller.setMediaPlayer(this);
        // controller.setAnchorView(findViewById(R.id.));
        controller.setEnabled(true);
    }

    public void restartBPMCount(View view) {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Calculating");
        goldenGuess = false;
        snapshot.reset();
        start = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "Resetting the bpm count...", Toast.LENGTH_SHORT).show();
    }

    public void playNext(View view) {
        musicSrv.playNext();
    }

    public void playPrev(View view) {
        musicSrv.playPrev();
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
