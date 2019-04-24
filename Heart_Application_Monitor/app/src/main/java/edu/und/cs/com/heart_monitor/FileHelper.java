package edu.und.cs.com.heart_monitor;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Andrew on 2/19/2015.
 */
public class FileHelper {
    String fileName;
    FileOutputStream fileOutputStream;

    //Opens FileOutputStream and generates filename from current time and date and stores them both in FileHelper Object
    public void startFile(FileHelper myFileHelper, Context ctx, Boolean isFiltered) {
        Calendar calendar = Calendar.getInstance();                                          //get calendar object to retrieve date and time info for file name
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        String filterStatus = isFiltered ? "" : "unfiltered";
        String tempFileName = (month + "_" + day + "_" + hour + "_" + min + "_" + sec + filterStatus + ".csv");     //combine date and time info into string for file name
        myFileHelper.fileName = tempFileName;
        try {
            FileOutputStream fOut = ctx.openFileOutput(tempFileName, Context.MODE_PRIVATE);
            myFileHelper.fileOutputStream = fOut;
            //Toast.makeText(ctx, "File" + tempFileName + " opened ", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            //Toast.makeText(ctx, "There was a problem opening the file", Toast.LENGTH_LONG).show();
        }
    }

    public void startFilteredFile(FileHelper myFileHelper, Context ctx, String OgFileName) {

        String tempFileName = (OgFileName + ".csv");     //combine date and time info into string for file name
        myFileHelper.fileName = tempFileName;
        try {
            FileOutputStream fOut = ctx.openFileOutput(tempFileName, Context.MODE_PRIVATE);
            myFileHelper.fileOutputStream = fOut;
            //Toast.makeText(ctx, "File" + tempFileName + " opened ", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(ctx, "There was a problem opening the file", Toast.LENGTH_LONG).show();
        }
    }

    //Gets FileOutputStream from FileHeler object and writes current cur_x and fileY value to the file on their own line
    public void appendFile(FileHelper myFileHelper, int xValue, int yValue, Context ctx){
        String contents = xValue + "," + yValue + "\n";         //place and and fileY value on their own line in ouptut file
        try {
            myFileHelper.fileOutputStream.write(contents.getBytes());
        }catch(Exception e){
            //Toast.makeText(ctx, "There was a problem appending the file", Toast.LENGTH_LONG).show();
        }
    }

    //Gets FileOutputStream from FileHeler object and closes current file's output stream
    public void closeFile(FileHelper myFileHelper, Context ctx){
        try {
            myFileHelper.fileOutputStream.close();
        }catch(Exception e){
           // Toast.makeText(ctx, "There was a problem closing the file", Toast.LENGTH_LONG).show();
        }
    }

    /*
  * Opens file and parses it placing the cur_x and fileY values into an Arraylist
  * then returns array list
   */
    public ArrayList loadFile(String fileName, Context ctx) {
        int yValue = 0;
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
                    yValue = Integer.parseInt(RowData[1]);
                    lineCount++;
                    myFileInfo.add(yValue);
                }
            }catch (IOException ex) {
                // handle exception trying to read line from file
            }finally {
                try {
                    fis.close();
                }catch (IOException e) {
                    // handle exception trying to close input stream
                }
            }
        } catch (FileNotFoundException e) {
               //handle exception trying to opein input stream
            e.printStackTrace();
        }
        return myFileInfo;
    }

    public Boolean deleteFile(String fileName, Context ctx) {
        return ctx.deleteFile(fileName);
    }
}