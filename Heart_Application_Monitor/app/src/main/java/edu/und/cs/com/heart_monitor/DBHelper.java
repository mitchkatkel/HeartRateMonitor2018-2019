package edu.und.cs.com.heart_monitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by owner on 2/16/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Heart_Users.db";
    UserData user_info;


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

    public void insert_user(UserData user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Name",user.name);
        contentValues.put("Phone",user.phone);
        contentValues.put("Email",user.email);
        contentValues.put("EmergencyName",user.eName);
        contentValues.put("EmergencyPhone",user.ePhone);
        contentValues.put("EmergencyEmail",user.eEmail);
        db.insert("USERS",null,contentValues);

    }

    public Cursor getContact(){
        SQLiteDatabase db =getReadableDatabase();
        Cursor ret = db.rawQuery("select * from USERS where id=1",null);
        return ret;
    }

    public void update_contact(UserData user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",user.name);
        contentValues.put("Phone",user.phone);
        contentValues.put("Email",user.email);
        contentValues.put("EmergencyName",user.eName);
        contentValues.put("EmergencyPhone",user.ePhone);
        contentValues.put("EmergencyEmail",user.eEmail);
        db.update("USERS", contentValues,"Id=1",null);
    }
}
