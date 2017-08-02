package es.us.etsii.sensorflow.managers;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import io.realm.Realm;
import io.realm.RealmResults;

@Singleton
public class RealmManager {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "RealmManager";

    // ------------------------- ATTRIBUTES --------------------------

    private Realm mRealm;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    RealmManager(Realm mRealm) {
        this.mRealm = mRealm;
    }

    // -------------------------- USE CASES --------------------------

    /**
     * Store the current Prediction along with the Samples used to calculate it.
     *
     * @param prediction Prediction containing Samples
     */
    public void storePrediction(final Prediction prediction){
        // FIXME Ideally async -> IllegalStateException currently
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.copyToRealmOrUpdate(prediction);

                // TODO - Testing only - Remove before delivery
//                Log.e(TAG, "storePrediction: findAllSamples: " + findAllSamples().size());
//                Log.e(TAG, "storePrediction: findAllPredictions: " + findAllPredictions().size());
            }
        });
    }

    public RealmResults<Sample> findAllSamples(){
        return mRealm.where(Sample.class).findAll();
    }

    public RealmResults<Prediction> findAllPredictions(){
        return mRealm.where(Prediction.class).findAll();
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
