package edu.und.cs.com.heart_monitor;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoFrame;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import edu.und.cs.com.heart_monitor.R;
import retrofit.RestAdapter;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;

import static android.widget.Toast.makeText;

public class ECG extends RoboActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean UPLOAD = false;
    private GraphViewSeries signalValueSeries;
    private GraphView myGraphView;
    boolean runTest = true;
    boolean testFailed = false;
    boolean connectionFailure = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean testInitiated = false;
    private AsyncTask myThread;
    private int RSSI; //bluetooth signal strength
    private String ecgReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg);
        //let user know it will take a few seconds to start getting readings
        makeText(this, "Establishing Connection to Sensor", Toast.LENGTH_LONG).show();
        //final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(this);
        final java.text.DateFormat simpleDateFormatter = new SimpleDateFormat("mm:ss");
        signalValueSeries = new GraphViewSeries(new GraphViewData[] {});
        myGraphView = new LineGraphView(this, "Electrocardiograph Values"){

            @Override
            protected String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // convert unix time to human time
                    return simpleDateFormatter.format(new Date((long) value*65));
                } else return super.formatLabel(value, isValueX); // let the y-value be normal-formatted
            }


        };
        myGraphView.addSeries(signalValueSeries);
        LinearLayout graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
        //myGraphView.setVerticalLabels(new String[] {"315","307","300"});
        myGraphView.setManualYAxisBounds(900, 200);
        graphLayout.addView(myGraphView);
        myGraphView.setScrollable(true);
        //myGraphView.setShowHorizontalLabels(false);//remove x axis labels

        final Button startButton = (Button) findViewById(R.id.startBTN);
        final Button quitButton = (Button) findViewById(R.id.quitBTN);
        final Button backButton = (Button) findViewById(R.id.backBTN);
        final Button storeButton = (Button) findViewById(R.id.storeBTN);

        startButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        storeButton.setOnClickListener(this);

        // execute
        if (!testInitiated) {
            myThread = new TestAsyncTask().execute();
            //testInitiated = true;           //signal test has started
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBTN:
                runTest = false;              //ensure current test terminates correctly
                startActivity(new Intent(ECG.this, ECG.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.quitBTN:
                runTest = false;
                break;
            case R.id.backBTN:
                runTest = false;
                android.os.Process.killProcess(android.os.Process.myPid());
                startActivity(new Intent(ECG.this, MainActivity.class));
                break;
            case R.id.storeBTN:
                if((runTest == true)&&(testFailed == false)&&(testInitiated == true)){
                    Toast.makeText(this, "Stop test first!", Toast.LENGTH_LONG).show();
                    // toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                    // toast.show();
                }else if((runTest == false)&&(testFailed == false)&&(testInitiated == true)){
                    Toast.makeText(this, "Making new recording", Toast.LENGTH_LONG).show();
                    FileHelper myhelper = new FileHelper();
                    myhelper.writeFile(ecgReading, getApplicationContext());
                    //toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
                    //toast.show();
                }else if((runTest == false)&&(testFailed == true)&&(testInitiated == false)){
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
        private InputStream is = null;
        private OutputStream os = null;
        private BITalinoDevice bitalino;
        public int currentValue = 0;
        public int currentFrameNumber = 0;


        private int numSamples = 0;

        @Override
        protected Void doInBackground(Void... paramses) {
            try {
                // Let's get the remote Bluetooth device
                final String remoteDevice = "98:D3:31:B2:BD:8D";
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                dev = btAdapter.getRemoteDevice(remoteDevice);

    /*
     * Establish Bluetooth connection
     *
     * Because discovery is a heavyweight procedure for the Bluetooth adapter,
     * this method should always be called before attempting to connect to a
     * remote device with connect(). Discovery is not managed by the Activity,
     * but is run as a system service, so an application should always call
     * cancel discovery even if it did not directly request a discovery, just to
     * be sure. If Bluetooth state is not STATE_ON, this API will return false.
     *
     * see
     * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
     * .html#cancelDiscovery()
     */
                btAdapter.cancelDiscovery();

                sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
                try {
                    sock.connect();
                    testInitiated = true;

                    bitalino = new BITalinoDevice(1000, new int[]{0, 1, 2, 3, 4, 5});

                    bitalino.open(sock.getInputStream(), sock.getOutputStream());

                    // start acquisition on predefined analog channels
                    bitalino.start();

                    // read until task is stopped
                    int counter = 0;
                    //long startTime = System.currentTimeMillis();
                    while (runTest) {
                        //final int numberOfSamplesToRead = 100;

                        BITalinoFrame[] frames = bitalino.read(8);//read(number of frames to read)

                        if (UPLOAD) {
                            // prepare reading for upload
                            BITalinoReading reading = new BITalinoReading();
                            reading.setTimestamp(System.currentTimeMillis());
                            reading.setFrames(frames);
                        }

                        // present data in screen
                        for (BITalinoFrame frame : frames)
                            //analog 3 == accelerometer
                            //analog 2 == ECG
                            currentValue = frame.getAnalog(3);
                        currentFrameNumber = counter;
                        //output results to screen using onProgressUpdate()
                        publishProgress(Integer.toString(currentValue), Integer.toString(counter));
                        counter++;
                    }

                    // trigger digital outputs
                    // int[] digital = { 1, 1, 1, 1 };
                    // device.trigger(digital);
                }catch(Exception e){    //error opening socket
                    connectionFailure = true;
                    runTest = false;
                    testFailed = true;
                    publishProgress();
                }
            } catch (Exception e) { //error connecting to bluetooth
                runTest = false;
                testFailed = true;
                Log.e(TAG, "There was an error connecting to phones bluetooth.", e);
            }
            onCancelled();
            myThread.cancel(true);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(connectionFailure == true) {
                Toast.makeText(getApplicationContext(),"Unable to establish connection", Toast.LENGTH_LONG).show();
            }else {
                signalValueSeries.appendData(new GraphViewData(currentFrameNumber / 8, currentValue), false, 200);
                ecgReading += (currentFrameNumber / 8) + "," + currentValue + "\n";
                //update graph with new data value "appendData((x value, y value), notsure?, max number of points on graph)"
                myGraphView.redrawAll();
            }

        }

        @Override
        protected void onCancelled() {
            // stop acquisition and close bluetooth connection
            try {
                // bitalino.stop();         //signal board to quit sending packets
                InputStream is = null;   //close input and output streams
                OutputStream os = null;
                //  bitalino.stop();         //signal board to quit sending packets
                sock.close();            //close socket on this end
            } catch (Exception e) {
                Log.e(TAG, "There was an error.", e);
            }
        }

    }

}