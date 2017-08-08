package es.us.etsii.sensorflow.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.managers.AuthManager;
import es.us.etsii.sensorflow.managers.RealmManager;
import es.us.etsii.sensorflow.views.MainActivity;
import io.realm.RealmResults;

public abstract class CSVUtils {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "CSVUtils";

    // -------------------------- USE CASES --------------------------

    public static boolean exportAllToCSV(String fileName, boolean headers, RealmManager rm){
        return exportToCsv(fileName, rm.findAllSamples(), headers);
    }

    public static boolean exportWithTimeframeToCSV(String fileName, boolean headers, long fromDate, long toDate, RealmManager rm){
        return exportToCsv(fileName, rm.findSamplesWithTimeframe(fromDate, toDate), headers);
    }

    public static boolean exportFromDateToCSV(String fileName, boolean headers, long fromDate, int optionalMaxSamples, RealmManager rm){

        // Filter by date and return only the number of samples requested
        RealmResults<Sample> samples = rm.findSamplesFromDate(fromDate);
        if(optionalMaxSamples > 0)
            samples.subList(0, optionalMaxSamples-1);

        return exportToCsv(fileName, samples, headers);
    }

    private static boolean exportToCsv(String fileName, RealmResults<Sample> samples, boolean headers){
        FileWriter fileWriter;
        CSVWriter csvWriter;

        fileName += ".csv";
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + Constants.CSV_FOLDER_ROUTE + File.separator + fileName;
        File f = new File(filePath);

        // If file exists, append to it, if not, create a new one
        try {
            fileWriter = f.exists() && !f.isDirectory() ? new FileWriter(filePath, true) : new FileWriter(filePath);
        } catch (IOException ex){
            Log.e(TAG, "exportToCsv: Problem creating the file writer", ex);
            return false;
        }
        csvWriter = new CSVWriter(fileWriter, Constants.CSV_SEPARATOR);

        // Write headers if requested
        if(headers){
            String[] data = {
                    Constants.COLUMN_USER,
                    Constants.COLUMN_TIMESTAMP,
                    Constants.COLUMN_ACCELEROMETER_X,
                    Constants.COLUMN_ACCELEROMETER_Y,
                    Constants.COLUMN_ACCELEROMETER_Z};
            csvWriter.writeNext(data);
        }

        // Write samples line by line in the CSVUtils file
        String userId = AuthManager.getUserId();
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);

            String[] data = {
                    userId,
                    String.valueOf(sample.getTimestamp()),
                    String.valueOf(sample.getAccelerometerX()),
                    String.valueOf(sample.getAccelerometerY()),
                    String.valueOf(sample.getAccelerometerZ())};
            csvWriter.writeNext(data);
        }

        try{
            csvWriter.close();
            fileWriter.close();     // csvWriter should close it be default
            return true;
        } catch (IOException ex){
            Log.e(TAG, "exportToCsv: Problem closing the writers", ex);
            return false;
        }
    }
}
