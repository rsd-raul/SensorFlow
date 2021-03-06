package com.raul.rsd.android.sensorflow.utils;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.raul.rsd.android.sensorflow.R;
import com.raul.rsd.android.sensorflow.domain.Sample;

import java.nio.ByteBuffer;

public abstract class DataUtils {

    // -------------------------- USE CASES --------------------------

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

    public static int getIntFromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    // TODO - Wear - decide which parameters are necessary & implement
    public static void setupPhoneConfigFromDataMap(DataMap dataMap) {
        // Extract the information from the Map
//        PhoneConfig.FREQUENCY_HZ = dataMap.getLong(Constants.COLUMN_TIMESTAMP);
    }

    public static int getActivityNameResFromIndex(@Constants.ActivityIndex int activityIndex) {
        switch (activityIndex) {
            case Constants.RUNNING_INDEX:
                return R.string.jogging;
            case Constants.SEATED_INDEX:
                return R.string.sitting;
            case Constants.STAIRS_DOWN_INDEX:
                return R.string.downstairs;
            case Constants.STAIRS_UP_INDEX:
                return R.string.upstairs;
            case Constants.STANDING_INDEX:
                return R.string.standing;
            case Constants.WALKING_INDEX:
                return R.string.walking;
            default:
                return R.string.unknown;
        }
    }
}
