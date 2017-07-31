package es.us.etsii.sensorflow.domain;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SensorData extends RealmObject {

    @PrimaryKey
    private long id = -1;
    private float accelerometerX;
    private float accelerometerY;
    private float accelerometerZ;

    public SensorData() { }

    public SensorData(float accelerometerX, float accelerometerY, float accelerometerZ) {
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
