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
        this.samples = new RealmList<>();
    }

    // ---------------------- GETTERS & SETTERS ----------------------

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public RealmList<Sample> getSamples() {
        return samples;
    }

    public void addSample(Sample sample){
        samples.add(sample);
    }

    // -------------------------- USE CASES --------------------------

    public void complete(int type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public Prediction reset(int overlapIndex) {
        this.id = PrimaryKeyFactory.nextKey();

        RealmList<Sample> temp = new RealmList<>();
        for (int i = overlapIndex; i < samples.size(); i++)
            temp.add(samples.get(i));
        samples = temp;

        return this;
    }

    // -------------------------- AUXILIARY --------------------------
}
