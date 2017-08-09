package es.us.etsii.sensorflow.domain;

public class Config {

    private String fullFilePath;
    private int maxSamples;
    private long fromDate;
    private long toDate;
    private int conflictIndex;


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

    public int getConflictIndex() {
        return conflictIndex;
    }

    public void setConflictIndex(int conflictIndex) {
        this.conflictIndex = conflictIndex;
    }
}
