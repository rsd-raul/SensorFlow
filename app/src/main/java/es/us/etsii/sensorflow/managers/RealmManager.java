package es.us.etsii.sensorflow.managers;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;
import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.utils.Constants;
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

    private RealmResults<Sample> findAllSamples(){
        return mRealm.where(Sample.class).findAll();
    }

    private RealmResults<Prediction> findAllPredictions(){
        return mRealm.where(Prediction.class).findAll();
    }

    public double findActiveSecondsToday(){
        RealmResults<Prediction> rr = findPredictionsToday();
        return filterByActive(rr).count() * Constants.S_ELAPSED_PER_SAMPLE;
    }

    public double findActiveSecondsTotal(){
        RealmResults<Prediction> rr = findAllPredictions();
        return filterByActive(rr).count() * Constants.S_ELAPSED_PER_SAMPLE;
    }

    public RealmResults<Prediction> findPredictionsToday(){
        return findAllPredictions().where().greaterThanOrEqualTo("timestamp", getDayStart()).findAll();
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

    // -------------------------- AUXILIARY --------------------------

    private RealmQuery<Prediction> filterByActive(RealmResults<Prediction> realmResults){
        return realmResults.where()
                .equalTo("type", Constants.RUNNING_INDEX).or()
                .equalTo("type", Constants.WALKING_INDEX).or()
                .equalTo("type", Constants.STAIRS_DOWN_INDEX).or()
                .equalTo("type", Constants.STAIRS_UP_INDEX);
    }

    private long getDayStart(){
        // Get the current day/time and restart the values to 0h 0m 0s 0ms
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        return todayCal.getTimeInMillis();
    }

    public void openRealm(){
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        mRealm.close();
    }
}
