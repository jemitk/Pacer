package com.example.karenlee.app;

import static org.junit.Assert.*;

import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        ass = new AccelSensorSnapshot(3);
        double[][] accel = {{0, 4., 0., 4.}, {0, -3, 0, -3}, {0, 5, 0, 5}};
        double[] t = {0., 1_000., 2_000., 3_000.};

        for (int i = 0; i < 3; i++) {
            ass.addSample(t[i], accel[0][i], accel[1][i], accel[2][i]);
        }

        assertEquals(60, ass.findBPM(), 5);
    }

    @Test
    public void testFindBpmAdvanced() {
        throw new RuntimeException("Not implemented!");
    }


    public void run() {

        String csvFile = "location/of/file.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        AccelSensorSnapshot sensorSnapshot;
        ArrayList<double[]> list = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                double[] array = new double[3];
                Arrays.asList(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])).toArray(array);
                list.add(array);
            }
            sensorSnapshot = new AccelSensorSnapshot(list.size());
            for (double[] array : list) {
                sensorSnapshot.addSample(array[0], array[1], array[2]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }


}
