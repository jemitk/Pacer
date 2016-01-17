package com.example.karenlee.app.sensoranalysis;

import android.util.Log;

import java.util.Arrays;

/**
 * A class that holds a specific snapshot of accelerometer (or any other 3D sensor data).
 * Used this to store sensor info and deliver it to the service/thread that finds the BPM.
 */
public class AccelSensorSnapshot {

    private static final String TAG = "ACCEL_SNAPSHOT_OBJECT";

    // TODO(Bryce): consider changing these to ArrayLists for easier management iff too many bugs
    private double[][] accelDataMatrix;
    private double[] timeDataVector;

    private int currentSample;
    private int max;

    public AccelSensorSnapshot(int sampleNum) {
        max = sampleNum;
        // seperated in case we want to just use this same object.
        init();
    }

    private void init() {
        accelDataMatrix = new double[3][max];
        timeDataVector = new double[max];
        currentSample = 0;
    }

    /**
     * Stores the given accelerometer sample in this data object, rejecting it if this object is
     * already filled.
     * @param time The system time in milliseconds (System.currentTimeMillis())
     *             already
     * @param x The accelerometer measurement in the x direction (horizontal axis of phone,
     *          positive right side)
     * @param y The accelerometer measurement in the y direction (vertical axis, positive up)
     * @param z The accelerometer measurement in the z direction (out of screen of the phone,
     *          positive out)
     */
    public void addSample(float time, float x, float y, float z) {
        addSample((double) time, (double) x, (double) y, (double) z);
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
    public void addSample(double time, double x, double y, double z) {
        if (currentSample < max) {
            // TODO(Bryce): add noise filtering, delta,  and magnitude measurements here.
            timeDataVector[currentSample] = time;
            accelDataMatrix[0][currentSample] = x;
            accelDataMatrix[1][currentSample] = y;
            accelDataMatrix[2][currentSample] = z;
            currentSample += 1;
        } else {
            Log.d(TAG, "Ran out of space! " + max + " is the max amount.");
        }
    }

    public double[] getTimeVec() {
        return timeDataVector;
    }

    public double[][] getAccelMatrix() {
        return accelDataMatrix;
    }

    public int getNumSamples() {
        return max;
    }

    /**
     * @return True if the accel matrix is equal to the max, false otherwise.
     */
    public boolean isFull() {
        return currentSample == max;
    }

    public void reset() {
        // just call init
        init();
    }

    /**
     * Returns the BPM from the current snapshot it has. Won't let you get the BPM until the
     * snapshot is filled (will return -1 instead).
     */
    public double findBPM() {
        if (isFull()) {
            double[] t = timeDataVector;
            double[][] a = accelDataMatrix;
            double[] x = a[0];
            double[] y = a[1];
            double[] z = a[2];
            double[] a_mag = new double[t.length];
            // Loop to find the overall acceleration magnitude of each sample
            for (int i = 0; i < t.length; i++) {
                a_mag[i] = Math.sqrt(x[i] * x[i] + y[i] * y[i] + z[i] * z[i]);
            }

            double a_mag_mean = mean(a_mag);

            Log.d(TAG, "Average magnitude: " + a_mag_mean);

            // normalize by the mean magnitude (gravity + overall running acceleration)
            for (int i = 0; i < t.length; i++) {
                a_mag[i] -= a_mag_mean;
            }

            Log.d(TAG, "Magnitude: " + Arrays.toString(Arrays.copyOfRange(a_mag, 0, 10)));

            PeakDetector pd = new PeakDetector(a_mag);
            int[] peakLocations = pd.process(8, 1);

            if (peakLocations.length == 0) {
                // we didn't find any peaks for some reason
                Log.w(TAG, "No peaks in the current snapshot.");
                return -1;
            }

            // correlate the locations of the peaks to times.
            double[] peakTimes = new double[peakLocations.length];
            for (int i = 0; i < peakTimes.length; i++) {
                peakTimes[i] = t[peakLocations[i]];
            }

            // difference vectors
            double[] peakDiffs = new double[peakTimes.length - 1];
            double diffSum = 0.0;
            for (int i = 0; i < peakTimes.length - 1; i++) {
                peakDiffs[i] = peakTimes[i + 1] - peakTimes[i];
                diffSum += peakDiffs[i];
            }

            // average distance between maxima
            double avgPeakDistance = diffSum / peakDiffs.length;

            // extrapolate BPM from the average distance
            return 60.0 / avgPeakDistance;
        } else {
            // not enough samples!
            Log.e(TAG, "Not enough samples to find the BPM. Current: " + (currentSample - 1) +
                    ", necessary: " + max);
            return -1;
        }
    }

    private static double standardDeviation(double[] x) {
        double temp = 0;
        double mean = mean(x);
        for(double a : x)
            temp += (mean-a)*(mean-a);
        return Math.sqrt(temp/x.length);
    }

    private static double mean(double[] x) {
        double sum = 0.0;
        // loop to find the magnitude total sum, and therefore the mean
        for(double a : x){
            sum += a;
        }
        return sum/x.length;
    }

}
