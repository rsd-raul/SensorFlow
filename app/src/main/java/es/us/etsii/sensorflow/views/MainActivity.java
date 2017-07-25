package es.us.etsii.sensorflow.views;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.TensorFlowClassifier;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;

public class MainActivity extends BaseActivity implements SensorEventListener {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MainActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.startAndStopFAB) FloatingActionButton startAndStopFAB;
    @BindView(R.id.iv_current_activity) ImageView currentActivityIV;
    @BindView(R.id.tv_current_activity) TextView currentActivityTV;
    @Inject SensorManager mSensorManager;
    @Inject Sensor[] mCriticalSensors;
    @BindViews({ R.id.tv_bar_x, R.id.tv_bar_y, R.id.tv_bar_z, R.id.tv_ace_x, R.id.tv_ace_y,
            R.id.tv_ace_z, R.id.tv_gyro_x, R.id.tv_gyro_y, R.id.tv_gyro_z, R.id.tv_mag_u})
    List<TextView> allSensorViews;
    private static float[] allSensorData = new float[10];
    private boolean RUNNING = false, WAS_RUNNING = false;
    @Inject TensorFlowClassifier classifier;

    private static List<Float> x = new ArrayList<>(), y = new ArrayList<>(), z = new ArrayList<>();

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    protected void inject(App.AppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    // --------------------------- STATES ----------------------------

    @Override
    protected void onResume() {
        super.onResume();

        if(!WAS_RUNNING)
            return;

        // If the service was running, restart it and star the UI updater
        registerSensorListener();
        updateSensorValuesUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

        WAS_RUNNING = RUNNING;
        RUNNING = false;
    }

    // ------------------------- AUXILIARY ---------------------------

    private float[] mergeAndFormatData(){
        List<Float> data = new ArrayList<>();
        data.addAll(x);
        data.addAll(y);
        data.addAll(z);

        float[] array = new float[data.size()];
        for (int i = 0; i < data.size(); i++)
            array[i] = data.get(i) != null ? data.get(i) : Float.NaN;
        return array;
    }

    // -------------------------- USE CASES --------------------------

    private void registerSensorListener(){
        for(Sensor sensor : mCriticalSensors) {
            // Check existence, despite the Manifest requirements, a user can side-load the apk
            if(sensor == null)
                DialogUtils.criticalErrorDialog(this, R.string.sensor_missing, R.string.sensor_missing_description);
            else
                mSensorManager.registerListener(this, sensor, Constants.SAMPLING_PERIOD_US);
        }
    }

    /**
     * Once every UI_REFRESH_RATE_MS update all sensor values with the data stored in allSensorData.
     */
    private void updateSensorValuesUI() {
        RUNNING = true;

        // Using a handler to create a recurrent task
        final Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                // Apply the same action to all the views
                ButterKnife.apply(allSensorViews, UPDATE);

                if(RUNNING)
                    h.postDelayed(this, Constants.UI_REFRESH_RATE_MS);
            }
        }, Constants.UI_REFRESH_RATE_MS);
    }

    @OnClick(R.id.startAndStopFAB)
    void clickRunAndStop(){
        int dra, col;

        // Toggle run and stop
        RUNNING = !RUNNING;

        // Depending on the action required, stop or start the service (and customize FAB)
        if(RUNNING) {
            dra = R.drawable.ic_stop_24dp;
            col = R.color.redDark;

            // Start service and UI updater
            registerSensorListener();
            updateSensorValuesUI();
        } else {
            dra = R.drawable.ic_play_24dp;
            col = R.color.tealDark;

            // Stop service
            mSensorManager.unregisterListener(this);
        }

        // Change colors and function
        startAndStopFAB.setImageResource(dra);
        startAndStopFAB.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, col));

        // Animate changes
        ScaleAnimation expandAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expandAnimation.setDuration(150);
        expandAnimation.setInterpolator(new AccelerateInterpolator());
        startAndStopFAB.startAnimation(expandAnimation);
    }

    private void predictActivity() {
        float[] results = classifier.predictProbabilities(mergeAndFormatData());

        // Extract from the results the index of the most probable one
        int index = 0;
        float higher = Float.MIN_VALUE;
        for (int i = 0; i < results.length; i++) {
            if(results[i] > higher){
                higher = results[i];
                index = i;
            }
        }

        // Set the activity with a higher probability
        currentActivityIV.setImageResource(Constants.ACTIVITY_IMAGES[index]);
        currentActivityTV.setText(Constants.ACTIVITY_NAMES[index]);
        currentActivityIV.setContentDescription(currentActivityTV.getText());

        // Overlap the samples by the OVERLAPPING_PERCENTAGE set on Constants
        x = x.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);
        y = y.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);
        z = z.subList(Constants.OVERLAP_FROM_INDEX, Constants.SAMPLE_SIZE);
    }

    // -------------------------- INTERFACE --------------------------

    private final ButterKnife.Action<TextView> UPDATE = new ButterKnife.Action<TextView>() {
        @Override public void apply(@NonNull TextView view, int index) {
            view.setText(String.valueOf(allSensorData[index]));
        }
    };

    // -------------------------- LISTENER ---------------------------

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_PRESSURE:
                allSensorData[0] = sensorEvent.values[0];
                allSensorData[1] = sensorEvent.values[1];
                allSensorData[2] = sensorEvent.values[2];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                allSensorData[3] = sensorEvent.values[0];
                allSensorData[4] = sensorEvent.values[1];
                allSensorData[5] = sensorEvent.values[2];

                if (x.size() == Constants.SAMPLE_SIZE)
                    predictActivity();

                x.add(sensorEvent.values[0]);
                y.add(sensorEvent.values[1]);
                z.add(sensorEvent.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                allSensorData[6] = sensorEvent.values[0];
                allSensorData[7] = sensorEvent.values[1];
                allSensorData[8] = sensorEvent.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                allSensorData[9] = sensorEvent.values[0];
                break;
            default:
                Log.e(TAG, "Unsupervised sensor change");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { /* Do nothing */ }
}
