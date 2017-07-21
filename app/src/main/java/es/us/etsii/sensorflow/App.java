package es.us.etsii.sensorflow;


import android.app.Application;
import javax.inject.Singleton;
import dagger.Component;
import es.us.etsii.sensorflow.views.MainActivity;

public class App extends Application {

    // ------------------------- ATTRIBUTES --------------------------

    private AppComponent appComponent;

    // -------------------------- INTERFACE --------------------------

    @Singleton
    @Component(modules = AppModule.class)
    public interface AppComponent {
        void inject(App application);
        void inject(MainActivity mainActivity);
    }

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApp_AppComponent
                .builder()
                .appModule(
                        new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    public AppComponent getComponent() {
        return appComponent;
    }

}