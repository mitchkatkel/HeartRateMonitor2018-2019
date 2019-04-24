package edu.und.cs.com.heart_monitor;

import android.content.Context;

import java.util.ArrayList;

public class QRSFilter {

    ArrayList<Integer> myFileInfoIn;
    FileHelper myFileHelperIn;
    FileHelper myFileHelperOut;

    public String runQRSFIlter(String fileName, Context context) {
        int[] values = filterFile(fileName, context);
        float[][] values2 = lowFrequency(values, values.length);
        float[] values3 = highFrequency(values2, values2.length);
        return writeFile(values3, fileName, context);
    }

    private int[] filterFile(String fileName, Context context) {
        myFileHelperIn = new FileHelper();
        myFileInfoIn = myFileHelperIn.loadFile(fileName, context);

        int[] inputData = new int[myFileInfoIn.size()];
        for (int i = 0; i < myFileInfoIn.size(); i++) {
            inputData[i] = myFileInfoIn.get(i);
        }

        return inputData;
    }

    private float[][] lowFrequency(int[] index, int size) {
        float[][] lowF = new float[size][2];
        float max = 0;
        float min = 0;
        float tmax = 0;
        float tmin = 0;
        int delta = 2;
        int omega = 2;

        for (int i = 0; i < index.length; i++) {
            if (i == 0) {
                max = index[i];
                min = index[i];
                tmax = index[i];
                tmin = index[i];
            } else {
                if (index[i] > tmax)
                    max = (tmax + (delta * omega));
                else
                    max = tmax - delta;

                if (index[i] < tmin)
                    min = (tmin - (delta * omega));
                else
                    min = tmin + delta;
            }

            lowF[i][0] = index[i] - ((max + min) / 2);
            lowF[i][1] = max - min;

            tmax = max;
            tmin = min;
        }

        return lowF;
    }

    private float[] highFrequency(float[][] low, int size) {
        float[] highF = new float[size];

        for (int i = 0; i < low.length; i++) {
            if (low[i][1] > Math.abs(low[i][0]))
                highF[i] = 0;
            else
                highF[i] = low[i][0] * ((Math.abs(low[i][0])) - low[i][1]);
        }

        return highF;
    }

    private String writeFile(float[] data, String name, Context context) {
        String[] parts = name.split("unfiltered");
        name = parts[0] + ".csv";

        myFileHelperOut = new FileHelper();
        myFileHelperOut.startFilteredFile(myFileHelperOut, context, name);

        for (int i = 0; i < data.length; i++) {
            myFileHelperOut.appendFile(myFileHelperOut, i, (int) data[i], context);
        }
        myFileHelperOut.closeFile(myFileHelperOut, context);
        return name;
    }
}
