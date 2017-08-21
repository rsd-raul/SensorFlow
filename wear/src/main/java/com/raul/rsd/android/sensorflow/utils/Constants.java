package com.raul.rsd.android.sensorflow.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Constants {

    static final String SAMPLES_PATH = "/sample_batch";
    public static final String PREDICTION_PATH = "/current_prediction";
    public static final String CONFIGURATION_PATH = "/phone_configuration";

    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_ACCELEROMETER_X = "acce-x";
    static final String COLUMN_ACCELEROMETER_Y = "acce-y";
    static final String COLUMN_ACCELEROMETER_Z = "acce-z";


    static final int STAIRS_DOWN_INDEX = 0, RUNNING_INDEX = 1, SEATED_INDEX = 2,
            STANDING_INDEX = 3, STAIRS_UP_INDEX = 4, WALKING_INDEX = 5;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STAIRS_DOWN_INDEX, RUNNING_INDEX, SEATED_INDEX, STANDING_INDEX, STAIRS_UP_INDEX, WALKING_INDEX})
    @interface ActivityIndex{}
}
