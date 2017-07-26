package es.us.etsii.sensorflow;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

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
        return new Sensor[]{
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        };
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
}