package com.example.karenlee.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;

public class RunActivity extends AppCompatActivity {


    /**
     * The amount of samples to hold in a snapshot.
     */
    private static final int SAMPLE_NUM = 2000;

    /**
     * The manager that you register all of the sensor objects with, handles their updates.
     */
    private SensorManager mSensorManager;

    /**
     * The aggregator for the bpm guesses.
     */
    private AccelSensorSnapshot snapshot = new AccelSensorSnapshot(SAMPLE_NUM);

    /**
     * The time that this process started.
     */
    private long start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        start = System.currentTimeMillis();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // registers given listener with the accelerometer, gets samples every 1,000 us, or 1 ms.
        mSensorManager.registerListener(new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                if (snapshot.isFull()) {
                    TextView text = (TextView) findViewById(R.id.textView);
                    text.setText("BPM: " + snapshot.findBPM());
                    snapshot.reset();
                    start = System.currentTimeMillis();
                } else {
                    // add another sample to the snapshot!
                    float axisX = event.values[0];
                    float axisY = event.values[1];
                    float axisZ = event.values[2];

                    snapshot.addSample((double) (System.currentTimeMillis() - start)/1000.0, axisX, axisY, axisZ);
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // blank for now
                // TODO(Bryce): find out what this method should actually do.
            }
        }, accelSensor, 10_000);

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

    public void restartBPMCount(View view) {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Calculating");
        snapshot.reset();
        start = System.currentTimeMillis();
    }
}
