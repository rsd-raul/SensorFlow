package com.raul.rsd.android.sensorflow.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.raul.rsd.android.sensorflow.R;
import com.raul.rsd.android.sensorflow.utils.Constants;
import com.raul.rsd.android.sensorflow.utils.DataUtils;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener,
                                                        MessageApi.MessageListener {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MainActivity";

    // ------------------------- ATTRIBUTES --------------------------

    private TextView mCurrentActivity;
    private GoogleApiClient mGoogleApiClient;

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mCurrentActivity = findViewById(R.id.tv_current_activity);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    // -------------------------- INTERFACE --------------------------

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }
    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }
    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    // Update values?
    private void updateDisplay() {
//        if (isAmbient()) {
//            // Apply a dark theme programmatically
//        } else {
//            // Apply a light theme programmatically
//        }
    }


    // -------------------------- LISTENER ---------------------------

    /**
     * Receives a message with the latest prediction obtained form the phone.
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        switch (messageEvent.getPath()){
            // Prediction received from the mobile phone
            case  Constants.PREDICTION_PATH:
                int activityIndex = DataUtils.getIntFromByteArray(messageEvent.getData());
                mCurrentActivity.setText(DataUtils.getActivityNameResFromIndex(activityIndex));
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }


}
