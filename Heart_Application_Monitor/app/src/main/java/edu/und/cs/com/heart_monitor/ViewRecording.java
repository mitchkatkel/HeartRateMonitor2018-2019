package edu.und.cs.com.heart_monitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewRecording extends ActionBarActivity implements View.OnClickListener {

    Button btnBack, btnEmail, btnDelete;
    LinearLayout lytGraphLayout;
    TextView txtFileName;
    FileHelper myFileHelper;
    ArrayList<Integer> myFileInfo;

    GraphView myGraphView;
    LineGraphSeries dataValueSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recording);

        myFileHelper = new FileHelper();

        /*dataValueSeries = new GraphViewSeries(new GraphView.GraphViewData[] {});
        myGraphView = new LineGraphView(this, "Electrocardiograph"){};
        lytGraphLayout = (LinearLayout) findViewById(R.id.graphLayoutTwo);
        myGraphView.addSeries(dataValueSeries);
        myGraphView.setManualYAxisBounds(900, 200);
        lytGraphLayout.addView(myGraphView);
        myGraphView.setViewPort(0,25);
        myGraphView.setScrollable(true);*/

        //Changes cur_x axis values of graph to seconds instead of frame number
        final java.text.DateFormat simpleDateFormatter = new SimpleDateFormat("mm:ss");
        dataValueSeries = new LineGraphSeries();
        myGraphView = new GraphView(this);
        //TODO need to investigate what this chunk was doing further
        /*signalValueSeries = new GraphViewSeries(new GraphViewData[] {});
        myGraphView = new GraphView(this, "Electrocardiograph"){

            @Override
            protected String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // convert unix time to human time
                    return simpleDateFormatter.format(new Date((long) value*65));
                } else return super.formatLabel(value, isValueX);                       // let the fileY-value be normal-formatted
            }
        };*/
        myGraphView = (GraphView) findViewById(R.id.graphLayouttwo);
        //Set graph options
        myGraphView.addSeries(dataValueSeries);
        //Set graph options
        myGraphView.getViewport().setXAxisBoundsManual(true);
        myGraphView.getViewport().setYAxisBoundsManual(true);
        myGraphView.getViewport().setMinX(0);
        myGraphView.getViewport().setMaxX(100);
        myGraphView.getViewport().setMaxY(1000);
        myGraphView.getViewport().setMinY(100);
        myGraphView.getViewport().setScrollable(true);
        myGraphView.getViewport().setScrollableY(true);
        myGraphView.getViewport().setScalable(true);
        //myGraphView.getViewport().setScalableY(true);


        btnEmail = (Button) findViewById(R.id.emailBTN);
        btnDelete = (Button) findViewById(R.id.deleteBTN);
        btnBack = (Button) findViewById(R.id.backBTN);
        //lytGraphLayout = (LinearLayout) findViewById(R.id.graphLayoutTwo);
        txtFileName = (TextView) findViewById(R.id.txtFileName);

        btnEmail.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Intent oldIntent = getIntent();
        Bundle myBundle = oldIntent.getExtras();
        String fileName = myBundle.getString("fileName");
        txtFileName.setText("File: " + fileName);
        myFileInfo = myFileHelper.loadFile(fileName, this);
        for (int i = 0; i < myFileInfo.size(); i++) {
            dataValueSeries.appendData(new DataPoint(i, myFileInfo.get(i)), false, myFileInfo.size() + 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_recording, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBTN:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.storeBTN:
                Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show();
                break;
            case R.id.deleteBTN:
                //TODO Yes/No Dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent oldIntent = getIntent();
                                Bundle myBundle = oldIntent.getExtras();
                                String fileName = myBundle.getString("fileName");
                                if (myFileHelper.deleteFile(fileName, getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), "File Deleted", Toast.LENGTH_LONG).show();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to delete file", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete this file?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
        }
    }
}