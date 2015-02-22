package edu.und.cs.com.heart_monitor;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Andrew on 2/19/2015.
 */
public class FileHelper {


    public int writeFile(String contents, Context ctx) {

        Calendar calendar = Calendar.getInstance();              //get calendar object to retrieve date and time info for file name
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String fileName = (month + "_" + day + "_" + hour + "_" + min + "_" + sec);     //combine date and time info into string for file name
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

    public int calcFileSize(String fileName, Context ctx) {

        int lineCount = 0;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = ctx.openFileInput( fileName );
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                }
            }
            catch (IOException ex) {
                // handle exception
            }
            finally {
                try {
                    fis.close();
                }
                catch (IOException e) {
                    // handle exception
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lineCount;
    }

    /*
    * Opens file and parses it placing the x and y values into an Arraylist
    * then returns array list
     */
    public ArrayList loadFile(String fileName, Context ctx) {
        int xValue = 0;
        int yValue = 0;
        String nextNum = "";
        ArrayList<Integer> myFileInfo = new ArrayList<>();
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = ctx.openFileInput( fileName );
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            try {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {    //reads on line at a time
                    String[] RowData = line.split(",");         //splits line at comma
                    if(lineCount == 0) RowData[0] = "0";        //ensure first character is read correctly and not null0
                    xValue = Integer.parseInt(RowData[0]);
                    yValue = Integer.parseInt(RowData[1]);
                    lineCount++;
                   myFileInfo.add(xValue);
                   myFileInfo.add(yValue);
                }
            }
            catch (IOException ex) {
                // handle exception
            }
            finally {
                try {
                    fis.close();
                }
                catch (IOException e) {
                    // handle exception
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return myFileInfo;
    }

}