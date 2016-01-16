package com.example.karenlee.app.sensoranalysis;

import android.util.Log;

import java.util.Arrays;

/**
 * A Class that, given the accelerometer matrix and time vector, can find the beats per minute.
 */
public class BpmBlackBox {

    static final String TAG = "BPM_BLACK_BOX";

    /** returns the BPM given a, t */
    public static double findBPM(double[][] a, double[] t) {
        double[] x = a[0];
        double[] y = a[1];
        double[] z = a[2];
        Log.d(TAG, Arrays.toString(x));
        Log.d(TAG, Arrays.toString(y));
        Log.d(TAG, Arrays.toString(z));
        double[] a_mag = new double[t.length];
        // Loop to find the overall acceleration magnitude of each sample
        for(int i=0; i<t.length; i++){
            a_mag[i] = Math.sqrt(x[i]*x[i] + y[i]*y[i] + z[i]*z[i]);
        }
        Log.d(TAG, Arrays.toString(a_mag));
        double a_mag_mean = mean(a_mag);

        Log.d(TAG, "" + a_mag_mean);

        // normalize by the mean magnitude (gravity + overall running acceleration)
        for(int i = 0; i < t.length; i++){
            a_mag[i] -= a_mag_mean;
        }
        Log.d(TAG, Arrays.toString(a_mag));

        PeakDetector pd = new PeakDetector(a_mag);
        int[] peakLocations = pd.process(15, standardDeviation(a_mag) * (2.0/3.0));

        // correlate the locations of the peaks to times.
        double[] peakTimes = new double [peakLocations.length];
        for (int i = 0; i < peakTimes.length; i++) {
            peakTimes[i] = t[peakLocations[i]];
        }

        // difference vectors
        double[] peakDiffs = new double[peakTimes.length-1];
        double diffSum = 0.0;
        for (int i = 0; i < peakTimes.length -1; i++) {
            peakDiffs[i] = peakTimes[i + 1] - peakTimes[i];
            diffSum += peakDiffs[i];
        }

        // average distance between maxima
        double avgPeakDistance = diffSum/peakDiffs.length;

        // extrapolate BPM from the average distance
        return 60.0/avgPeakDistance;
    }

    private static double standardDeviation(double[] x) {
        double temp = 0;
        double mean = mean(x);
        for(double a : x)
            temp += (mean-a)*(mean-a);
        return temp/x.length;
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
