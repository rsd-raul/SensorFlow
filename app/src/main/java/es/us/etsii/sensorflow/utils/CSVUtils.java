package es.us.etsii.sensorflow.utils;

import android.content.Context;
import android.util.Log;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import es.us.etsii.sensorflow.domain.Config;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.managers.AuthManager;
import es.us.etsii.sensorflow.managers.RealmManager;

public abstract class CSVUtils {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "CSVUtils";

    public static int sConflictIndex = Constants.WARN;

    // -------------------------- USE CASES --------------------------

    public static boolean exportAllToCSV(Config config, RealmManager rm){
        // Get all samples and return only the number of samples requested
        List<Sample> samples = rm.findAllSamples();
        if(config.getMaxSamples() > 0)
            samples = samples.subList(0, config.getMaxSamples()-1);

        return exportToCsv(config.getFullFilePath(), samples);
    }

    public static boolean exportWithTimeFrameToCSV(Config config, RealmManager rm){
        return exportToCsv(config.getFullFilePath(),
                rm.findSamplesWithTimeFrame(config.getFromDate(), config.getToDate()));
    }

    public static boolean exportFromDateToCSV(Config config, RealmManager rm){
        // Filter by date and return only the number of samples requested
        List<Sample> samples = rm.findSamplesFromDate(config.getFromDate());
        if(config.getMaxSamples() > 0)
            samples = samples.subList(0, config.getMaxSamples()-1);

        return exportToCsv(config.getFullFilePath(), samples);
    }

    private static boolean exportToCsv(String fullFilePath, List<Sample> samples){
        FileWriter fileWriter;
        CSVWriter csvWriter;

        // FIXME DO SOMETHING BASED ON THE CONFLICT
        // sConflictIndex;

        File f = new File(fullFilePath);

        // If file exists, append to it, if not, create a new one
        try {
            fileWriter = f.exists() && !f.isDirectory() ? new FileWriter(fullFilePath, true) : new FileWriter(fullFilePath);
        } catch (IOException ex){
            Log.e(TAG, "exportToCsv: Problem creating the file writer", ex);
            return false;
        }
        csvWriter = new CSVWriter(fileWriter, Constants.CSV_SEPARATOR);

        // Write headers if requested
        if(Constants.HEADERS_CSV){
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
