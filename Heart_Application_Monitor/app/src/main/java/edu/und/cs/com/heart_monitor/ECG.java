package edu.und.cs.com.heart_monitor;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
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

import java.util.UUID;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import roboguice.activity.RoboActivity;

import static android.widget.Toast.makeText;

public class ECG extends RoboActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final boolean UPLOAD = false;
    boolean runTest = false;
    boolean testFailed = false;
    boolean connectionFailure = false;
    boolean isCanceled = false;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean testInitiated = false;
    private AsyncTask myThread;
    public FileHelper myFileHelper;
    private BluetoothSocket sock = null;
    private BITalinoDevice bitalino;
    private GifImageView loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ecg);

        //let user know it will take a few seconds to start getting readings
        Toast.makeText(this, "Press start to begin ECG", Toast.LENGTH_LONG).show();

        myFileHelper = new FileHelper();

        //Find the buttons by their ID
        final Button startButton = (Button) findViewById(R.id.startBTN);
        final Button quitButton = (Button) findViewById(R.id.quitBTN);
        final Button cancelButton = (Button) findViewById(R.id.cancelBTN);
        loadingImage = (GifImageView) findViewById(R.id.heart_gif);

        ((GifDrawable) loadingImage.getDrawable()).stop();

        //Listen for button presses
        startButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBTN:
                if (!runTest && !testInitiated) {
                    //let user know it will take a few seconds to start getting readings
                    makeText(this, "Establishing Connection to Sensor", Toast.LENGTH_LONG).show();
                    runTest = true;
                    myFileHelper.startFile(myFileHelper, getApplicationContext(), false);
                    myThread = new TestAsyncTask().execute();
                }
                break;
            case R.id.quitBTN:
                if ((runTest) && (!testFailed) && (testInitiated)) {                       //test has run successfully, has stopped, can store file
                    isCanceled = true;
                    try {
                        bitalino.stop();         //signal board to quit sending packets
                        sock.close();               //close socket on this end
                    } catch (Exception e) {
                        Log.e(TAG, "There was an error.", e);
                    }
                    myThread.cancel(true);
                    Toast.makeText(this, "File " + myFileHelper.fileName + " created", Toast.LENGTH_LONG).show();
                    myFileHelper.closeFile(myFileHelper, getApplicationContext());
                    Bundle myBundle = new Bundle();                                                 //Bundle fileName to send to AnalyzeECG Activity
                    myBundle.putString("fileName", myFileHelper.fileName);
                    Intent newIntent = new Intent(getApplicationContext(), AnalyzeECG.class);
                    newIntent.putExtras(myBundle);
                    startActivity(newIntent);
                } else if ((!runTest) && (testFailed) && (!testInitiated)) {                       //test is not running and an error of some kind occurred
                    isCanceled = true;
                    Toast.makeText(this, "No recording made", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cancelBTN:
                isCanceled = true;
                runTest = false;
                if (myThread != null) {
                    try {
                        bitalino.stop();         //signal board to quit sending packets
                        sock.close();               //close socket on this end
                    } catch (Exception e) {
                        Log.e(TAG, "There was an error.", e);
                    }
                    myThread.cancel(true);
                    myFileHelper.closeFile(myFileHelper, getApplicationContext());
                    getApplicationContext().deleteFile(myFileHelper.fileName);
                }
                startActivity(new Intent(ECG.this, MainActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void handleConnectionFailure() {
        if (!isCanceled) {
            myThread.cancel(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(ECG.this);
            builder.setMessage("Error Connecting! Check your connection in User Settings.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (myFileHelper.fileName != null) {
                                myFileHelper.closeFile(myFileHelper, getApplicationContext());
                                getApplicationContext().deleteFile(myFileHelper.fileName);
                            }
                            startActivity(new Intent(ECG.this, MainActivity.class));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private class TestAsyncTask extends AsyncTask<Void, String, Void> {

        private BluetoothDevice dev = null;
        int currentValue = 0;
        int currentFrameNumber = 0;
        SharedPreferences getPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String macAddress = getPreference.getString("macAddress", null);
        private final int ecgChannel = 1;
        private final int sampleNumberToGet = 1000;

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
                    ((GifDrawable) loadingImage.getDrawable()).start();
                    // read until task is stopped
                    currentFrameNumber = 0;
                    while (runTest) {
                        BITalinoFrame[] frames = bitalino.read(sampleNumberToGet);   //read(number of frames to read)
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

                            //output results to screen using onProgressUpdate()
                            publishProgress(Integer.toString(currentValue), Integer.toString(currentFrameNumber));
                            currentFrameNumber++;
                        }
                    }
                } catch (Exception e) {              //error opening socket
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
            if (connectionFailure) {
                handleConnectionFailure();
                //Toast.makeText(getApplicationContext(), "Unable to establish connection", Toast.LENGTH_LONG).show();
            } else {
                myFileHelper.appendFile(myFileHelper, Integer.parseInt(values[1]) / sampleNumberToGet, Integer.parseInt(values[0]), getApplicationContext());
            }
        }
    }
}