package es.us.etsii.sensorflow.domain;

import es.us.etsii.sensorflow.utils.Constants;

public class Config {

    // ------------------------- ATTRIBUTES --------------------------

    private String fullFilePath;
    private int maxSamples;
    private long fromDate;
    private long toDate;
    private @Constants.ConflictMode int conflictIndex;

    // ------------------------- CONSTRUCTOR -------------------------

    public Config() { }

    // ---------------------- GETTERS & SETTERS ----------------------

    public String getFullFilePath() {
        return fullFilePath;
    }
    public void setFullFilePath(String fullFilePath) {
        this.fullFilePath = fullFilePath;
    }

    public int getMaxSamples() {
        return maxSamples;
    }
    public void setMaxSamples(int maxSamples) {
        this.maxSamples = maxSamples;
    }

    public long getFromDate() {
        return fromDate;
    }
    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }
    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    @Constants.ConflictMode public int getConflictIndex() {
        return conflictIndex;
    }
    public void setConflictIndex(@Constants.ConflictMode int conflictIndex) {
        this.conflictIndex = conflictIndex;
    }
}
