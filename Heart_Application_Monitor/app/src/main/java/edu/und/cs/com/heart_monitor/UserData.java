package edu.und.cs.com.heart_monitor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class UserData extends ActionBarActivity implements View.OnClickListener {

   String name;
    String phone;
    String email;
    String eName;
    String ePhone;
    String eEmail;
Button results;
    TextView r;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        dbHelper = new DBHelper(this);

       results = (Button)findViewById(R.id.btnResult);
       results.setOnClickListener(this);
        r = (TextView)findViewById(R.id.txtResult);

        Intent myIntent = getIntent();
        Bundle dataBundle=myIntent.getExtras();
        name = dataBundle.getString("name");
       phone = dataBundle.getString("phone");
        email = dataBundle.getString("email");
        eName = dataBundle.getString("Emerg_name");
        ePhone = dataBundle.getString("Emerg_phone");
        eEmail = dataBundle.getString("Emerg_email");
        myIntent.putExtras(dataBundle);
        r.setText("you entered: " + name +" " +phone+ " " + email+ ".");
        setResult(Activity.RESULT_OK, myIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_data, menu);
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
        finish();
    }
}

