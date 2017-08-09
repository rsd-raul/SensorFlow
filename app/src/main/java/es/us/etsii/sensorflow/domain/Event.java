package es.us.etsii.sensorflow.domain;


public class Event {

    // ------------------------- ATTRIBUTES --------------------------

    private int type = -1;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0f;
    private float accuracy = 0.0f;
    private long timestamp = 0L;

    // ------------------------- CONSTRUCTOR -------------------------

    public Event() { }

    public Event(int type, double latitude, double longitude, double altitude, float accuracy, long timestamp) {
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
