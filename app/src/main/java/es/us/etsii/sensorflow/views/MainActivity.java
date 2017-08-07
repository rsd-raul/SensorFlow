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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
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
import es.us.etsii.sensorflow.classifiers.TensorFlowClassifier;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;
import es.us.etsii.sensorflow.utils.Utils;

public class MainActivity extends BaseActivity implements SensorEventListener {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MainActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.startAndStopFAB) FloatingActionButton startAndStopFAB;
    @BindView(R.id.iv_current_activity) ImageView currentPredictionIV;
    @BindView(R.id.tv_current_activity) TextView currentPredictionTV;
    @BindViews({R.id.tv_ace_x, R.id.tv_ace_y, R.id.tv_ace_z}) List<TextView> mSensorsInfoTVs;
    @BindView(R.id.tv_today_time) TextView mTodayTimeTV;
    @BindView(R.id.tv_total_time) TextView mTotalTimeTV;
    @BindView(R.id.rv_todays_activities) RecyclerView mTodaysActivitiesRV;
    @BindView(R.id.s_scroll) Switch mScrollS;
    @Inject AuthManager mAuthManager;
    @Inject Lazy<SensorManager> mSensorManager;
    @Inject Lazy<RealmManager> mRealmManager;
    @Inject Lazy<Sensor[]> mCriticalSensors;
    @Inject Lazy<TensorFlowClassifier> mClassifier;
    @Inject Lazy<FusedLocationProviderClient> mFusedLocationClient;
    @Inject FastItemAdapter<PredictionItem> mFastAdapter;
    @Inject Provider<PredictionItem> mPredictionItemProvider;
    private static Prediction mPrediction;
    private static float[] sAllSensorData = new float[3];
    private boolean RUNNING = false;
    private double mTodayExercise = -1, mTotalExercise = -1;

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    protected void inject(App.AppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup activity and bind views
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Setup Today's recycler view using the DB
        mTodaysActivitiesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTodaysActivitiesRV.setAdapter(mFastAdapter);
        for (Prediction prediction : mRealmManager.get().findPredictionsFromDate(Utils.getDayStart()))
            addPredictionToTodayRV(prediction);

        // Calculate total and today's active time using the DB
        mTodayExercise = mRealmManager.get().calculateActiveSecondsToday();
        mTotalExercise = mRealmManager.get().calculateActiveSecondsTotal();
        setCustomHHmmss(mTodayTimeTV, mTodayExercise);
        setCustomHHmmss(mTotalTimeTV, mTotalExercise);
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

    // -------------------------- USE CASES --------------------------

    @OnClick(R.id.startAndStopFAB)
    void clickRunAndStop() {
        int dra, col;

        if(mPrediction == null)
            mPrediction = new Prediction();

        // Toggle run and stop
        RUNNING = !RUNNING;

        // Depending on the action required, stop or start the service (and customize FAB)
        if (RUNNING) {
            // Notify if starting without Firebase login
            if(!mAuthManager.isLoggedFirebase())
                Toast.makeText(this, "Not reporting to Firebase", Toast.LENGTH_SHORT).show();

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

    private void predictAndStoreActivity() {
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


        // Add the prediction to the list or modify it's values
        addPredictionToTodayRV(mPrediction);
        updateUICurrentPrediction(index);

        // If the user is logged into Firebase, check if the prediction is worth sending
        if(mAuthManager.isLoggedFirebase())
            checkIfDangerousEvent(index);

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

    // ------------------------- AUXILIARY ---------------------------

    // FIXME use the DB to calculate
    private void calculateTodayAndTotal() {
        mTodayExercise = 0;
        mTotalExercise = 0;
    }

    private float[] mergeAndFormatData() {
        // The array Sum will be the aggregation of all the accelerometer's data
        float[] arraySum = new float[mPrediction.getSamples().size() * 3];
        int samplesSize = mPrediction.getSamples().size();

        // Iterate over the samples and introduce the data on the correct position
        for (int i = 0; i < samplesSize; i++) {
            Sample sample = mPrediction.getSamples().get(i);

            arraySum[i] = sample.getAccelerometerX();
            arraySum[i + samplesSize] = sample.getAccelerometerY();
            arraySum[i + samplesSize * 2] = sample.getAccelerometerZ();
        }
        return arraySum;
    }

    public boolean isUserActive(int eventIndex) {
        return eventIndex == Constants.RUNNING_INDEX || eventIndex == Constants.WALKING_INDEX ||
                eventIndex == Constants.STAIRS_UP_INDEX || eventIndex == Constants.STAIRS_DOWN_INDEX;
    }

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

    // -------------------------- INTERFACE --------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
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
                ButterKnife.apply(mSensorsInfoTVs, UPDATE_TV);

                if (RUNNING)
                    h.postDelayed(this, Constants.UI_REFRESH_RATE_MS);
            }
        }, Constants.UI_REFRESH_RATE_MS);
    }

    private final ButterKnife.Action<TextView> UPDATE_TV = new ButterKnife.Action<TextView>() {
        @Override
        public void apply(@NonNull TextView view, int index) {
            view.setText(String.valueOf(sAllSensorData[index]));
        }
    };

    private void updateUICurrentPrediction(final int eventIndex) {
        int activityImageRes = Constants.PREDICTION_IMAGES[eventIndex];
        int activityNameRes = Constants.PREDICTION_NAMES[eventIndex];

        currentPredictionTV.setText(activityNameRes);
        currentPredictionIV.setImageResource(activityImageRes);
        currentPredictionIV.setContentDescription(currentPredictionTV.getText());

        // Recalculate the exercises done today in case of activity
        if(mTodayExercise == -1 && mTotalExercise == -1)
            calculateTodayAndTotal();
        else if(isUserActive(eventIndex)) {
            mTodayExercise += Constants.S_ELAPSED_PER_SAMPLE;
            mTotalExercise += Constants.S_ELAPSED_PER_SAMPLE;
        }

        // Update total and today's time counter
        setCustomHHmmss(mTodayTimeTV, mTodayExercise);
        setCustomHHmmss(mTotalTimeTV, mTotalExercise);
    }

    private void addPredictionToTodayRV(Prediction prediction) {
        int itemCount = mFastAdapter.getAdapterItemCount();

        // Get the last item in the list if there is one
        PredictionItem lastItem = itemCount > 0 ? mFastAdapter.getAdapterItem(itemCount - 1) : null;

        // If the last item exists and is the same time as the new prediction, modify the value
        if(lastItem != null && lastItem.getPredictionType() == prediction.getType()) {
            double newTotalTime = lastItem.getTotalTime() + Constants.S_ELAPSED_PER_SAMPLE;
            lastItem.addToTotalTime(Utils.getCustomHHmmssString(this, newTotalTime));
            mFastAdapter.notifyAdapterItemChanged(itemCount-1);
        } else {
            CharSequence totalTime = Utils.getCustomHHmmssString(this, Constants.S_ELAPSED_PER_SAMPLE);
            mFastAdapter.add(mPredictionItemProvider.get().withPrediction(prediction.getType(), totalTime));
        }
        if(mScrollS.isChecked())
            mTodaysActivitiesRV.smoothScrollToPosition(itemCount);
    }

    private void setCustomHHmmss(TextView tv, double timeMs){
        tv.setText(Utils.getCustomHHmmssString(this, (int) Math.floor(timeMs)));
    }

    // -------------------------- LISTENER ---------------------------

    /**
     * Sensor data capture every Xms
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Check the sensor is the one we monitor
        if(sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        // If we have enough samples, predict the activity, store in the DB and RESET the prediction
        if (mPrediction.getSamples().size() == Constants.SAMPLE_SIZE)
            predictAndStoreActivity();

        // TODO Review -> Can be done from mPrediction.getSamples().last()
        // Store the sensor values to update the UI
        sAllSensorData[0] = sensorEvent.values[0];
        sAllSensorData[1] = sensorEvent.values[1];
        sAllSensorData[2] = sensorEvent.values[2];

        // Add the prediction to the list of samples
        mPrediction.addSample(
                new Sample(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { /* Do nothing */ }

    /**
     * Google SignIn completed
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result from GoogleSignInApi
        if (requestCode == Constants.GOOGLE_AUTH)
            mAuthManager.handleSignInResult(data);
    }

    /**
     * Toolbar menu item clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // React depending on the item selected
        switch (item.getItemId()){

            // Handle sign IN and OUT
            case R.id.action_login:
                if(!mAuthManager.isLoggedFirebase()) {
                    FirebaseApp.initializeApp(this);    // FIXME check for Internet connection first
                    mAuthManager.init(this);            // TODO Run in background? Skipping frames...
                } else
                    mAuthManager.signOut();
                return true;

            // Delegate the export and config to ExportActivity
            case R.id.action_export:
                this.startActivity(new Intent(this, ExportActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
