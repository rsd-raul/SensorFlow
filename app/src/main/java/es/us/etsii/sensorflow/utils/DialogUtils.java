package es.us.etsii.sensorflow.utils;

import android.content.SharedPreferences;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;

import java.util.Calendar;
import java.util.Date;

import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.views.ExportActivity;

public abstract class DialogUtils {

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

    public static void exportDialog(@NonNull AppCompatActivity activity) {
        new MaterialDialog.Builder(activity)
                .title("Choose export mode")
                .content("empty")
                .negativeText(R.string.close_app)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Process.killProcess(Process.myPid());
                    }
                })
                .show();
    }

    public static void folderPickerDialog(ExportActivity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String path = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);

        new FolderChooserDialog.Builder(activity)
                .initialPath(path)  // Changes initial path
                .allowNewFolder(true, 0)
                .show();
    }

    private static SublimeOptions getDateTimePicker(SublimeOptions.Picker picker){
        return getBaseOptionsSublime(picker, true, true, false, true);
    }

    private static SublimeOptions getBaseOptionsSublime(SublimeOptions.Picker picker,
                                                        boolean date, boolean time,
                                                        boolean repeat, boolean range){

        SublimeOptions options = new SublimeOptions();

        // Select the start screen
        options.setPickerToShow(picker);

        // Select the extras that will be available
        int displayOptions = 0;
        if (date)
            displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        if (time)
            displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        if (repeat)
            displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
        options.setCanPickDateRange(range);

        options.setDisplayOptions(displayOptions);

        // If 'displayOptions' is zero, the chosen options are not valid
        if(displayOptions == 0)
            throw new UnsupportedOperationException("Display options empty");

        return options;
    }

    private static SublimeOptions customizeOptionsSublime(SublimeOptions options, Date date, boolean is24){
        // Example for setting date range:
        // Note that you can pass a date range as the initial date params
        // even if you have date-range selection disabled. In this case,
        // the user WILL be able to change date-range using the header
        // TextViews, but not using long-press.

        /*Calendar startCal = Calendar.getInstance();
        startCal.set(2016, 2, 4);
        Calendar endCal = Calendar.getInstance();
        endCal.set(2016, 2, 17);
        options.setDateParams(startCal, endCal);*/


        // Initialize the calendar and if the task is valid, set the calendar to that value
        Calendar cal = Calendar.getInstance();
        if(date != null)
            cal.setTime(date);

        // Set the default or the selected date
        options.setDateParams(cal);
        options.setTimeParams(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), is24);

        return options;
    }


}
