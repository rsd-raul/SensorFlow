package es.us.etsii.sensorflow.utils;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import es.us.etsii.sensorflow.domain.Sample;

public abstract class DataUtils {

    // -------------------------- USE CASES --------------------------

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

    public static byte[] getByteArrayFromInt(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    // TODO decide which parameters are necessary & implement
    public static PutDataRequest generateDataRequestFromConstants() {

        // Prepare package to send
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.CONFIGURATION_PATH);
//        putDataMapRequest.getDataMap().putLongArray(Constants.COLUMN_FREQUENCY_HZ, );
//        putDataMapRequest.getDataMap().putFloatArray(Constants.COLUMN_OVERLAPPING_PERCENTAGE, );
//        putDataMapRequest.getDataMap().putFloatArray(Constants.COLUMN_SAMPLE_WINDOW_S, );

        return putDataMapRequest.asPutDataRequest();
    }
}
