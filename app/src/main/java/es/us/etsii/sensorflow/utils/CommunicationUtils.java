package es.us.etsii.sensorflow.utils;

import com.google.android.gms.wearable.DataMap;
import es.us.etsii.sensorflow.domain.Sample;

public class CommunicationUtils {

    public static Sample[] getSamplesBatchFromDataMap(DataMap dataMap){
        // Extract the information from the Map
        long[] timestamps = dataMap.getLongArray(Constants.COLUMN_TIMESTAMP);
        float[] acceXs = dataMap.getFloatArray(Constants.COLUMN_ACCELEROMETER_X);
        float[] acceYs = dataMap.getFloatArray(Constants.COLUMN_ACCELEROMETER_Y);
        float[] acceZs = dataMap.getFloatArray(Constants.COLUMN_ACCELEROMETER_Z);

        // Format the information and return it
        Sample[] samples = new Sample[timestamps.length];
        for (int i = 0; i < samples.length; i++)
            samples[i] = new Sample(timestamps[i], acceXs[i], acceYs[i], acceZs[i]);
        return samples;
    }
}
