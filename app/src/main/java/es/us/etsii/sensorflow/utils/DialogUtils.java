package es.us.etsii.sensorflow.utils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import java.util.Calendar;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.views.ExportActivity;
import es.us.etsii.sensorflow.views.SublimePickerFragment;

public abstract class DialogUtils {

    public static void waringDialog(ExportActivity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.file_name_conflict)
                .content(R.string.file_name_conflict_desc)
                .positiveText(R.string.override)
                .onPositive(activity)
                .neutralText(R.string.append)
                .onNeutral(activity)
                .negativeText(R.string.cancel)
                .onNegative(activity)
                .show();
    }

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

    public static void folderPickerDialog(ExportActivity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String path = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);

        new FolderChooserDialog.Builder(activity)
                .initialPath(path)  // Changes initial path
                .allowNewFolder(true, 0)
                .show();
    }

    public static void buildDateTimePicker(int id, long initialDate, ExportActivity activity){
        // Get a Date & Time picker starting in time and without range
        SublimeOptions options = getBaseOptionsSublime(SublimeOptions.Picker.TIME_PICKER,
                true, true, false, true);

        // Based on the user Locale, get the format
        boolean is24 = DateFormat.is24HourFormat(activity);

        // Set the date if any, or default
        options = customizeOptionsSublime(options, is24, initialDate);

        // Build the final picker
        buildSublimePicker(id, options, activity);
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

    private static SublimeOptions customizeOptionsSublime(SublimeOptions options, boolean is24,
                                                          long initialDate){
        // Initialize the picker to the date selected
        Calendar fromDate = Calendar.getInstance();
        if(initialDate > 0)
            fromDate.setTimeInMillis(initialDate);
        options.setDateParams(fromDate);

        // Set the default or the selected date
        options.setTimeParams(fromDate.get(Calendar.HOUR_OF_DAY), fromDate.get(Calendar.MINUTE), is24);

        return options;
    }

    private static void buildSublimePicker(int id, SublimeOptions options, ExportActivity activity){
        SublimePickerFragment pickerFrag = new SublimePickerFragment().withCallbackAndId(id, activity);

        // Valid options
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.SUBLIME_OPTIONS, options);
        pickerFrag.setArguments(bundle);

        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        pickerFrag.show(activity.getSupportFragmentManager(), Constants.SUBLIME_PICKER);
    }
}
