package es.us.etsii.sensorflow.managers;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import es.us.etsii.sensorflow.domain.SensorData;
import io.realm.Realm;

@Singleton
public class RealmManager {

    private Realm mRealm;

    @Inject
    RealmManager(Realm mRealm) {
        this.mRealm = mRealm;
    }

    public void storeSensorDataBatch(final List<SensorData> sensorDataBatch){
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealm(sensorDataBatch);
            }
        });
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
