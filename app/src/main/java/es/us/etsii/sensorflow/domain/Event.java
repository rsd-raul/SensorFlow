package es.us.etsii.sensorflow.domain;


public class Event {

    private int type = -1;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0f;
    private float accuracy = 0.0f;

    public Event() { }

    public Event(int type, double latitude, double longitude, double altitude, float accuracy) {
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
    }

    public int getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }
}
