package es.us.etsii.sensorflow.utils;

import android.os.Environment;

import java.io.File;

import es.us.etsii.sensorflow.R;

public abstract class Constants {

    // Configurable:

    private static final int FREQUENCY_HZ = 50;
    private static final double SAMPLE_WINDOW_S = 4.0;          // Needs 200 as sample size
    private static final double OVERLAPPING_PERCENTAGE = 50;    // FIXME test with overlap 0
    public static final int ACTIVITY_TO_REPORT = Constants.STANDING_INDEX;

    public static final String CSV_FILE_PREFIX = "DataSet_";
    public static final String CSV_FOLDER_ROUTE = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "Download" + File.separator + "SensorFlow_Export";
    static final char CSV_SEPARATOR = ',';

    // CSVUtils Export

    static final String COLUMN_USER = "user-id";
    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_ACCELEROMETER_X = "acce-x";
    static final String COLUMN_ACCELEROMETER_Y = "acce-y";
    static final String COLUMN_ACCELEROMETER_Z = "acce-z";

    // Constants:

    public static final int GOOGLE_AUTH = 1;
    public static final String CSV_FOLDER = "key2";
    public static final String SUBLIME_OPTIONS = "key3";
    static final String SUBLIME_PICKER = "key4";
    public static final int FROM_PICKER = 5, TO_PICKER = 6;
    public static final int WARN = 7, APPEND = 8, OVERRIDE = 9;

    private static final int MS2US = 1000;
    private static final double MS2S = 0.001;
    public static final int UI_REFRESH_RATE_MS = 1000;

    // TensorFlow Model:

    public static final int SAMPLE_SIZE = (int) Math.floor(FREQUENCY_HZ * SAMPLE_WINDOW_S);
    public static final int OVERLAP_FROM_INDEX = (int) Math.ceil(SAMPLE_SIZE * (OVERLAPPING_PERCENTAGE/100));
    public static final int SAMPLING_PERIOD_US = Math.round((1000/FREQUENCY_HZ) * MS2US);

    private static final double SAMPLING_PERIOD_S = (1000/FREQUENCY_HZ) * MS2S;
    public static final double S_ELAPSED_PER_SAMPLE = (SAMPLE_SIZE / (100/OVERLAPPING_PERCENTAGE)) * SAMPLING_PERIOD_S;

    //FIXME: New icon for stairs_down
    public static final int[] PREDICTION_IMAGES = {R.drawable.ic_stairs_down_24dp,
            R.drawable.ic_run_24dp, R.drawable.ic_seat_24dp, R.drawable.ic_standing_24dp,
            R.drawable.ic_stairs_up_24dp, R.drawable.ic_walk_24dp};
    public static final int[] PREDICTION_NAMES = {R.string.downstairs, R.string.jogging,
            R.string.sitting, R.string.standing, R.string.upstairs, R.string.walking};
    public static final int STAIRS_DOWN_INDEX = 0, RUNNING_INDEX = 1, SEATED_INDEX = 2,
            STANDING_INDEX = 3, STAIRS_UP_INDEX = 4, WALKING_INDEX = 5;
}
