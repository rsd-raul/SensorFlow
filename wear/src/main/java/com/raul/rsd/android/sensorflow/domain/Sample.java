package com.raul.rsd.android.sensorflow.domain;

public class Sample {

    // ------------------------- ATTRIBUTES --------------------------

    private long timestamp;
    private float accelerometerX;
    private float accelerometerY;
    private float accelerometerZ;

    // ------------------------- CONSTRUCTOR -------------------------

    public Sample() { }

    public Sample(float accelerometerX, float accelerometerY, float accelerometerZ) {
        this.timestamp = System.currentTimeMillis();
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }
    public void setAccelerometerX(float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }
    public void setAccelerometerY(float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public float getAccelerometerZ() {
        return accelerometerZ;
    }
    public void setAccelerometerZ(float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }
}
