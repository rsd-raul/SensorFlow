package es.us.etsii.sensorflow.views;

import android.os.Bundle;

import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void inject(App.AppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
