package es.us.etsii.sensorflow.managers;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import es.us.etsii.sensorflow.domain.SensorData;
import io.realm.Realm;
import io.realm.RealmResults;

@Singleton
public class RealmManager {

    private Realm mRealm;

    @Inject
    RealmManager(Realm mRealm) {
        this.mRealm = mRealm;
    }

    // FIXME Needs to be async
    public void storeSensorDataBatch(final List<SensorData> sensorDataBatch){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealm(sensorDataBatch);
            }
        });
    }

    public RealmResults<SensorData> findAllSensorData(){
        return mRealm.where(SensorData.class).findAll();
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
