package com.example.karenlee.app;

import java.util.ArrayList;

/**
 * A simple class to change how we deal with incoming taps to the BPMMusicFinderActivity screen.
 * Created by brycewilley on 1/21/16.
 */
public class TapCounter {

    // TODO: consider arrays if the ArrayList is too slow
    private ArrayList<Long> tapTimes = new ArrayList<>(); //
    private int cycleCount = 0; // the number of times this tap counter has cycled
    private final int goalMeasurements;

    /**
     * Standard Constructor.
     * @param measurements The number of measurements (taps) needed
     */
    public TapCounter(int measurements) {
        goalMeasurements = measurements;
    }

    public void tap(long time) {
        tapTimes.add(time);
    }

    public boolean isReady() {
        return (tapTimes.size() >= goalMeasurements);
    }

    /**
     * Returns the estimate of the beats per minute given the current tap times.
     * If this call is valid, restart the tap counter and increment the cycleCount.
     * @return
     */
    public double bpmEstimate() {
        if (isReady()) {
            long timeSpan = tapTimes.get(tapTimes.size()-1) - tapTimes.get(0);
            restart();
            return 600000.0 / timeSpan;
        } else {
            // there's an error, we aren't actually ready to get the estimate
            return -1;
        }
    }

    public int getCycleCount() {
        return cycleCount;
    }

    private void restart() {
        tapTimes = new ArrayList<>();
        cycleCount++;
    }
}
