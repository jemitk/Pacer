package com.example.karenlee.app;

import android.util.Log;

/**
 * A class that holds a specific snapshot of accelerometer (or any other 3D sensor data).
 * Used this to store sensor info and deliver it to the service/thread that finds the BPM.
 */
public class AccelSensorSnapshot {

    private static final String TAG = "ACCEL_SNAPSHOT_OBJECT";

    // TODO(Bryce): consider changing these to ArrayLists for easier management iff too many bugs
    private float[][] accelDataMatrix;
    private float[] timeDataVector;

    private int currentSample;
    private int max;

    public AccelSensorSnapshot(int sampleNum) {
        max = sampleNum;
        // seperated in case we want to just use this same object.
        init();
    }

    private void init() {
        accelDataMatrix = new float[max][3];
        timeDataVector = new float[max];
        currentSample = 0;
    }

    /**
     * Stores the given accelerometer sample in this data object, rejecting it if this object is
     * already filled.
     * @param time The system time in milliseconds (System.currentTimeMillis())
     *             TODO(Bryce): adjust this to the time in location 0 if there is something there
     *             already
     * @param x The accelerometer measurement in the x direction (horizontal axis of phone,
     *          positive right side)
     * @param y The accelerometer measurement in the y direction (vertical axis, positive up)
     * @param z The accelerometer measurement in the z direction (out of screen of the phone,
     *          positive out)
     */
    public void addSample(float time, float x, float y, float z) {
        if (currentSample < max) {
            // TODO(Bryce): add noise filtering, delta,  and magnitude measurements here.
            timeDataVector[currentSample] = time;
            accelDataMatrix[currentSample][0] = x;
            accelDataMatrix[currentSample][1] = y;
            accelDataMatrix[currentSample][2] = z;
        } else {
            Log.d(TAG, "Ran out of space! " + max + " is the max amount.");
        }
    }

    public float[] getTimeVec() {
        return timeDataVector;
    }

    public float[][] getAccelMatrix() {
        return accelDataMatrix;
    }

    public int getNumSamples() {
        return max;
    }

}
