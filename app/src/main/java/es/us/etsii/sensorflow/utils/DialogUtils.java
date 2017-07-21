package es.us.etsii.sensorflow.utils;

import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import es.us.etsii.sensorflow.R;

public class DialogUtils {

    public static void criticalErrorDialog(@NonNull AppCompatActivity activity, int title, int content) {
        new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .negativeText(R.string.close_app)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Process.killProcess(Process.myPid());
                    }
                })
                .show();
    }
}
