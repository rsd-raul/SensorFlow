package es.us.etsii.sensorflow.wear;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.utils.DataUtils;
import es.us.etsii.sensorflow.utils.Constants;

public class WearSyncService extends WearableListenerService {

    // -------------------------- LISTENER ---------------------------

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if(dataEvent.getType() != DataEvent.TYPE_CHANGED)
                continue;

            String path = dataEvent.getDataItem().getUri().getPath();

            switch (path){
                case Constants.SAMPLES_PATH:
                    Log.e("WearSyncService", "onDataChanged: GETTING SAMPLES FROM WEAR");

                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                    Sample[] samples = DataUtils.getSamplesBatchFromDataMap(dataMap);
                    // TODO - Wear - store the samples on Realm
                    break;
            }
        }
    }
}
