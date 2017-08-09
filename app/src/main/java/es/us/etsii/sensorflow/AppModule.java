package es.us.etsii.sensorflow;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import es.us.etsii.sensorflow.views.PredictionItem;
import io.realm.Realm;

@Module
class AppModule {

    private Application app;

    AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context contextProvider() {
        return app;
    }

    @Provides
    SensorManager sensorManagerProvider(){
        return (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides
    Sensor[] criticalSensorsProvider(SensorManager sensorManager){
        return new Sensor[]{ sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) };
    }

    @Provides
    FirebaseAuth firebaseAuthProvider(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    FusedLocationProviderClient fusedLocationProviderClientProducer(Context context){
        // FIXME check for Google Play Services first
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    Realm realmProvider(){
        return Realm.getDefaultInstance();
    }

    @Provides
    PredictionItem predictionItemProvider(){
        return new PredictionItem();
    }

    @Provides
    FastItemAdapter<PredictionItem> fastItemAdapterProvider(){
        return new FastItemAdapter<>();
    }
}