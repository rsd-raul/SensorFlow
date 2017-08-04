package es.us.etsii.sensorflow.managers;

import javax.inject.Inject;
import javax.inject.Singleton;
import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.Utils;
import io.realm.Realm;
import io.realm.RealmQuery;
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

    // ---------------------------- FIND -----------------------------

    private RealmResults<Prediction> findAllPredictions(){
        return mRealm.where(Prediction.class).findAll();
    }

    public RealmResults<Prediction> findPredictionsFromDate(long fromDate){
        return mRealm.where(Prediction.class).greaterThanOrEqualTo("timestamp", fromDate).findAll();
    }

    public RealmResults<Sample> findAllSamples(){
        return mRealm.where(Sample.class).findAll();
    }

    public RealmResults<Sample> findSamplesFromDate(long fromDate){
        return mRealm.where(Sample.class).greaterThanOrEqualTo("timestamp", fromDate).findAll();
    }

    public RealmResults<Sample> findSamplesWithTimeframe(long fromDate, long toDate){
        return mRealm.where(Sample.class).between("timestamp", fromDate, toDate).findAll();
    }

    // ---------------------------- SAVE -----------------------------

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

    // --------------------------- DELETE ----------------------------

    // -------------------------- USE CASES --------------------------

    public double calculateActiveSecondsToday(){
        RealmResults<Prediction> rr = findPredictionsFromDate(Utils.getDayStart());
        return filterByActive(rr).count() * Constants.S_ELAPSED_PER_SAMPLE;
    }

    public double calculateActiveSecondsTotal(){
        RealmResults<Prediction> rr = findAllPredictions();
        return filterByActive(rr).count() * Constants.S_ELAPSED_PER_SAMPLE;
    }

    // -------------------------- AUXILIARY --------------------------

    private RealmQuery<Prediction> filterByActive(RealmResults<Prediction> realmResults){
        return realmResults.where()
                .equalTo("type", Constants.RUNNING_INDEX).or()
                .equalTo("type", Constants.WALKING_INDEX).or()
                .equalTo("type", Constants.STAIRS_DOWN_INDEX).or()
                .equalTo("type", Constants.STAIRS_UP_INDEX);
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
