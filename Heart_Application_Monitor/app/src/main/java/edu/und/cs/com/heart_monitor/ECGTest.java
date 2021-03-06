package edu.und.cs.com.heart_monitor;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);

        fileSeries = new LineGraphSeries();
        myGraphView = new GraphView(this);
        myGraphView = (GraphView) findViewById(R.id.graph);
        //Set graph options
        myGraphView.addSeries(fileSeries);
        //Set graph options
        myGraphView.getViewport().setXAxisBoundsManual(true);
        myGraphView.getViewport().setYAxisBoundsManual(true);
        myGraphView.getViewport().setMinX(0);
        myGraphView.getViewport().setMaxX(2000);
        myGraphView.getViewport().setMaxY(1000);
        myGraphView.getViewport().setMinY(100);
        myGraphView.getViewport().setScrollable(true);
        myGraphView.getViewport().setScrollableY(true);
        myGraphView.getViewport().setScalable(true);

        //Find the buttons by their ID
        final Button backButton = (Button) findViewById(R.id.backBTN);

        //Listen for button presses
        backButton.setOnClickListener(this);

        Intent oldIntent = getIntent();
        Bundle myBundle = oldIntent.getExtras();
        String fileName = myBundle.getString("fileName");
        AssetManager mngr = getAssets();
        BufferedReader reader;
        try {
            InputStream stream = mngr.open("samples/"+fileName);
            reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            String[] lineArr;
            int point;
            int cur = 1;
            while ((line = reader.readLine())!= null) {
                lineArr = line.split(",");
                point = Integer.parseInt(lineArr[1]);
                fileSeries.appendData(new DataPoint(cur, point), false, 600000);

                cur++;
            }
        }
        catch(Exception e) {
            return;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBTN:
                onBackButton();
                break;
        }
    }

    /**
     * Button to go back was pressed.
     */
    private void onBackButton() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}