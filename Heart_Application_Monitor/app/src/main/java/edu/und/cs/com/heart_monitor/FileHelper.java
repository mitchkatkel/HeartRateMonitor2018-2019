package edu.und.cs.com.heart_monitor;

import android.content.Context;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Andrew on 2/19/2015.
 */
public class FileHelper {


    public int writeFile(String contents, Context ctx) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String fileName = (month + "_" + day + "_" + hour + "_" + min + "_" + sec);
        try {
            FileOutputStream fOut = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fOut.write(contents.getBytes());
            fOut.close();
            Toast.makeText(ctx, "File " + fileName + " saved ", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(ctx, "There was a problem saving the file", Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    public boolean deleteFile(String fileName, Context ctx) {
        boolean deleted = false;
        try {
            ctx.deleteFile(fileName);
            deleted = true;
        }catch(Exception e){

        }

        return deleted;
    }

}