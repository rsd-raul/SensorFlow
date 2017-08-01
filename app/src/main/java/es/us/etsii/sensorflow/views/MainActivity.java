package es.us.etsii.sensorflow.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.domain.Event;
import es.us.etsii.sensorflow.domain.Prediction;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.managers.AuthManager;
import es.us.etsii.sensorflow.managers.FirebaseManager;
import es.us.etsii.sensorflow.managers.RealmManager;
import es.us.etsii.sensorflow.utils.TensorFlowClassifier;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;
import es.us.etsii.sensorflow.utils.Utils;

public class MainActivity extends BaseActivity implements SensorEventListener {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MainActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.startAndStopFAB) FloatingActionButton startAndStopFAB;
    @BindView(R.id.iv_current_activity) ImageView currentActivityIV;
    @BindView(R.id.tv_current_activity) TextView currentActivityTV;
    @BindViews({R.id.tv_ace_x, R.id.tv_ace_y, R.id.tv_ace_z}) List<TextView> mSensorsInfoTVs;
    @BindView(R.id.tv_today_time) TextView mTodayTimeTV;
    @BindView(R.id.tv_total_time) TextView mTotalTimeTV;
    @BindView(R.id.rv_todays_activities) RecyclerView todaysActivitiesRV;
    @Inject AuthManager mAuthManager;
    @Inject Lazy<SensorManager> mSensorManager;
    @Inject Lazy<RealmManager> mRealmManager;
    @Inject Lazy<Sensor[]> mCriticalSensors;
    @Inject Lazy<TensorFlowClassifier> mClassifier;
    @Inject Lazy<FusedLocationProviderClient> mFusedLocationClient;
    @Inject FastItemAdapter<PredictionItem> mFastAdapter;
    @Inject Provider<PredictionItem> mActivityItemProvider;
    private static Prediction mPrediction;
    private static float[] sAllSensorData = new float[3];
    private static List<Float> sX = new ArrayList<>(), sY = new ArrayList<>(), sZ = new ArrayList<>();
    private boolean RUNNING = false;
    private double mTodayExercise = -1, mTotalExercise = -1;
    private List<Sample> sensorDataBatch = new ArrayList<>();

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    protected void inject(App.AppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start up
        ButterKnife.bind(this);
        // FIXME check for Internet connection first
        FirebaseApp.initializeApp(this);
        mAuthManager.init(this);  // TODO Run in background? Skipping frames...
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result from GoogleSignInApi
        if (requestCode == Constants.GOOGLE_AUTH)
            mAuthManager.handleSignInResult(data);
    }

    // --------------------------- STATES ----------------------------

    @Override
    protected void onResume() {
        super.onResume();

        // If the service was running, restart it and star the UI updater
//        registerSensorListener();
//        updateSensorValuesUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mSensorManager.unregisterListener(this);

        // When left in background, recalculate today/total exercise
        mTodayExercise = -1;
        mTotalExercise = -1;
    }

    // ------------------------- AUXILIARY ---------------------------

    private float[] mergeAndFormatData() {
        List<Float> data = new ArrayList<>();
        data.addAll(sX);
        data.addAll(sY);
        data.addAll(sZ);

        float[] array = new float[data.size()];
        for (int i = 0; i < data.size(); i++)
            array[i] = data.get(i) != null ? data.get(i) : Float.NaN;
        return array;
    }

    public boolean isUserActive(int eventIndex) {
        return eventIndex == Constants.RUNNING_INDEX || eventIndex == Constants.WALKING_INDEX ||
               eventIndex == Constants.STAIRS_UP_INDEX || eventIndex == Constants.STAIRS_DOWN_INDEX;
    }

    // -------------------------- USE CASES --------------------------

    private void registerSensorListener() {
        for (Sensor sensor : mCriticalSensors.get()) {
            // Check existence, despite the Manifest requirements, a user can side-load the apk
            if (sensor == null)
                DialogUtils.criticalErrorDialog(this, R.string.sensor_missing,
                        R.string.sensor_missing_description);
            else
                mSensorManager.get().registerListener(this, sensor, Constants.SAMPLING_PERIOD_US);
        }
    }

    /**
     * Once every UI_REFRESH_RATE_MS update sensor values with the data stored in sAllSensorData.
     */
    private void updateSensorValuesUI() {
        RUNNING = true;

        // Using a handler to create a recurrent task
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                // Apply the same action to all the views
                ButterKnife.apply(mSensorsInfoTVs, UPDATE);

                if (RUNNING)
                    h.postDelayed(this, Constants.UI_REFRESH_RATE_MS);
            }
        }, Constants.UI_REFRESH_RATE_MS);
    }

    @OnClick(R.id.startAndStopFAB)
    void clickRunAndStop() {
        int dra, col;

        // Toggle run and stop
        RUNNING = !RUNNING;

        // Depending on the action required, stop or start the service (and customize FAB)
        if (RUNNING) {
            dra = R.drawable.ic_stop_24dp;
            col = R.color.redDark;

            // Start service and UI updater
            mRealmManager.get().openRealm();
            registerSensorListener();
            updateSensorValuesUI();

        } else {
            dra = R.drawable.ic_play_24dp;
            col = R.color.tealDark;

            // Stop service
            mSensorManager.get().unregisterListener(this);
            mRealmManager.get().closeRealm();
        }

        // Change colors and function
        startAndStopFAB.setImageResource(dra);
        startAndStopFAB.setBackgroundTintList(ContextCompat.getColorStateList(this, col));

        // Animate changes
        ScaleAnimation expandAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expandAnimation.setDuration(150);
        expandAnimation.setInterpolator(new AccelerateInterpolator());
        startAndStopFAB.startAnimation(expandAnimation);
    }

    private void predictActivity() {
        float[] results = mClassifier.get().predictProbabilities(mergeAndFormatData());

        // Extract from the results the index of the most probable one and set the activity
        int index = 0;
        float higher = Float.MIN_VALUE;
        for (int i = 0; i < results.length; i++) {
            if (results[i] > higher) {
                higher = results[i];
                index = i;
            }
        }

        mPrediction.complete(index, System.currentTimeMillis());
        mRealmManager.get().storePrediction(mPrediction);



        updateCurrentActivity(index);
        checkIfDangerousEvent(index);


        // TODO REMOVE
        // Overlap the samples by the OVERLAPPING_PERCENTAGE set on Constants
        sX = sX.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);
        sY = sY.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);
        sZ = sZ.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);


        // Reset the prediction once stored and UI updated
        mPrediction.reset(Constants.OVERLAP_FROM_INDEX);
    }

    private void checkIfDangerousEvent(final int eventIndex){
        // Check if it's the particular Event we want and that we have permissions for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || eventIndex != Constants.ACTIVITY_TO_REPORT)
            return;

        // Request the location, once obtained, log the event in the DB
        mFusedLocationClient.get().getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location == null)
                            return;

                        Event event = new Event();
                        event.setType(eventIndex);
                        event.setTimestamp(System.currentTimeMillis());
                        event.setLatitude(location.getLatitude());
                        event.setLongitude(location.getLongitude());
                        event.setAltitude(location.getAltitude());
                        event.setAccuracy(location.getAccuracy());
                        FirebaseManager.createEvent(event);
                    }
                });
    }
    // -------------------------- INTERFACE --------------------------

    private final ButterKnife.Action<TextView> UPDATE = new ButterKnife.Action<TextView>() {
        @Override
        public void apply(@NonNull TextView view, int index) {
            view.setText(String.valueOf(sAllSensorData[index]));
        }
    };

    private void updateCurrentActivity(final int eventIndex) {
        int activityImageRes = Constants.ACTIVITY_IMAGES[eventIndex];
        int activityNameRes = Constants.ACTIVITY_NAMES[eventIndex];

        currentActivityIV.setImageResource(activityImageRes);
        currentActivityTV.setText(activityNameRes);
        currentActivityIV.setContentDescription(currentActivityTV.getText());

        // Add the prediction to the list or modify it's values
        addPredictionToList();

        // Recalculate the exercises done today in case of activity
        if(mTodayExercise == -1 && mTotalExercise == -1)
            calculateTodayAndTotal();
        else if(isUserActive(eventIndex)) {
            mTodayExercise += Constants.M_ELAPSED_PER_SAMPLE;
            mTotalExercise += Constants.M_ELAPSED_PER_SAMPLE;
        }

        // Update total and today's time counter
        mTodayTimeTV.setText(Utils.getCustomDurationString(this, (int) Math.floor(mTodayExercise)));
        mTotalTimeTV.setText(Utils.getCustomDurationString(this, (int) Math.floor(mTotalExercise)));
    }

    private void addPredictionToList() {
        int itemCount = mFastAdapter.getAdapterItemCount();

        // Get the last item in the list if there is one
        PredictionItem lastItem = itemCount > 0 ? mFastAdapter.getAdapterItem(itemCount - 1) : null;

        // If the last item
        if(lastItem != null && lastItem.getType() == mPrediction.getType())
            lastItem.addToTotalTime(Constants.M_ELAPSED_PER_SAMPLE);
        else
            mFastAdapter.add(mActivityItemProvider.get()
                    .withPrediction(mPrediction.getType(), mPrediction.getTimestamp()));

        // TODO necessary?
//        mFastAdapter.notifyDataSetChanged();
    }

    // FIXME use the DB to calculate
    private void calculateTodayAndTotal() {
        mTodayExercise = 0;
        mTotalExercise = 0;
    }

    private void setupTodaysActivities(){
        todaysActivitiesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        todaysActivitiesRV.setAdapter(mFastAdapter);

        mFastAdapter.withOnClickListener(new FastAdapter.OnClickListener<PredictionItem>() {
                @Override
                public boolean onClick(View v, IAdapter<PredictionItem> adapter, PredictionItem item, int position) {
                    Toast.makeText(MainActivity.this, "Click on item with id: " + item.getTimestamp(),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
    }

    // -------------------------- LISTENER ---------------------------

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        sAllSensorData[0] = sensorEvent.values[0];
        sAllSensorData[1] = sensorEvent.values[1];
        sAllSensorData[2] = sensorEvent.values[2];

        // Add the a new data sample to the
        sensorDataBatch.add(new Sample(sAllSensorData[0], sAllSensorData[1], sAllSensorData[2]));
        if(sensorDataBatch.size() == Constants.REALM_BATCH_SIZE) {
            mRealmManager.get().storeSensorDataBatch(sensorDataBatch);
            sensorDataBatch.clear();
        }

        if (sX.size() == Constants.SAMPLE_SIZE)
            predictActivity();

        sX.add(sensorEvent.values[0]);
        sY.add(sensorEvent.values[1]);
        sZ.add(sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { /* Do nothing */ }
}
