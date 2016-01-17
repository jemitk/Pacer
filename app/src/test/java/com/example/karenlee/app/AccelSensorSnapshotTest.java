package com.example.karenlee.app;

import static org.junit.Assert.*;

import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;
import com.example.karenlee.app.sensoranalysis.PeakDetector;

import org.junit.Test;

/**
 * Unit tests for the AccelSensorSnapshot object.
 */
public class AccelSensorSnapshotTest {

    private static final int NUM_SAMPLES = 10;

    private AccelSensorSnapshot ass = new AccelSensorSnapshot(NUM_SAMPLES);

    @Test
    public void testLoadDoubles() {
        ass.addSample(0.0, 1, 1, 1);
        for (double i = 0.1; i < 2; i+=.1) {
            ass.addSample(i, 0, 0, 0);
        }
        assertEquals(ass.getAccelMatrix()[0][0], 1, 0.0);
        // for x, y, z
        assertEquals(ass.getAccelMatrix().length, 3);
        assertEquals(ass.getAccelMatrix()[0].length, NUM_SAMPLES);
    }

    @Test
    public void testLoadFloats() {
        ass.addSample((float) .1, (float) 1, (float) 1, (float) 1);
        for (float i = (float) 0.1; i < 2; i+=.1) {
            ass.addSample(i, 0, 0, 0);
        }
        assertEquals(ass.getAccelMatrix()[0][0], (float) 1, 0.0);
        // for x, y, z
        assertEquals(ass.getAccelMatrix().length, 3);
        assertEquals(ass.getAccelMatrix()[0].length, NUM_SAMPLES);
    }

    @Test
    public void testLoadElementsFew() {
        throw new RuntimeException("Not implemented!");
    }

    @Test
    public void testOverflowResilient() {
        throw new RuntimeException("Not implemented!");
    }

    @Test
    public void testInitToZeros() {
        assertEquals(ass.getTimeVec()[0], 0.0, 0.0);
        assertEquals(ass.getTimeVec()[9], 0.0, 0.0);
    }

    @Test
    public void testCrashOnReversingInputOrder() {
        throw new RuntimeException("Not implemented!");
    }

    @Test
    public void testFindBpmSimple() {
        throw new RuntimeException("Not implemented!");
    }

    @Test
    public void testFindBpmAdvanced() {
        double[] a = {4,2,1,3,2,2};
        PeakDetector p = new PeakDetector(a);
        p.process(3,1);
        System.out.println(p.process(3,1));

    }


}
