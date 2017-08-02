package es.us.etsii.sensorflow.managers;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import io.realm.Realm;
import io.realm.RealmResults;

@Singleton
public class RealmManager {

    // ------------------------- ATTRIBUTES --------------------------

    private Realm mRealm;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    RealmManager(Realm mRealm) {
        this.mRealm = mRealm;
    }

    // -------------------------- USE CASES --------------------------

    public void storePrediction(final Prediction prediction){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(prediction);

//                Check if Samples are stored as a result of the prediction
                System.out.println("SIZE : " + findAllSensorData().size());
            }
        });
    }

    // FIXME Needs to be async
    public void storeSensorDataBatch(final List<Sample> sensorDataBatch){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealm(sensorDataBatch);
            }
        });
    }

    public RealmResults<Sample> findAllSensorData(){
        return mRealm.where(Sample.class).findAll();
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
