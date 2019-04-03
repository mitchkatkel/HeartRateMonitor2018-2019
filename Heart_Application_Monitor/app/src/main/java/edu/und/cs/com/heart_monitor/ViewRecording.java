package edu.und.cs.com.heart_monitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.util.ArrayList;

public class ViewRecording extends ActionBarActivity implements View.OnClickListener {

    Button btnBack, btnShare, btnDelete;
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

        dataValueSeries = new LineGraphSeries();
        myGraphView = new GraphView(this);
        myGraphView = (GraphView) findViewById(R.id.graphLayouttwo);
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


        btnShare = (Button) findViewById(R.id.shareBTN);
        btnDelete = (Button) findViewById(R.id.deleteBTN);
        btnBack = (Button) findViewById(R.id.backBTN);
        txtFileName = (TextView) findViewById(R.id.txtFileName);

        btnShare.setOnClickListener(this);
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
            case R.id.shareBTN:
                Intent oldIntent = getIntent();
                Bundle myBundle = oldIntent.getExtras();

                String fileName = myBundle.getString("fileName");
                File F = new File(this.getFilesDir(), fileName);
                Uri U = FileProvider.getUriForFile(this, "com.package.name.fileprovider", F);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(U, "text/csv");
                intent.putExtra(Intent.EXTRA_STREAM, U);
                startActivity(Intent.createChooser(intent, "Choose sharing method"));

                Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show();
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

    public void shareText(View view) {
        String FILE = Environment.getExternalStorageDirectory() + File.separator
                + "Foldername";

        Intent oldIntent = getIntent();
        Bundle myBundle = oldIntent.getExtras();
        String fileName = myBundle.getString("fileName");

        String temp_path = FILE + "/" + "Filename.csv";
        File F = new File(temp_path);
        Uri U = Uri.fromFile(F);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, fileName);
        intent.putExtra(Intent.EXTRA_STREAM, U);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }
}