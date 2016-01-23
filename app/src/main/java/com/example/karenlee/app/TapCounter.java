package com.example.karenlee.app;

import android.util.Log;

import java.util.ArrayList;

/**
 * A simple class to change how we deal with incoming taps to the BPMMusicFinderActivity screen.
 * Created by brycewilley on 1/21/16.
 */
public class TapCounter {

    static final String TAG = "TAP_COUNTER";

    // TODO: consider arrays if the ArrayList is too slow
    private ArrayList<Long> tapTimes = new ArrayList<>(); // the list of times when the user tapped
    private ArrayList<Long> tapDeltas = new ArrayList<>();
    private int cycleCount = 0; // the number of times this tap counter has cycled

    /**
     * The number of "measurements needed to be considered ready to estimate the bpm of the taps.
     * Right now, measurements in the raw number of taps known in the list, but it could change to
     * the valid number of delta's between taps
     */
    private final int goalMeasurements;

    /**
     * Standard Constructor.
     * @param measures The number of measures that the tapper should play at least once
     */
    public TapCounter(int measures) {
        goalMeasurements = measures * 4 - 1;
    }

    /**
     * Add a tap to the list.
     * @param time The time in milliseconds (from System.currentTimeMillis) that the user tapped
     */
    public void tap(long time) {
        if (!tapTimes.isEmpty()) {
            tapDeltas.add(time - tapTimes.get(tapTimes.size() - 1));
        }
        tapTimes.add(time);
    }

    /**
     * Returns true if the counter has enough taps to give an accurate measurement of the song
     */
    public boolean isReady() {
        return (tapDeltas.size() >= goalMeasurements);
    }

    /**
     * Returns the estimate of the beats per minute given the current tap times.
     * If this call is valid, restart the tap counter and increment the cycleCount.
     */
    public double bpmEstimate() {
        if (isReady()) {
            Log.i(TAG, "Variance before removal is " + variance(tapDeltas));
            removeOutliers();
            double avgDelta = mean(tapDeltas);
            Log.i(TAG, "Variance after removal is " + variance(tapDeltas));
            restart();
            return 60000.0 / avgDelta;
        } else {
            // there's an error, we aren't actually ready to get the estimate
            return -1;
        }
    }

    /**
     * Removes any tap deltas that are greater than 2 std devs away from the mean
     */
    private void removeOutliers() {
        double stdDev = Math.sqrt(variance(tapDeltas));
        double mean = mean(tapDeltas);
        ArrayList<Integer> outliers = new ArrayList<>();
        for (int i = 0; i < tapDeltas.size(); i++) {
            if (Math.abs(mean - tapDeltas.get(i)) > 2.1 * stdDev) {
                outliers.add(0, i);
            }
        }
        // remove those outliers
        for (int i : outliers) {
            tapDeltas.remove(i);
        }
    }

    private static double mean(ArrayList<Long> x) {
        long sum = 0;
        // loop to find the magnitude total sum, and therefore the mean
        for(long a : x){
            sum += a;
        }
        return sum/x.size();
    }

    private static long variance(ArrayList<Long> x) {
        long temp = 0;
        double mean = mean(x);
        for(long a : x)
            temp += (mean-a)*(mean-a);
        return temp/x.size();
    }

    /**
     * Returns the number of estimated bpms (or the number of cycles it has traced) that this object
     * has returned
     */
    public int getCycleCount() {
        return cycleCount;
    }

    /**
     * Starts the counter over again for a different estimate.
     */
    private void restart() {
        tapTimes = new ArrayList<>();
        tapDeltas = new ArrayList<>();
        cycleCount++;
    }
}
