package com.raul.rsd.android.sensorflow.utils;

import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.raul.rsd.android.sensorflow.domain.Sample;

public class CommunicationUtils {

    public static PutDataRequest getDataRequestFromSamplesBatch(Sample[] samples) {
        // Format samples batch to be sent to the phone
        int batchSize = samples.length;
        long[] timestamps = new long[batchSize];
        float[] accelerometerXs = new float[batchSize];
        float[] accelerometerYs = new float[batchSize];
        float[] accelerometerZs = new float[batchSize];

        for (int i = 0; i < samples.length; i++) {
            timestamps[i] = samples[i].getTimestamp();
            accelerometerXs[i] = samples[i].getAccelerometerX();
            accelerometerYs[i] = samples[i].getAccelerometerY();
            accelerometerZs[i] = samples[i].getAccelerometerZ();
        }

        // Prepare package to send
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Constants.SAMPLES_PATH);
        putDataMapRequest.getDataMap().putLongArray(Constants.COLUMN_TIMESTAMP, timestamps);
        putDataMapRequest.getDataMap().putFloatArray(Constants.COLUMN_ACCELEROMETER_X, accelerometerXs);
        putDataMapRequest.getDataMap().putFloatArray(Constants.COLUMN_ACCELEROMETER_Y, accelerometerYs);
        putDataMapRequest.getDataMap().putFloatArray(Constants.COLUMN_ACCELEROMETER_Z, accelerometerZs);

        return putDataMapRequest.asPutDataRequest();
    }
}
