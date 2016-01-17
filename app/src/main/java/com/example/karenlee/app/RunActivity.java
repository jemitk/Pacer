package com.example.karenlee.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;

public class RunActivity extends AppCompatActivity implements SensorEventListener{

    static final String TAG = "RUN_ACTIVITY";

    /** The amount of samples to hold in a snapshot. */
    private static final int SAMPLE_NUM = 2_000;

    /** The aggregator for the bpm guesses. */
    private AccelSensorSnapshot snapshot = new AccelSensorSnapshot(SAMPLE_NUM);

    /** The manager of the sensors. */
    private SensorManager mSensorManager;

    /** The time that this process started. */
    private long start;

    /** Once we have a whole block of samples to get the BPM from, keep that guess up there. */
    private boolean goldenGuess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

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
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (snapshot.isFull()) {
            goldenGuess = true;
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.textView)).setText("Final BPM: " +
                                    (int) Math.round(snapshot.findBPM()));
                        }
                    }
            );
            snapshot.reset();
            start = System.currentTimeMillis();
        } else {
            // add another sample to the snapshot!
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            snapshot.addSample((double) (System.currentTimeMillis() - start) / 1_000.0, axisX, axisY, axisZ);

            // Randomly decide whether or not to try to
            if (!goldenGuess) {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                double bpm = snapshot.findBPM();
                                if (bpm > 0)
                                    ((TextView) findViewById(R.id.textView)).setText("Potentially "
                                            + (int) Math.round(snapshot.findBPM()));
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

    public void restartBPMCount(View view) {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Calculating");
        goldenGuess = false;
        snapshot.reset();
        start = System.currentTimeMillis();
    }
}
