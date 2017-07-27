package es.us.etsii.sensorflow.managers;

import android.content.Context;
import java.util.List;
import javax.inject.Inject;
import es.us.etsii.sensorflow.domain.SensorData;
import io.realm.Realm;

public class RealmManager {

    private Realm mRealm;

    @Inject
    public RealmManager(Context context) {
        Realm.init(context);
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
