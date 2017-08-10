package es.us.etsii.sensorflow.utils;

import android.util.Log;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.domain.Config;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.managers.AuthManager;
import es.us.etsii.sensorflow.managers.RealmManager;
import io.realm.RealmResults;

public abstract class CSVUtils {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "CSVUtils";

    // -------------------------- USE CASES --------------------------

    public static Integer exportToCSV(Config exportConfig, RealmManager realmManager) {
        // Make sure the file has a name by checking the path doesn't end in /.csv
        String fileName = exportConfig.getFullFilePath();
        if(fileName.substring(fileName.length()-5, fileName.length()-4).equals(File.separator))
            return R.string.problems_no_valid_name;

        Boolean response;
        switch (classifyCSVExport(exportConfig)){
            case Constants.ALL:
                response = CSVUtils.exportAllToCSV(exportConfig, realmManager);
                break;
            case Constants.FROM_DATE:
                response = CSVUtils.exportFromDateToCSV(exportConfig, realmManager);
                break;
            case Constants.WITH_RANGE:
                response = CSVUtils.exportWithTimeFrameToCSV(exportConfig, realmManager);
                break;
            case Constants.MALFORMED:
            default:
                return R.string.incorrect_settings;
        }

        if(response == null)
            return R.string.problems_no_samples_found;
        else if (!response)
            return R.string.problems_storing_samples;
        else
            return R.string.samples_stored;
    }

    private static Boolean exportAllToCSV(Config config, RealmManager rm){
        // Get all samples and return only the number of samples requested
        List<Sample> samples = rm.findAllSamples();
        if(config.getMaxSamples() > 0)
            samples = samples.subList(0, Math.min(config.getMaxSamples(), samples.size()));

        return exportToCsv(config, samples);
    }

    private static Boolean exportWithTimeFrameToCSV(Config config, RealmManager rm){
        RealmResults<Sample> samples = rm.findSamplesWithTimeFrame(config.getFromDate(),
                                                                                config.getToDate());
        return exportToCsv(config, samples);
    }

    private static Boolean exportFromDateToCSV(Config config, RealmManager rm){
        // Filter by date and return only the number of samples requested
        List<Sample> samples = rm.findSamplesFromDate(config.getFromDate());
        if(config.getMaxSamples() > 0)
            samples = samples.subList(0, Math.min(config.getMaxSamples(), samples.size()));

        return exportToCsv(config, samples);
    }

    private static Boolean exportToCsv(Config config, List<Sample> samples){
        // If we have an empty list, send null so we inform the user
        if(samples.size()==0)
            return null;

        String fullFilePath = config.getFullFilePath();
        int conflictIndex = config.getConflictIndex();
        FileWriter fileWriter;
        CSVWriter csvWriter;
        File f = new File(fullFilePath);

        try {
            // If file already exists and , if not, create a new one
            if(f.exists() && !f.isDirectory())
                fileWriter = new FileWriter(fullFilePath, conflictIndex == Constants.APPEND);
            else
                fileWriter = new FileWriter(fullFilePath);
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
            csvWriter.writeNext(data, Constants.CSV_USE_QUOTES);
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
            csvWriter.writeNext(data, Constants.CSV_USE_QUOTES);
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

    // ------------------------- AUXILIARY ---------------------------

    @Constants.ExportMode private static int classifyCSVExport(Config config) {
//        if(config.getFullFilePath().toCharArray())
//FIXME if fileEmpty complain


        if(config.getFromDate() == 0) {
            if (config.getToDate() == 0)
                return Constants.ALL;                     // Nothing set
            else
                return Constants.MALFORMED;               // ToDate without FromDate
        } else {
            if (config.getToDate() == 0)
                return Constants.FROM_DATE;               // Only FromDateSet
            else
                return Constants.WITH_RANGE;              // Both FromDate and ToDate set
        }
    }
}
