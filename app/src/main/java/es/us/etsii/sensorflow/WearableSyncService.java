package es.us.etsii.sensorflow;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import es.us.etsii.sensorflow.domain.Sample;
import es.us.etsii.sensorflow.utils.CommunicationUtils;
import es.us.etsii.sensorflow.utils.Constants;

public class WearableSyncService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if(dataEvent.getType() != DataEvent.TYPE_CHANGED)
                continue;


            String path = dataEvent.getDataItem().getUri().getPath();

            switch (path){
                case Constants.SAMPLES_PATH:
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();

                    Sample[] samples = CommunicationUtils.getSamplesBatchFromDataMap(dataMap);
                    // TODO store the samples
                    break;
            }
        }
    }
}
