package es.us.etsii.sensorflow.domain;

import es.us.etsii.sensorflow.utils.PrimaryKeyFactory;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Prediction extends RealmObject {

    // ------------------------- ATTRIBUTES --------------------------

    @PrimaryKey
    private long id;
    private int type;
    private long timestamp;
    private RealmList<Sample> samples;

    // ------------------------- CONSTRUCTOR -------------------------

    public Prediction() {
        this.id = PrimaryKeyFactory.nextKey();
        samples = new RealmList<>();
    }

    public void complete(int type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Prediction reset(int overlapIndex) {
        this.id = PrimaryKeyFactory.nextKey();

        return this;
    }

    // -------------------------- USE CASES --------------------------

    // -------------------------- AUXILIARY --------------------------
}
