package es.us.etsii.sensorflow.utils;

import es.us.etsii.sensorflow.R;

public abstract class Constants {

    // Configurable:

    private static final int FREQUENCY_HZ = 50;
    private static final double SAMPLE_WINDOW_S = 4.0;      // Needs 200 as sample size
    private static final double OVERLAPPING_PERCENTAGE = 50;

    // Constants:

    public static final int GOOGLE_AUTH = 1;
    private static final int MS2US = 1000;
    public static final int UI_REFRESH_RATE_MS = 1000;

    // TensorFlow Model:

    public static final int SAMPLE_SIZE = (int) Math.floor(FREQUENCY_HZ * SAMPLE_WINDOW_S);
    public static final int OVERLAP_FROM_INDEX = (int) Math.ceil(SAMPLE_SIZE * (OVERLAPPING_PERCENTAGE/100));
    public static final int SAMPLING_PERIOD_US = Math.round((1000/FREQUENCY_HZ) * Constants.MS2US);

    //FIXME: New icon for stairs_down
    public static final int[] ACTIVITY_IMAGES = {R.drawable.ic_stairs_down_24dp, R.drawable.ic_run_24dp,
            R.drawable.ic_seat_24dp, R.drawable.ic_standing_24dp, R.drawable.ic_stairs_up_24dp,
            R.drawable.ic_walk_24dp};

    public static final int[] ACTIVITY_NAMES = {R.string.downstairs, R.string.jogging,
            R.string.sitting, R.string.standing, R.string.upstairs, R.string.walking};
}
