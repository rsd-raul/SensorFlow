package es.us.etsii.sensorflow.managers;


import android.content.Context;

import javax.inject.Inject;

import es.us.etsii.sensorflow.domain.SensorData;
import io.realm.Realm;

public class RealmManager {

    private Realm mRealm;

    @Inject
    public RealmManager(Context context) {
        Realm.init(context);

        mRealm = Realm.getDefaultInstance();
    }

    public void storeSensorDataBatch(SensorData[] sensorData){

    }
}
