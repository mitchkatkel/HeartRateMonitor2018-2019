package edu.und.cs.com.heart_monitor;

import android.content.Context;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by Andrew on 2/19/2015.
 */
public class FileHelper {


    public int writeFile(String contents, Context ctx) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int time = calendar.get(Calendar.SECOND);
        String fileName = (month + "_" + day + "_" + time);
        try {
            FileOutputStream fOut = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fOut.write(contents.getBytes());
            fOut.close();
            Toast.makeText(ctx, "File saved", Toast.LENGTH_LONG).show();
         }catch(Exception e){
            Toast.makeText(ctx, "There was a problem saving the file", Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    public double[] readFile(String filename) {

        return null;
    }

}