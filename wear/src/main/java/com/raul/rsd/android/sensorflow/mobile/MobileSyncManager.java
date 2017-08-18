package com.raul.rsd.android.sensorflow.mobile;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.raul.rsd.android.sensorflow.domain.Sample;
import com.raul.rsd.android.sensorflow.utils.DataUtils;

public abstract class MobileSyncManager {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MobileSyncManager";

    // -------------------------- USE CASES --------------------------

    public static void sendSampleDataBatch(GoogleApiClient googleApiClient, Sample[] samples){
        PutDataRequest putDataRequest = DataUtils.getDataRequestFromSamplesBatch(samples);

        Wearable.DataApi.putDataItem(googleApiClient, putDataRequest)
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
