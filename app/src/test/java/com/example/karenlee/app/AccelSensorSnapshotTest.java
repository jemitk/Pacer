package com.example.karenlee.app;

import static org.junit.Assert.*;

import com.example.karenlee.app.sensoranalysis.AccelSensorSnapshot;
import com.example.karenlee.app.sensoranalysis.PeakDetector;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
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
        AccelSensorSnapshot ass = readInTestData();
        System.out.println(ass.findBPM());
    }

    @Test
    public void testFindPeakAdvanced() {
        double[] a = {-6.9308,-8.3614,-9.7609,-11.159,-12.122,-12.122,-13.227,-13.345,-12.722,-12.307,-12.499,-12.743,-12.656,-12.656,-12.06,-11.011,-9.4394,-7.5163,-5.9905,-4.6418,-4.6418,-3.8625,-3.7287,-3.9489,-3.8846,-3.4362,-3.5074,-4.3887,-4.3887,-4.9931,-3.6263,0.64822,6.7896,12.783,12.783,16.613,17.144,14.827,11.803,9.6788,8.9536,8.9536,9.4858,10.76,12.088,13.303,13.896,12.562,10.419,10.419,8.5702,6.951,5.7052,4.8113,3.9087,2.9868,2.9868,2.2704,1.9548,1.9013,2.0134,1.9947,1.837,1.837,1.7426,1.7561,1.4475,0.77958,-0.16367,-0.49734,-0.49734,0.30627,1.8388,3.7761,5.6599,6.8403,7.4127,7.4127,8.0267,9.2154,9.9706,10.253,10.156,9.5624,9.0004,9.0004,8.8998,8.708,7.796,5.9828,3.793,1.5786,1.5786,-0.28105,-2.9252,-5.0786,-6.9519,-8.1452,-8.903,-8.903,-9.4218,-9.7821,-9.4372,-8.2391,-6.8061,-5.5378,-5.5378,-4.2601,-3.0743,-2.0757,-1.5125,-1.0751,-0.7892,-1.0719,-0.93975,-1.3467,-2.2738,-2.7358,-2.2889,-1.8031,-2.7778,-2.7778,-4.9798,-6.9925,-7.3646,-5.1429,-0.38705,5.9688,5.9688,11.962,15.864,17.344,17.553,17.216,17.034,17.034,16.793,16.654,16.417,15.826,14.503,12.614,12.614,10.27,7.571,4.6951,2.3039,0.74303,-0.026201,-0.27046,-0.47637,-0.72472,-0.75528,-0.62348,-0.586,-0.72114,-0.97959,-0.97959,-1.2442,-1.1696,-0.57597,0.56037,1.9849,3.2267,3.2267,4.3501,5.4972,6.3819,7.6006,9.2637,11.262,11.262,12.799,13.518,13.536,13.323,13.184,12.543,12.543,10.658,7.0273,2.6396,-1.583,-4.7019,-6.4538,-7.27,-7.27,-7.9531,-8.5954,-9.5267,-10.654,-11.51,-11.387,-11.387,-10.136,-8.5582,-7.1553,-6.148,-5.4736,-4.894,-4.894,-3.9688,-2.8841,-1.9319,-1.3959,-1.8251,-2.9985,-2.9985,-4.1695,-4.839,-4.6625,-4.6265,-6.6747,-9.7455,-9.7455,-11.276,-8.0096,-0.64282,8.831,17.651,23.368,24.442,24.442,22.368,19.469,16.924,14.992,13.772,13.026,13.026,12.371,11.227,9.5073,7.3236,4.9921,2.8039,2.8039,0.85963,-0.72429,-2.0159,-2.9572,-3.5591,-4.0487,-4.0487,-4.4673,-4.7972,-5.0469,-5.2351,-5.3057,-5.2126,-5.2126,-4.5016,-3.4004,-2.227,-0.87898,0.7847,2.5455,4.4714,4.4714,6.3955,8.5395,10.476,11.312,10.794,9.6476,9.6476,8.906,9.1597,10.076,10.056,8.2763,4.8845,4.8845,0.78524,-3.2279,-6.5412,-8.7637,-10.103,-10.564,-10.564,-10.583,-10.429,-10.328,-10.463,-10.221,-9.4782,-9.4782,-8.4007,-7.4033,-6.7737,-6.2877,-5.6522,-5.0126,-4.1671,-4.2542,-3.6579,-3.6992,-4.6023,-6.0182,-7.2913,-7.891,-7.891,-7.9405,-8.7366,-10.409,-10.67,-7.6605,-2.0048,-2.0048,5.2665,13.395,19.972,22.385,20.638,17.579,17.579,14.87,13.108,12.218,11.746,11.141,10.17,10.17,8.7605,7.1338,5.3358,3.4254,1.4105,-0.47381,-1.8432,-1.935,-3.0204,-3.7389,-4.0244,-4.1127,-3.8924,-3.539,-3.539,-3.3293,-3.536,-4.0868,-4.6476,-4.8818,-4.4995,-4.4995,-3.4915,-1.7901,0.53602,2.7088,4.3816,5.8529,5.8529,6.5924,6.5068,6.4304,6.1738,6.1207,7.2533,7.2533,9.4941,11.216,10.892,8.1125,3.5214,-2.0957,-7.2436,-7.2436,-11.175,-13.653,-14.834,-15.226,-15.275,-14.898,-14.898,-14.198,-13.225,-12.136,-11.213,-10.365,-9.5285,-9.5285,-8.8645,-8.5569,-8.3794,-8.1789,-7.5982,-7.036,-7.036,-7.4173,-8.7442,-10.54,-11.999,-11.904,-11.504,-11.504,-11.836,-11.095,-8.4256,-4.0651,2.2529,9.3122,9.3122,15.213,17.819,16.47,13.643,11.223,9.6084,8.3899,8.3899,7.261,5.851,4.3575,2.8224,1.2832,-0.16756,-0.16756,-1.5505,-2.8169,-3.9569,-4.8877,-5.5015,-5.7875,-5.7875,-5.9638,-5.9706,-5.8753,-5.8143,-5.9542,-5.8733,-5.8733,-5.6531,-5.231,-4.5318,-3.3021,-1.463,0.29117,0.29117,1.6375,3.3677,5.6463,8.2172,10.792,12.737,13.655,13.655,13.308,11.829,9.9237,8.2286,6.8409,5.5271,5.5271,3.8849,1.0959,-2.5667,-5.4834,-7.7691,-9.5505,-9.5505,-11.286,-12.065,-11.89,-11.557,-10.977,-10.265,-10.265,-9.5091,-8.4986,-7.3899,-6.542,-6.2183,-6.3814,-6.3814,-6.4634,-6.3672,-6.1333,-6.0676,-6.619,-7.7162,-8.7701,-8.7701,-8.6742,-7.9329,-7.8612,-7.0774,-4.7622,-1.0978,-1.0978,4.5216,11.829,17.951,20.735,19.771,16.692,16.692,13.238,10.284,8.0423,6.2993,4.7408,3.2967,3.2967,1.9686,0.62632,-0.6662,-1.7443,-2.6986,-3.6129,-3.6129,-4.3693,-4.9433,-5.3613,-5.6311,-5.7012,-5.7009,-5.7519,-5.7519,-5.9132,-5.8077,-5.5397,-5.3138,-4.8556,-3.6867,-3.6867,-2.0082,-0.21917,1.5024,3.6188,5.8314,8.0995,8.0995,9.7729,10.723,11.159,10.941,10.368,9.5161,8.5034,8.5034,7.5841,6.1989,3.8349,0.9621,-2.0894,-2.0894,-5.0178,-7.6257,-9.6384,-10.668,-10.773,-10.383,-10.093,-10.093,-10.037,-9.7592,-9.0524,-8.1387,-7.173,-6.4131,-6.4131,-5.86,-5.4135,-4.7109,-4.0324,-3.9495,-4.7162,-4.7162,-5.9769,-7.2267,-8.0636,-8.8392,-9.8295,-9.6507,-9.6507,-6.8685,-1.8479,5.4756,13.536,19.487,21.077,21.077,18.646,14.262,10.063,6.9334,4.8914,3.986,3.8012,3.8012,3.6847,3.3625,2.7455,1.8567,0.8317,-0.37884,-0.37884,-1.6227,-2.7904,-3.8835,-4.8191,-5.4049,-5.6601,-5.6601,-5.7516,-5.7672,-5.7924,-5.7565,-5.4684,-4.9945,-4.9945,-4.2601,-3.0329,-1.7092,-0.33756,1.4598,4.0648,4.0648,7.0262,9.782,11.788,12.706,12.018,10.37,8.263,8.263,6.0656,4.2599,2.9072,1.738,0.21267,-2.1689,-2.1689,-5.2632,-8.3845,-11.02,-13.072,-14.298,-14.204,-13.15,-13.248,-12.098,-11.14,-10.298,-9.3412,-8.4388,-8.4388,-7.6879,-6.929,-6.334,-5.8697,-5.7928,-6.0712,-6.0712,-6.6992,-7.8115,-9.0641,-10.008,-10.154,-9.7218,-7.8279,-7.8279,-4.4876,-0.41481,4.2051,9.1736,12.989,14.631,14.631,14.049,11.974,9.3793,7.1999,5.6736,4.6042,4.6042,4.3185,4.4128,4.4815,4.4565,4.025,3.1629,3.1629,1.9412,0.64862,-0.7336,-1.8057,-2.6053,-2.9985,-2.9214,-2.8782,-2.5886,-2.6139,-3.0433,-3.6774,-4.2615,-4.6815,-4.6815,-4.7448,-4.1453,-2.7881,-1.2287,0.23627,1.3902,1.3902,2.398,3.4716,4.6505,5.9057,6.8817,7.1943,7.1943,6.9851,6.7256,6.387,5.5569,3.8321,1.24,1.24,-1.8726,-4.6919,-6.7986,-8.2328,-9.1733,-9.7764,-10.16,-10.228,-10.588,-11.035,-11.524,-11.672,-11.32,-10.372,-10.372,-9.3383,-8.4337,-7.7068,-6.9535,-6.2392,-5.6393,-5.6393,-5.2446,-5.4326,-6.281,-7.5727,-8.8409,-9.7679,-9.7679,-10.838,-11.273,-9.7276,-6.5539,-1.3852,5.5428,5.5428,12.111,16.091,16.388,13.938,10.918,8.1921,6.174,6.1531,4.8776,4.1361,3.8393,3.6018,3.286,2.6368,2.6368,1.6608,0.4422,-0.87019,-2.0963,-3.1476,-4.0671,-4.0671,-4.7544,-5.1254,-5.4566,-5.5823,-5.6252,-5.716,-5.716,-5.7817,-5.8351,-5.7562,-5.5484,-5.1889,-4.3316,-4.3316,-2.8591,-0.96981,1.2123,3.8037,6.4037,8.559,10.025,10.025,11.104,11.942,12.247,12.262,11.874,11.068,11.068,9.7619,7.6459,4.7791,1.6215,-1.3217,-3.943,-3.943,-6.1471,-8.0111,-9.5473,-10.867,-11.626,-11.315,-11.315,-10.277,-8.9728,-7.7517,-7.0046,-6.59,-6.2849,-6.2849,-6.041,-5.6012,-5.1761,-4.8736,-5.1467,-6.236,-7.595,-7.595,-8.4294,-7.951,-7.1082,-8.1279,-10.442,-11.185,-11.185,-9.0294,-2.9801,5.8337,14.263,19.697,20.39,20.39,17.32,13.157,9.9703,8.1671,7.5967,7.8752,7.8752,8.1926,7.9121,7.3777,6.621,5.5313,4.2399,4.2399,2.7021,1.0078,-0.81231,-2.4545,-3.6347,-4.3032,-4.6892,-4.6892,-4.9342,-5.2895,-5.7015,-6.0226,-6.3257,-6.4746,-6.4746,-6.513,-6.2948,-5.2515,-3.5637,-1.3263,1.3145,1.3145,4.297,7.5727,10.459,11.983,11.486,9.6604,9.6604,7.4781,5.5198,4.3464,3.794,3.3881,2.9872,2.9872,1.7626,-0.52666,-3.0586,-5.3546,-7.5527,-9.3011,-10.228,-10.228,-10.133,-9.6934,-9.0841,-8.1827,-7.0712,-5.8678,-4.8958,-4.8958,-4.3335,-4.1765,-4.192,-4.1743,-4.1668,-4.1668,-4.3058,-4.8519,-5.7773,-6.6235,-7.3078,-8.3438,-8.3438,-9.0559,-8.0315,-4.5984,0.78716,7.0388,12.807,12.807,16.757,17.554,15.75,12.951,10.291,8.3147,7.1885,7.1885,7.0077,7.2293,7.2655,6.615,5.3636,3.9498,3.9498,2.5466,1.2889,0.11269,-1.0619,-2.1885,-3.2022,-3.2022,-4.0845,-4.8039,-5.2747,-5.6414,-5.9145,-6.1145,-6.1145,-6.2385,-6.1673,-6.0082,-5.3876,-4.1536,-2.8862,-2.8862,-1.6752,0.6301,3.7315,7.5737,11.667,14.636,15.804,15.804,15.527,13.767,11.27,8.6863,6.5098,5.0184,5.0184,4.066,2.7858,0.64376,-2.1698,-5.1646,-7.696,-7.696,-9.2358,-9.7487,-9.574,-8.9911,-8.366,-7.7409,-7.7409,-7.2513,-7.0062,-6.7017,-6.2401,-5.7383,-5.4151,-5.313,-5.4764,-5.978,-6.901,-8.1554,-9.182,-9.2203,-8.6923,-8.6923,-8.7955,-9.1749,-8.4895,-6.6341,-2.8694,3.7046,3.7046,10.921,15.422,15.405,12.318,8.7588,6.1247,6.1247,4.3006,3.4828,3.7182,4.3949,4.9874,4.9665,4.9665,4.3853,3.5499,2.5478,1.478,0.40569,-0.53302,-1.4377,-1.4377,-2.3025,-2.9852,-3.4426,-3.4969,-3.5658,-3.493,-3.493,-3.4908,-3.6367,-3.9007,-4.2062,-4.1898,-3.8582,-3.8582,-2.8918,-1.3986,0.56266,2.7537,5.6671,9.0001,9.0001,12.003,13.928,14.74,14.602,13.763,11.812,11.812,8.9114,5.6586,2.7526,0.66253,-0.67913,-1.6969,-3.088,-3.088,-4.7221,-6.3919,-7.8126,-8.7907,-9.5048,-9.7764,-9.7764,-9.6966,-9.3821,-8.853,-8.425,-8.215,-8.1268,-8.1268,-7.9974,-7.847,-7.7475,-7.6485,-7.6404,-8.0428,-8.0428,-8.7895,-9.6198,-10.31,-10.793,-10.446,-9.748,-9.748,-10.258,-8.139,-0.48946,8.1414,14.042,15.544,14.029,14.029,11.811,10.087,9.0556,8.6983,8.7753,8.558,8.558,7.8046,6.7299,5.596,4.3757,3.0095,1.5657,1.5657,0.15713,-1.217,-2.2542,-2.9333,-3.1673,-3.1041,-3.1041,-2.9646,-2.8247,-2.7446,-2.797,-2.6802,-2.642,-2.7623,-2.7623,-2.9207,-3.2039,-3.3115,-3.0693,-2.2183,-0.54115,-0.54115,1.6721,4.7421,8.915,13.593,17.079,18.346,18.346,18.185,17.443,16.121,14.142,12.128,10.212,10.212,8.4097,6.9936,5.5739,3.5156,0.61347,-2.3108,-2.3108,-4.4652,-5.6315,-5.866,-5.7775,-5.6062,-5.1803,-4.2631,-4.2631,-3.3152,-2.6765,-2.3423,-2.1553,-2.0868,-2.0826,-2.0826,-2.3972,-3.0193
        };
        PeakDetector p = new PeakDetector(a);
        p.process(3,1);
        System.out.println(Arrays.toString(p.process(8, 1)));
        System.out.println(p.process(8,1).length);
    }

    public AccelSensorSnapshot readInTestData() {
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        AccelSensorSnapshot sensorSnapshot = null;
        ArrayList<Double[]> list = new ArrayList<>();
        ArrayList<Double> time = new ArrayList<>();
        File aFile = new File("a.csv");
        File tFile = new File("t.csv");

        try {

            br = new BufferedReader(new FileReader(aFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                Double[] array = new Double[4];
                Arrays.asList(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])).toArray(array);
                list.add(array);
            }
            sensorSnapshot = new AccelSensorSnapshot(list.size());

            br = new BufferedReader(new FileReader(tFile));
            while ((line = br.readLine()) != null) {
                time.add(Double.parseDouble(line));
            }

            for (int i = 0; i < list.size(); i++) {
                sensorSnapshot.addSample(time.get(i), list.get(i)[0], list.get(i)[1], list.get(i)[2]);
            }

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
        return sensorSnapshot;
    }

}
