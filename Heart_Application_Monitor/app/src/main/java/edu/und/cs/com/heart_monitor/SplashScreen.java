package edu.und.cs.com.heart_monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.widget.Toast;


public class SplashScreen extends ActionBarActivity {
    static final int TIMER = 3000;
    final String file_settings = "preferences";
    SharedPreferences mySettings;
    SharedPreferences.Editor myEditor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mySettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String mac_address = mySettings.getString("macAddress",null);
        //if((mySettings.getBoolean("first_time",true)) && (mySettings.getString("macAddress",null)==null )){
         if(mac_address == null){
            myEditor = mySettings.edit();
            myEditor.putBoolean("first_time",false);
            myEditor.commit();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this,PrefUser.class);
                    startActivity(i);
                    finish();
                }
            },TIMER);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, TIMER);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
}
