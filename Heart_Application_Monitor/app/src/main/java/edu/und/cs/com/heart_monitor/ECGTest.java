package edu.und.cs.com.heart_monitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import roboguice.activity.RoboActivity;

/**
 * Created by Jack Wolff on 1/15/2017.
 * Class used to test ECG readings without using the bitalino board.
 */

public class ECGTest extends RoboActivity implements View.OnClickListener {

    //Series that has been through the high and low pass filters
    //private LineGraphSeries highPassFilterSeries;
    //private LineGraphSeries lowPassFilterSeries;
    //Series that reads directly from the file
    private LineGraphSeries fileSeries;
    private GraphView myGraphView;

    AsyncTask task;
    private boolean isAsyncTaskCancelled = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);

        //highPassFilterSeries = new LineGraphSeries();
        //lowPassFilterSeries = new LineGraphSeries();
        fileSeries = new LineGraphSeries();
        fileSeries.setColor(Color.RED);
        //lowPassFilterSeries.setColor(Color.GREEN);
        myGraphView = (GraphView)findViewById(R.id.graph);
        //myGraphView.addSeries(highPassFilterSeries);
        myGraphView.addSeries(fileSeries);
        //myGraphView.addSeries(lowPassFilterSeries);
        //Set graph options
        myGraphView.getViewport().setXAxisBoundsManual(true);
        myGraphView.getViewport().setYAxisBoundsManual(true);
        myGraphView.getViewport().setMinX(100);
        myGraphView.getViewport().setMaxX(5000);
        myGraphView.getViewport().setMinY(-100);
        myGraphView.getViewport().setMaxY(150);

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
        isAsyncTaskCancelled = true;
    }

    /**
     * Button to chose a file was pressed.
     */
    private void onFileButton() {

        try {
            AssetManager mnger = getAssets();
            InputStream stream = mnger.open("samples/Sample1-Filtered.txt");
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
            Log.d("TAG", e.getMessage());
        }
    }


    /**
     * Button to go back was pressed.
     */
    private void onBackButton() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            isAsyncTaskCancelled = true;
            getFragmentManager().popBackStack();
        }
        else {
            isAsyncTaskCancelled = true;
            super.onBackPressed();
        }
    }

    private class TestAsyncTask extends AsyncTask<String, String, Void> {
        //Current cur_x value in the graph
        int cur_x;

        private final int sample = 500;

        private int[] qrs;
        private float[] highFilter;
        private float[] lowFilter;
        private int[] file;

        BufferedReader reader;
        /**
         * Read from the file and update the graph.
         * @param params A single array containing the filename
         * @return null
         */
        protected Void doInBackground(String... params) {
            //Get the filename and open the file for parsing
            String fileName = params[0];
            AssetManager mnger = getAssets();
            //Start at 0
            cur_x = 0;
            try {
                InputStream stream = mnger.open("samples/"+fileName);
                reader = new BufferedReader(new InputStreamReader(stream));
            }
            catch(Exception e) {
                task.cancel(true);
                return null;
            }
            readFromFile();

            boolean read = true;

            while(read) {
                try {
                    //If this task has been cancelled, stop immediately
                    if(isAsyncTaskCancelled){break;}
                    //Plot the points
                    publishProgress();
                    if (cur_x%50 == 0) {
                        try {
                            Thread.sleep(0, 1);
                            //Log.d("WAIT", "Waiting...");
                        } catch (Exception e) {
                            //Log.d("TAG", e.getMessage());
                        }
                    }
                    cur_x++;
                    if(cur_x % sample == 0)
                        readFromFile();
                }
                catch(Exception e) {
                    read = false;
                    break;
                }
            }

            task.cancel(true);
            return null;
        }

        private void readFromFile() {
            file = new int[sample];
            String[] line;
            for (int x = 0; x < sample; x++) {
                try {
                    line = reader.readLine().split(",");
                    file[x] = Integer.parseInt(line[1]);
                }
                catch(Exception e) {
                    Log.d("ECGTest", e.getMessage());
                }
            }

            //highFilter = QRSDetection.highPass(file, sample);
            //lowFilter = QRSDetection.lowPass(highFilter, sample);
            //qrs = QRSDetection.QRS(lowFilter, sample);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //DataPoint from the file
            DataPoint fileDataPoint = new DataPoint(cur_x, file[cur_x % sample]);
            //DataPoint ecgDetectPoint = new DataPoint(cur_x, highFilter[cur_x % sample]);
            //DataPoint lowFilterPoint = new DataPoint(cur_x, lowFilter[cur_x % sample]);
            fileSeries.appendData(fileDataPoint, true, 5000);
            //highPassFilterSeries.appendData(ecgDetectPoint, true, 200);
            //lowPassFilterSeries.appendData(lowFilterPoint, true, 200);
            /*if(qrs[cur_x % sample] == 1) {
                PointsGraphSeries<DataPoint> point = new PointsGraphSeries<>(
                        new DataPoint[] {
                                new DataPoint(cur_x, file[cur_x % sample])
                        });
                myGraphView.addSeries(point);
            }*/
        }
    }
}