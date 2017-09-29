package es.us.etsii.sensorflow;

import android.app.Application;
import javax.inject.Singleton;
import dagger.Component;
import es.us.etsii.sensorflow.utils.PrimaryKeyFactory;
import es.us.etsii.sensorflow.views.ExportActivity;
import es.us.etsii.sensorflow.views.MainActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    // ------------------------- ATTRIBUTES --------------------------

    private AppComponent appComponent;

    // -------------------------- INTERFACE --------------------------

    @Singleton
    @Component(modules = AppModule.class)
    public interface AppComponent {
        void inject(App application);
        void inject(MainActivity mainActivity);
        void inject(ExportActivity exportActivity);
    }

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApp_AppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);

        // Set the RealmConfiguration and PrimaryKeyFactory for Realm usage
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(0)
//                .migration(new MigrationHelper())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        PrimaryKeyFactory.initialize(Realm.getDefaultInstance());
    }

    public AppComponent getComponent() {
        return appComponent;
    }

}