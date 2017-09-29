package es.us.etsii.sensorflow.wear;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import java.util.List;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DataUtils;

public abstract class WearSyncManager {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "WearSyncManager";

    // -------------------------- USE CASES --------------------------

    public static void sendCurrentPrediction(String nodeId, int predictionIndex,
                                             GoogleApiClient googleApiClient){

        Wearable.MessageApi.sendMessage(googleApiClient, nodeId, Constants.PREDICTION_PATH,
                DataUtils.getByteArrayFromInt(predictionIndex))
                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        // TODO - Wear - retry until a new prediction is generated or X seconds have passed?
                        if(sendMessageResult.getStatus().isSuccess())
                            Log.d(TAG, "onResult: Current prediction successfully sent");
                        else
                            Log.e(TAG, "onResult: Current prediction failed to be sent");
                    }
                });
    }

    public static void sendCurrentAppConfiguration(GoogleApiClient googleApiClient){
        PutDataRequest putDataRequest = DataUtils.generateDataRequestFromConstants();

        Wearable.DataApi.putDataItem(googleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        // TODO - Wear - retry?
                        if(dataItemResult.getStatus().isSuccess())
                            Log.d(TAG, "onResult: Current configuration successfully stored to send");
                        else
                            Log.e(TAG, "onResult: Current configuration failed to be stored to send");
                    }
                });
    }

    // -------------------------- AUXILIARY --------------------------

    // NOT on UI THREAD
    public static List<Node> getConnectedNodes(GoogleApiClient googleApiClient){
        return Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();
    }
}
