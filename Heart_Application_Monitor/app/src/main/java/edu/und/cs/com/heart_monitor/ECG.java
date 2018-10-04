package edu.und.cs.com.heart_monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoFrame;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.UUID;

import roboguice.activity.RoboActivity;

import static android.widget.Toast.makeText;

public class ECG extends RoboActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean UPLOAD = false;
    private LineGraphSeries signalValueSeries;
    private GraphView myGraphView;
    boolean runTest = true;
    boolean testFailed = false;
    boolean connectionFailure = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean testInitiated = false;
    private AsyncTask myThread;
    private boolean keepFile;
    private int RSSI;                                           //bluetooth signal strength TODO ANDREW
    public FileHelper myFileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg);

        keepFile = false;                                                               //flag used to indicate whether or not file should be kept

        //let user know it will take a few seconds to start getting readings
        makeText(this, "Establishing Connection to Sensor", Toast.LENGTH_LONG).show();

        //Changes cur_x axis values of graph to seconds instead of frame number
        final java.text.DateFormat simpleDateFormatter = new SimpleDateFormat("mm:ss");
        signalValueSeries = new LineGraphSeries();
        myGraphView =new GraphView(this);
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
        myGraphView = (GraphView)findViewById(R.id.graphLayout);
        //Set graph options
        myGraphView.addSeries(signalValueSeries);
        //Set graph options
        myGraphView.getViewport().setXAxisBoundsManual(true);
        myGraphView.getViewport().setYAxisBoundsManual(true);
        myGraphView.getViewport().setMinX(0);
        myGraphView.getViewport().setMaxX(200);
        myGraphView.getViewport().setMinY(200);
        myGraphView.getViewport().setMaxY(900);
        //myGraphView.setScrollable(true);
        //myGraphView.setShowHorizontalLabels(false);                                   //remove cur_x axis labels
        myFileHelper = new FileHelper();
        myFileHelper.startFile(myFileHelper, getApplicationContext());

        //Find the buttons by their ID
        final Button startButton = (Button) findViewById(R.id.startBTN);
        final Button quitButton = (Button) findViewById(R.id.quitBTN);
        final Button backButton = (Button) findViewById(R.id.backBTN);
        final Button storeButton = (Button) findViewById(R.id.storeBTN);

        //Listen for button presses
        startButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        storeButton.setOnClickListener(this);

        // execute
        if (!testInitiated) {
            myThread = new TestAsyncTask().execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBTN:
                runTest = false;                                                                                    //ensure current test terminates correctly
                myFileHelper.closeFile(myFileHelper, getApplicationContext());                                       //close file output stream
                if(!keepFile) getApplicationContext().deleteFile(myFileHelper.fileName);
                startActivity(new Intent(ECG.this, ECG.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.quitBTN:
                runTest = false;
                myFileHelper.closeFile(myFileHelper, getApplicationContext());
                break;
            case R.id.backBTN:
                runTest = false;
                myFileHelper.closeFile(myFileHelper, getApplicationContext());
                if(!keepFile) getApplicationContext().deleteFile(myFileHelper.fileName);
                android.os.Process.killProcess(android.os.Process.myPid());
                startActivity(new Intent(ECG.this, MainActivity.class));
                break;
            case R.id.storeBTN:
                if((runTest)&&(!testFailed)&&(testInitiated)){                              //test is still running
                    Toast.makeText(this, "Stop test first!", Toast.LENGTH_LONG).show();
                }else if((!runTest)&&(!testFailed)&&(testInitiated)){                       //test has run successfully, has stopped, can store file
                    Toast.makeText(this, "File " + myFileHelper.fileName + " created", Toast.LENGTH_LONG).show();
                    keepFile = true;
                    myFileHelper.closeFile(myFileHelper, getApplicationContext());
                }else if((!runTest)&&(testFailed)&&(!testInitiated)){                       //test is not running and an error of some kind occurred
                    Toast.makeText(this, "No recording made", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class TestAsyncTask extends AsyncTask<Void, String, Void> {

        private BluetoothDevice dev = null;
        private BluetoothSocket sock = null;
        private BITalinoDevice bitalino;
        int currentValue = 0;
        int currentFrameNumber = 0;
        SharedPreferences getPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String macAddress = getPreference.getString("macAddress",null );
        private final int ecgChannel = 1;
        private final int sampleRate = 8;

        protected Void doInBackground(Void... paramses) {
            try {
                // Get the remote Bluetooth device
                final String remoteDevice = macAddress;
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                dev = btAdapter.getRemoteDevice(remoteDevice);
                //establish bluetooth connection
                sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
                try {
                    sock.connect();
                    testInitiated = true;
                    bitalino = new BITalinoDevice(1000, new int[]{ecgChannel});
                    bitalino.open(sock.getInputStream(), sock.getOutputStream());
                    // start acquisition on predefined analog channels
                    bitalino.start();
                    // read until task is stopped
                    int counter = 0;
                    //long startTime = System.currentTimeMillis();
                    while (runTest) {
                        BITalinoFrame[] frames = bitalino.read(sampleRate);//read(number of frames to read)
                        if (UPLOAD) {
                            // prepare reading for upload
                            BITalinoReading reading = new BITalinoReading();
                            reading.setTimestamp(System.currentTimeMillis());
                            reading.setFrames(frames);
                        }
                        // go into frames to gather data from sensors
                        for (BITalinoFrame frame : frames) {
                            //analog 2 == ECG
                            currentValue = frame.getAnalog(ecgChannel);
                        }
                        currentFrameNumber = counter;
                        //output results to screen using onProgressUpdate()
                        publishProgress(Integer.toString(currentValue), Integer.toString(counter));
                        counter++;
                    }
                }catch(Exception e){              //error opening socket
                    connectionFailure = true;     //flag that connection has failed
                    runTest = false;              //flag that the test has not run
                    testFailed = true;            //flag indicates the test has failed
                    publishProgress();
                }
            } catch (Exception e) { //error connecting to bluetooth
                runTest = false;
                testFailed = true;
                Log.e(TAG, "There was an error connecting to phones bluetooth.", e);
            }
            onCancelled();                          //close input and output streams and close socket
            myThread.cancel(true);                  //terminate thread
            return null;
        }
        /*
        *       onProgress update allows the asynctask to send data gathered to User interface
         */
        @Override
        protected void onProgressUpdate(String... values) {
            //If the connection has failed, show a message
            if(connectionFailure) {
                Toast.makeText(getApplicationContext(),"Unable to establish connection", Toast.LENGTH_LONG).show();
            }else {
                //TODO do I want false signalValueSeries.appendData(new GraphViewData(currentFrameNumber, currentValue), false, 200);
                signalValueSeries.appendData(new DataPoint(currentFrameNumber, currentValue), true,200);
                //update graph with new data value "appendData((cur_x value, fileY value), notsure?, max number of points on graph)"
                myFileHelper.appendFile(myFileHelper,currentFrameNumber / sampleRate, currentValue, getApplicationContext());
            }
        }

        @Override
        protected void onCancelled() {
            // stop acquisition and close bluetooth connection
            try {
                bitalino.stop();         //signal board to quit sending packets
                //InputStream is = null;      //close input and output streams
                //OutputStream os = null;
                sock.close();               //close socket on this end
            } catch (Exception e) {
                Log.e(TAG, "There was an error.", e);
            }
        }

   }

}