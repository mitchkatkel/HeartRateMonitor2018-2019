package edu.und.cs.com.heart_monitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import roboguice.activity.RoboActivity;

public class AnalyzeECG extends RoboActivity implements View.OnClickListener {


    private static final String TAG = "AnalyzingECG";
    private String fileName;
    private String newFileName;
    private AsyncTask myThread;
    private QRSFilter filter;
    private AnomalyDetection detection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_ecg);

        //Find the buttons by their ID
        final Button cancelButton = (Button) findViewById(R.id.cancelBTN);

        cancelButton.setOnClickListener(this);

        Intent oldIntent = getIntent();
        Bundle oldBundle = oldIntent.getExtras();
        fileName = oldBundle.getString("fileName");

        filter = new QRSFilter();
        detection = new AnomalyDetection();

        myThread = new TestAsyncTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelBTN:
                myThread.cancel(true);
                startActivity(new Intent(AnalyzeECG.this, MainActivity.class));
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

        protected Void doInBackground(Void... paramses) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //run filter
            //assign the new file and delete the temp file
            //TODO filtering currently crashes, additionally is cancels out a lot of the QRS complex so it is debatable if it is what is actually needed
            //newFileName = filter.runQRSFIlter(myFileHelperOut.fileName, getApplicationContext());

            //TODO temp until filter system works
            newFileName = fileName;

            //check for issues
            //TODO skeleton file for anomaly detection
            //detection.Detect(newFileName);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(AnalyzeECG.this, "Finished analyzing. Redirecting you.", Toast.LENGTH_LONG).show();
            Bundle myBundle = new Bundle();                                                 //Bundle fileName to send to ViewRecording Activity
            myBundle.putString("fileName", newFileName);
            Intent newIntent = new Intent(getApplicationContext(), DisplayECG.class);
            newIntent.putExtras(myBundle);
            startActivity(newIntent);
        }

    }

}
