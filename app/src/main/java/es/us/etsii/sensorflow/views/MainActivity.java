package es.us.etsii.sensorflow.views;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;

public class MainActivity extends BaseActivity implements SensorEventListener {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MainActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.tester) TextView testTextView;
    @Inject SensorManager mSensorManager;
    @Inject Sensor[] mCriticalSensors;

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
        int frequency = 20 * Constants.MS2US;

        for(Sensor sensor : mCriticalSensors) {
            // Check existence, despite the Manifest requirements, a user can sideload the apk
            if(sensor == null)
                DialogUtils.criticalErrorDialog(this, R.string.sensor_missing,R.string.sensor_missing_description);
            else
                mSensorManager.registerListener(this, sensor, frequency);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // ------------------------- AUXILIARY ---------------------------

    // -------------------------- USE CASES --------------------------

    // -------------------------- LISTENER ---------------------------

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_GYROSCOPE:
                Log.e(TAG, "Gyroscope X: " + sensorEvent.values[0]);
                Log.e(TAG, "Gyroscope Y: " + sensorEvent.values[1]);
                Log.e(TAG, "Gyroscope Z: " + sensorEvent.values[2]);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                Log.e(TAG, "Accelerometer X: " + sensorEvent.values[0]);
                Log.e(TAG, "Accelerometer Y: " + sensorEvent.values[1]);
                Log.e(TAG, "Accelerometer Z: " + sensorEvent.values[2]);
                break;
            case Sensor.TYPE_PRESSURE:
                Log.e(TAG, "Barometer X: " + sensorEvent.values[0]);
                Log.e(TAG, "Barometer Y: " + sensorEvent.values[1]);
                Log.e(TAG, "Barometer Z: " + sensorEvent.values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                Log.e(TAG, "Magnetometer: " + sensorEvent.values[0]);
                break;
            default:
                Log.e(TAG, "Unsupervised sensor change");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { /* Do nothing */ }
}
