package es.us.etsii.sensorflow.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import es.us.etsii.sensorflow.App;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(((App) getApplication()).getComponent());
    }

    /**
     * When implementing, call component.inject(this) for injecting dependencies on the Activity.
     */
    protected abstract void inject(App.AppComponent component);
}
