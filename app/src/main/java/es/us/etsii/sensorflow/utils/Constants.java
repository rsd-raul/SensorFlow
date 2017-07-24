package es.us.etsii.sensorflow.utils;

public abstract class Constants {

    private static final int MS2US = 1000;
    public static final int UI_REFRESH_RATE_MS = 1000;


    private static final int FREQUENCY_HZ = 50;
    private static final double SAMPLE_WINDOW_S = 4.0;      // Needs 200 as sample size
    private static final double OVERLAPPING_PERCENTAGE = 50;

    public static final int SAMPLE_SIZE = (int) Math.floor(FREQUENCY_HZ * SAMPLE_WINDOW_S);
    public static final int OVERLAP_FROM_INDEX = (int) Math.ceil(SAMPLE_SIZE * (OVERLAPPING_PERCENTAGE/100));
    public static final int SAMPLING_PERIOD_US = Math.round((1000/FREQUENCY_HZ) * Constants.MS2US);
}
