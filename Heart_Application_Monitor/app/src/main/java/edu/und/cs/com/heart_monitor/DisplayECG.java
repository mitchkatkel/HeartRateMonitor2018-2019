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
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class DisplayECG extends ActionBarActivity implements View.OnClickListener {

    Button btnBack, btnSave, btnDelete;
    TextView txtFileName;
    TextView txtWarningMessage;

    Boolean fileSaved = false;
    FileHelper myFileHelper;
    ArrayList<Integer> myFileInfo;

    GraphView myGraphView;
    LineGraphSeries dataValueSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ecg);

        myFileHelper = new FileHelper();

        dataValueSeries = new LineGraphSeries();
        myGraphView = new GraphView(this);
        myGraphView = (GraphView) findViewById(R.id.graphLayout);
        //Set graph options
        myGraphView.addSeries(dataValueSeries);
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


        btnSave = (Button) findViewById(R.id.storeBTN);
        btnDelete = (Button) findViewById(R.id.deleteBTN);
        btnBack = (Button) findViewById(R.id.backBTN);
        txtFileName = (TextView) findViewById(R.id.txtFileName);
        txtWarningMessage = (TextView) findViewById(R.id.txtWarningMessages);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Intent oldIntent = getIntent();
        Bundle myBundle = oldIntent.getExtras();
        txtWarningMessage.setText("Warning! You are dying"); //TODO messages passed in myBundle extracted and put here, if no message leave as empty string
        String fileName = myBundle.getString("fileName");
        txtFileName.setText("File: " + fileName);
        myFileInfo = myFileHelper.loadFile(fileName, this);
        if (myFileInfo != null) {
            for (int i = 0; i < myFileInfo.size(); i++) {
                dataValueSeries.appendData(new DataPoint(i, myFileInfo.get(i)), false, myFileInfo.size() + 1);
            }
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
                if (!fileSaved) {
                    Intent oldIntent = getIntent();
                    Bundle myBundle = oldIntent.getExtras();
                    String fileName = myBundle.getString("fileName");
                    if (myFileHelper.deleteFile(fileName, getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "File Deleted", Toast.LENGTH_LONG).show();
                    }
                }
                startActivity(new Intent(DisplayECG.this, MainActivity.class));
                break;
            case R.id.storeBTN:
                fileSaved = true;
                break;
            case R.id.deleteBTN:
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
                                    startActivity(new Intent(DisplayECG.this, MainActivity.class));
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