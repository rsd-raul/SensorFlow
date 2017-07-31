package es.us.etsii.sensorflow.utils;

import es.us.etsii.sensorflow.R;

public abstract class Constants {

    // Configurable:

    private static final int FREQUENCY_HZ = 50;
    private static final double SAMPLE_WINDOW_S = 4.0;      // Needs 200 as sample size
    private static final double OVERLAPPING_PERCENTAGE = 50;
    public static final int ACTIVITY_TO_REPORT = Constants.STANDING_INDEX;

    // Constants:

    public static final int GOOGLE_AUTH = 1;
    private static final int MS2US = 1000;
    private static final double MS2S = 0.001;
    private static final double S2M = 0.0166666667;
    public static final int UI_REFRESH_RATE_MS = 1000;

    // TensorFlow Model:

    public static final int SAMPLE_SIZE = (int) Math.floor(FREQUENCY_HZ * SAMPLE_WINDOW_S);
    public static final int OVERLAP_FROM_INDEX = (int) Math.ceil(SAMPLE_SIZE * (OVERLAPPING_PERCENTAGE/100));
    public static final int SAMPLING_PERIOD_US = Math.round((1000/FREQUENCY_HZ) * MS2US);

    private static final double SAMPLING_PERIOD_M = (1000/FREQUENCY_HZ) * MS2S * S2M;
    public static final double M_ELAPSED_PER_SAMPLE = (SAMPLE_SIZE / (100/OVERLAPPING_PERCENTAGE)) * SAMPLING_PERIOD_M;

    public static final int STAIRS_DOWN_INDEX = 0, RUNNING_INDEX = 1, SEATED_INDEX = 2,
            STANDING_INDEX = 3, STAIRS_UP_INDEX = 4, WALKING_INDEX = 5;
    //FIXME: New icon for stairs_down
    public static final int[] ACTIVITY_IMAGES = {R.drawable.ic_stairs_down_24dp, R.drawable.ic_run_24dp,
            R.drawable.ic_seat_24dp, R.drawable.ic_standing_24dp, R.drawable.ic_stairs_up_24dp,
            R.drawable.ic_walk_24dp};

    public static final int[] ACTIVITY_NAMES = {R.string.downstairs, R.string.jogging,
            R.string.sitting, R.string.standing, R.string.upstairs, R.string.walking};
}
