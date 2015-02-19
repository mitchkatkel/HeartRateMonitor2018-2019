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

class DBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Heart.db";

    private static final String CREATE_IT = "" +
            "CREATE TABLE IF NOT EXISTS USERS" +
            "(Id integer PRIMARY KEY, Name text, Phone text,Email text, EmergencyName text," +
            "EmergencyPhone text, EmergencyEmail text" +
            ");";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        onCreate(db);
    }

    public void insert_user(String name,String phone, String email, String emergency_phone,String emergency_email,String emergency_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Name",name);
        contentValues.put("Phone",phone);
        contentValues.put("Email",email);
        contentValues.put("EmergencyName",emergency_name);
        contentValues.put("EmergencyPhone",emergency_phone);
        contentValues.put("EmergencyEmail",emergency_email);
        db.insert("USERS",null,contentValues);

    }
}
