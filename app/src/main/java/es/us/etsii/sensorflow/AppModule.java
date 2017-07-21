package es.us.etsii.sensorflow;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
}