package es.us.etsii.sensorflow.utils;

import android.os.Environment;
import android.support.annotation.IntDef;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import es.us.etsii.sensorflow.R;

public abstract class Constants {

    // Configurable:

    private static final int FREQUENCY_HZ = 50;                 // FREQ * WIND_S = SAMPLE_SIZE
    private static final double SAMPLE_WINDOW_S = 4.0;
    private static final double OVERLAPPING_PERCENTAGE = 50.0;
    public static final int ACTIVITY_TO_REPORT = Constants.STANDING_INDEX;
    public static final int UI_REFRESH_RATE_MS = 1000;

    // Configurable - CSV:

    public static final String CSV_FILE_PREFIX = "DataSet_";
    public static final String CSV_FOLDER_ROUTE = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "Download" + File.separator + "SensorFlow_Export";
    static final char CSV_SEPARATOR = ',';

    static final boolean HEADERS_CSV = true;
    static final boolean CSV_USE_QUOTES = false;
    static final String COLUMN_USER = "user-id";
    static final String COLUMN_TIMESTAMP = "timestamp";
    static final String COLUMN_ACCELEROMETER_X = "acce-x";
    static final String COLUMN_ACCELEROMETER_Y = "acce-y";
    static final String COLUMN_ACCELEROMETER_Z = "acce-z";

    // Configurable - TF:

    public static final String MODEL_FILE = "file:///android_asset/frozen_model.pb";
//    public static final String MODEL_FILE = "file:///android_asset/frozen_har.pb";

    //FIXME: New icon for stairs_down
    public static final int[] PREDICTION_IMAGES = {R.drawable.ic_stairs_down_24dp,
            R.drawable.ic_run_24dp, R.drawable.ic_seat_24dp, R.drawable.ic_standing_24dp,
            R.drawable.ic_stairs_up_24dp, R.drawable.ic_walk_24dp};
    public static final int[] PREDICTION_NAMES = {R.string.downstairs, R.string.jogging,
            R.string.sitting, R.string.standing, R.string.upstairs, R.string.walking};

    public static final int STAIRS_DOWN_INDEX = 0, RUNNING_INDEX = 1, SEATED_INDEX = 2,
            STANDING_INDEX = 3, STAIRS_UP_INDEX = 4, WALKING_INDEX = 5;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STAIRS_DOWN_INDEX, RUNNING_INDEX, SEATED_INDEX, STANDING_INDEX, STAIRS_UP_INDEX, WALKING_INDEX})
    public @interface ActivityIndex{}
    public static final int OUTPUT_SIZE = 6;

    // Constants:

    public static final int GOOGLE_AUTH = 1;
    public static final String CSV_FOLDER = "key2";
    public static final String SUBLIME_OPTIONS = "key3";
    static final String SUBLIME_PICKER = "key4";
    public static final int FROM_PICKER = 5, TO_PICKER = 6;
    private static final int MS2US = 1000;

    public static final int WARN = 0, APPEND = 1, OVERRIDE = 2;     // Indexes on Spinner
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WARN, APPEND, OVERRIDE})
    public @interface ConflictMode{}

    static final int ALL = 1, FROM_DATE = 2, WITH_RANGE = 3, MALFORMED = 4;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ALL, FROM_DATE, WITH_RANGE, MALFORMED})
    @interface ExportMode{}

    /** SAMPLE_SIZE is calculated with the window (do not modify the value directly) */
    public static final int SAMPLE_SIZE = (int) Math.floor(FREQUENCY_HZ * SAMPLE_WINDOW_S);
    public static final int OVERLAP_FROM_INDEX = SAMPLE_SIZE - (int) Math.ceil(SAMPLE_SIZE * (OVERLAPPING_PERCENTAGE/100));
    public static final int SAMPLING_PERIOD_US = Math.round((1000/FREQUENCY_HZ) * MS2US);
    public static final double S_ELAPSED_PER_SAMPLE = SAMPLE_WINDOW_S - (SAMPLE_WINDOW_S * (OVERLAPPING_PERCENTAGE/100));

    // Android Wear

    public static final String SAMPLES_PATH = "/sample_batch";
    public static final String PREDICTION_PATH = "/current_prediction";
    static final String CONFIGURATION_PATH = "/phone_configuration";

    // TODO - Wear - don't send or store parameters not needed by the Wear App
//    public static final String COLUMN_FREQUENCY_HZ = "freq_hz";
//    public static final String COLUMN_SAMPLE_WINDOW_S = "windows_s";
//    public static final String COLUMN_OVERLAPPING_PERCENTAGE = "overlap_perc";
}
