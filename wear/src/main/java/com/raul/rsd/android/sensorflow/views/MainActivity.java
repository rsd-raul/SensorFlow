package com.raul.rsd.android.sensorflow.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.raul.rsd.android.sensorflow.R;
import com.raul.rsd.android.sensorflow.domain.Sample;
import com.raul.rsd.android.sensorflow.utils.CommunicationUtils;
import com.raul.rsd.android.sensorflow.utils.Constants;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
                                                      GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private TextView mCurrentActivity;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mCurrentActivity = (TextView) findViewById(R.id.tv_current_activity);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sendSampleDataBatch(Sample[] samples){
        PutDataRequest putDataRequest = CommunicationUtils.getDataRequestFromSamplesBatch(samples);

        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        // TODO retry?
                        if(dataItemResult.getStatus().isSuccess())
                            Log.d(TAG, "onResult: Samples batch successfully stored to send");
                        else
                            Log.e(TAG, "onResult: Samples batch failed to be stored to send");
                    }
                });
    }
}
