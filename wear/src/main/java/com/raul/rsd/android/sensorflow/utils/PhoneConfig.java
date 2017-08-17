package com.raul.rsd.android.sensorflow.utils;

public class PhoneConfig {

    // Configurable:

    private static int FREQUENCY_HZ;
    private static double SAMPLE_WINDOW_S;
    private static double OVERLAPPING_PERCENTAGE;
    private static int ACTIVITY_TO_REPORT;

    public PhoneConfig(int frequencyHz, double sampleWindowsS, double overlappingPercentage,
                       int activityToReport) {
        FREQUENCY_HZ = frequencyHz;
        SAMPLE_WINDOW_S = sampleWindowsS;
        OVERLAPPING_PERCENTAGE = overlappingPercentage;
        ACTIVITY_TO_REPORT = activityToReport;
    }
}
