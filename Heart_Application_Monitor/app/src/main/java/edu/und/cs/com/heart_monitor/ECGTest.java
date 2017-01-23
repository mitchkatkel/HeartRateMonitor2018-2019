package edu.und.cs.com.heart_monitor;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.app.Dialog;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import android.content.res.AssetManager;
import android.content.DialogInterface;
import android.widget.Toast;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import java.io.BufferedInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import roboguice.activity.RoboActivity;

/**
 * Created by Jack Wolff on 1/15/2017.
 * Class used to test ECG readings without using the bitalino board.
 */

public class ECGTest extends RoboActivity implements View.OnClickListener{

    private GraphViewSeries signalValueSeries;
    private GraphView myGraphView;

    AsyncTask task;

    private int qrs[];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);

        //Changes x axis values of graph to seconds instead of frame number
        final java.text.DateFormat simpleDateFormatter = new SimpleDateFormat("mm:ss");
        signalValueSeries = new GraphViewSeries(new GraphViewData[] {});
        myGraphView = new LineGraphView(this, "Electrocardiograph");
        //Set graph options
        myGraphView.addSeries(signalValueSeries);
        LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
        myGraphView.setManualYAxisBounds(200, -200);
        graphLayout.addView(myGraphView);
        myGraphView.setScrollable(true);

        //Find the buttons by their ID
        final Button startButton = (Button) findViewById(R.id.startBTN);
        final Button stopButton = (Button) findViewById(R.id.stopBTN);
        final Button fileButton = (Button) findViewById(R.id.fileBTN);
        final Button backButton = (Button) findViewById(R.id.backBTN);

        //Listen for button presses
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        fileButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBTN:
                onStartButton();
                break;
            case R.id.stopBTN:
                onStopButton();
                break;
            case R.id.fileBTN:
                onFileButton();
                break;
            case R.id.backBTN:
                onBackButton();
                break;
        }
    }

    /**
     * Button to start the test was pressed.
     */
    private void onStartButton() {

    }

    /**
     * Button to stop the test was pressed.
     */
    private void onStopButton() {
        task.cancel(true);
    }

    /**
     * Button to chose a file was pressed.
     */
    private void onFileButton() {

        try {
            AssetManager mnger = getAssets();
            InputStream stream = mnger.open("samples/Sample1.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            float y;
            int sam = 10000;
            int[] list = new int[sam];
            String[] t = new String[sam];
            String[] line;
            for (int x = 0; x < sam; x++) {
                line = reader.readLine().split(",");
                list[x] = Integer.parseInt(line[1]);
                t[x] = line[0];
            }
            float[] pass = QRSDetection.highPass(list, sam);
            float[] low = QRSDetection.lowPass(pass, sam);
            qrs = QRSDetection.QRS(low, sam);
        }
        catch(Exception e) {
            Log.d("TAG", e.getMessage());
        }

        //Get the AssetManager and get all the sample files
        AssetManager mngr = getAssets();

        try {
            final String[] samples = mngr.list("samples");
            //Create a dialog to select a file to read from
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick Sample");
            builder.setItems(samples, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int index) {
                    //Close the dialog
                    dialog.cancel();
                    //Set the chosen file
                    task = new TestAsyncTask().execute(samples[index]);
                }
            });
            builder.show();
        }
        catch(Exception e) {
        }
    }


    /**
     * Button to go back was pressed.
     */
    private void onBackButton() {

    }

    private class TestAsyncTask extends AsyncTask<String, String, Void> {
        int x;
        float y;
        /**
         * Read from the file and update the graph.
         * @param params A single array containing the filename
         * @return null
         */
        protected Void doInBackground(String... params) {
            //Get the filename and open the file for parsing
            String fileName = params[0];
            AssetManager mnger = getAssets();
            BufferedReader reader = null;

            try {
                InputStream stream = mnger.open("samples/"+fileName);
                reader = new BufferedReader(new InputStreamReader(stream));
            }
            catch(Exception e) {
                task.cancel(true);
                return null;
            }

            boolean read = true;
            x = 0;
            while(read) {
                try {
                    String[] line = reader.readLine().split(",");
                    y = Float.parseFloat(line[1]);
                    x++;
                    publishProgress();
                    try {
                        Thread.sleep(25);
                        Log.d("WAIT", "Waiting...");
                    }
                    catch(Exception e) {

                    }
                }
                catch(Exception e) {
                    read = false;
                    break;
                }
            }


            task.cancel(true);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            GraphViewData data = new GraphViewData(x, y);

            signalValueSeries.appendData(data, false, 200);
            if(qrs[x] == 1)
                Log.d("QRS", x + " IS QRS POINT.");
            myGraphView.redrawAll();
        }
    }
}