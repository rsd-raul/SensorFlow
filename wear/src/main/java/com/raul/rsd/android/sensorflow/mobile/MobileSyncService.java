package com.raul.rsd.android.sensorflow.mobile;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.raul.rsd.android.sensorflow.utils.Constants;
import com.raul.rsd.android.sensorflow.utils.DataUtils;

public class MobileSyncService extends WearableListenerService {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "MobileSyncService";

    // -------------------------- LISTENER ---------------------------

    /**
     * Receives the config for the Sensor extraction algorithm if the mobile app changes
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if(dataEvent.getType() != DataEvent.TYPE_CHANGED)
                continue;

            switch (dataEvent.getDataItem().getUri().getPath()){
                // The mobile app configuration has changed, setup the new one
                case Constants.CONFIGURATION_PATH:
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    DataUtils.setupPhoneConfigFromDataMap(dataMap);
                    break;
            }
        }
    }

//          DONE on MAIN ACTIVITY as it's an ACTIVITY RELATED task
//    /**
//     * Receives a message with the latest prediction obtained form the phone.
//     */
//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        switch (messageEvent.getPath()){
//            // Prediction received from the mobile phone
//            case  Constants.PREDICTION_PATH:
//                int currentIndex = DataUtils.getIntFromByteArray(messageEvent.getData());
//                break;
//        }
//    }
}
