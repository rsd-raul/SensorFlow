package es.us.etsii.sensorflow.managers;

import android.os.Process;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.Utils;
import es.us.etsii.sensorflow.views.MainActivity;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@Singleton
public class RealmManager {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "RealmManager";

    // ------------------------- ATTRIBUTES --------------------------

    private Provider<Realm> mRealmProvider;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    RealmManager(Provider<Realm> mRealmProvider) {
        this.mRealmProvider = mRealmProvider;
    }

    // ---------------------------- FIND -----------------------------

    private RealmResults<Prediction> findAllPredictions(){
        return mRealmProvider.get().where(Prediction.class).findAll();
    }

    public RealmResults<Prediction> findPredictionsFromDate(long fromDate){
        return mRealmProvider.get().where(Prediction.class).greaterThanOrEqualTo("timestamp", fromDate).findAll();
    }

    public RealmResults<Sample> findAllSamples(){
        return mRealmProvider.get().where(Sample.class).findAll();
    }

    public RealmResults<Sample> findSamplesFromDate(long fromDate){
        return mRealmProvider.get().where(Sample.class).greaterThanOrEqualTo("timestamp", fromDate).findAll();
    }

    public RealmResults<Sample> findSamplesWithTimeFrame(long fromDate, long toDate){
        return mRealmProvider.get().where(Sample.class).between("timestamp", fromDate, toDate).findAll();
    }

    // ---------------------------- SAVE -----------------------------

    /**
     * Store the current Prediction along with the Samples used to calculate it.
     *
     * @param prediction Prediction containing Samples
     */
    public void storePrediction(final Prediction prediction){
        mRealmProvider.get().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(prediction);

                // TODO - Testing only - Remove before delivery
//                Log.e(TAG, "storePrediction: findAllSamples: " + findAllSamples().size());
//                Log.e(TAG, "storePrediction: findAllPredictions: " + findAllPredictions().size());
            }
        });
    }

    // --------------------------- DELETE ----------------------------

    public static void deleteAllUserData() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Prediction.class).findAll().deleteAllFromRealm();
                realm.where(Sample.class).findAll().deleteAllFromRealm();
            }
        });
        Process.killProcess(Process.myPid());
    }

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
}
