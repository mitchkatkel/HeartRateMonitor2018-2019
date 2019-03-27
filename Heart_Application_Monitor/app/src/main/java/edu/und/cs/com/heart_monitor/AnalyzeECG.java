package edu.und.cs.com.heart_monitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import roboguice.activity.RoboActivity;

public class AnalyzeECG extends RoboActivity implements View.OnClickListener {


    private static final String TAG = "AnalyzingECG";
    private String fileName;
    private String newFileName;
    private AsyncTask myThread;
    private FileHelper myFileHelperIn;
    private FileHelper myFileHelperOut;


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

        myFileHelperIn = new FileHelper();
        myFileHelperOut = new FileHelper();

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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //run filter

            //assign the new file and delete the temp file
            newFileName = myFileHelperOut.fileName;
            newFileName = fileName; //TODO temp until filter added and new file is created

            //check for issues

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //TODO toast showing done with analyzing
            Bundle myBundle = new Bundle();                                                 //Bundle fileName to send to ViewRecording Activity
            myBundle.putString("fileName", newFileName);
            Intent newIntent = new Intent(getApplicationContext(), DisplayECG.class);
            newIntent.putExtras(myBundle);
            startActivity(newIntent);
        }

    }

}
