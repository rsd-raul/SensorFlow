package es.us.etsii.sensorflow.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;
import es.us.etsii.sensorflow.utils.Utils;

public class ExportActivity extends BaseActivity implements FolderChooserDialog.FolderCallback,
                                                            SublimePickerFragment.SublimeCallback,
                                                            MaterialDialog.SingleButtonCallback {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "ExportActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.lv_folder_content) ListView mFolderContentLV;
    @BindView(R.id.et_file_name) EditText mFileNameET;
    @BindView(R.id.tv_folder_name) TextView mFolderNameTV;
    @BindView(R.id.tv_from_date) TextView mFromDateTV;
    @BindView(R.id.tv_to_date) TextView mToDateTV;
    @BindView(R.id.et_max_samples) EditText mMaxSamplesET;
    @BindView(R.id.iv_remove_to_date) ImageView mRemoveToDateIV;
    @BindView(R.id.iv_remove_from_date) ImageView mRemoveFromDateIV;
    @BindView(R.id.s_conflict_mode) Spinner mConflictModeSP;
    @BindView(R.id.saveFAB) FloatingActionButton mSaveFAB;
    private ArrayAdapter<String> mFolderContentAdapter;
    private long mFromDate = 0, mToDate = 0, mMaxSamples = 0;
    private final int ALL = 1, ALL_WITH_MAX = 2, FROM_DATE = 3, WITH_RANGE = 4, MALFORMED = 5;
    private List<String> mFiles = new ArrayList<>();

    // ------------------------- CONSTRUCTOR -------------------------

    @Override
    protected void inject(App.AppComponent component) {
        component.inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup activity and bind views
        setContentView(R.layout.activity_export);
        ButterKnife.bind(this);
        refreshName();

        // Setup a simple list with the files at the default location
        setupFolderContent(false);
    }

    // --------------------------- STATES ----------------------------

    // -------------------------- USE CASES --------------------------

    @OnClick(R.id.saveFAB)
    public void checkAndExportCSV(){
        // Check if we have permissions to access external storage
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_missing_sd_write, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (classifyCSVExport()){
            case ALL:
                break;
            case ALL_WITH_MAX:
                break;
            case FROM_DATE:
                break;
            case WITH_RANGE:
                break;
            case MALFORMED:
                Toast.makeText(this, R.string.incorrect_settings, Toast.LENGTH_SHORT).show();
                return;
            default:
                Log.e(TAG, "exportToCSV: Combination not supported: " + mFromDate + " " + mToDate +
                        " " + mMaxSamplesET.getText().toString());
                return;
        }

        String fileName = mFileNameET.getText().toString();
        if(mFiles.contains(fileName) && mConflictModeSP.getSelectedItemPosition() == Constants.WARN)
            DialogUtils.waringDialog(this);
        else
            exportToCSV();
    }

    public void exportToCSV() {
        //
    }

    @OnClick(R.id.tv_folder_name)
    public void chooseFolderPath(){
        DialogUtils.folderPickerDialog(this);
    }

    @OnClick(R.id.tv_from_date)
    public void fromDatePicker(){
        DialogUtils.buildDateTimePicker(Constants.FROM_PICKER, mFromDate, this);
    }

    @OnClick(R.id.tv_to_date)
    public void toDatePicker(){
        DialogUtils.buildDateTimePicker(Constants.TO_PICKER, mToDate, this);
    }

    @OnClick(R.id.iv_remove_from_date)
    public void fromDateRemove(){
        mFromDate = 0;
        mFromDateTV.setText(R.string.no_date_set);
        mRemoveFromDateIV.setVisibility(View.GONE);
    }
    @OnClick(R.id.iv_remove_to_date)
    public void toDateRemove(){
        mToDate = 0;
        mToDateTV.setText(R.string.no_date_set);
        mRemoveToDateIV.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")    // Internationalization not needed here
    @OnClick(R.id.iv_refresh_name)
    public void refreshName(){
        mFileNameET.setText(Constants.CSV_FILE_PREFIX + System.currentTimeMillis());
    }

    // ------------------------- AUXILIARY ---------------------------

    private int classifyCSVExport() {
        try {
            mMaxSamples = Integer.parseInt(mMaxSamplesET.getText().toString());
        } catch (Exception ex){
            return MALFORMED;
        }

        if(mFromDate == 0)
            if (mToDate == 0)
                if (mMaxSamples > 0)
                    return ALL_WITH_MAX;        // Only MaxSamples set
                else
                    return ALL;                 // Nothing set
            else
                return MALFORMED;               // ToDate without FromDate
        else
            if(mToDate == 0)
                return FROM_DATE;               // Only FromDateSet
            else
                return WITH_RANGE;              // Both FromDate and ToDate set
    }

    // -------------------------- INTERFACE --------------------------

    private void setupFolderContent(boolean updateOnly) {
        // Get the folder path form preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);

        mFolderNameTV.setText(path);

        // Populate the adapter and setup the ListView
        if(!updateOnly)
            mFolderContentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        else
            mFolderContentAdapter.clear();

        File[] files = new File(path).listFiles();

        if(files != null)
            for (File file : files)
                mFiles.add(file.getName());
        else
            mFiles.add(getString(R.string.no_files_in_directory));

        mFolderContentAdapter.addAll(mFiles);

        if(!updateOnly)
            mFolderContentLV.setAdapter(mFolderContentAdapter);
    }

    // -------------------------- LISTENER ---------------------------

    /**
     * Folder picker dialog -> Folder selected
     */
    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        // Update the preferred location
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(Constants.CSV_FOLDER, folder.getAbsolutePath()).apply();

        // Setup UI
        setupFolderContent(true);
    }
    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) { }

    /**
     * Date and time picker callback
     */
    @Override
    public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, int id) {
        Calendar date = selectedDate.getFirstDate();
        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
        date.set(Calendar.MINUTE, minute);

        long dateLong = date.getTimeInMillis();
        switch (id){
            case Constants.FROM_PICKER:
                mFromDate = dateLong;
                mFromDateTV.setText(Utils.longToDateString(this, dateLong));
                mRemoveFromDateIV.setVisibility(View.VISIBLE);
                break;
            case Constants.TO_PICKER:
                mToDate = dateLong;
                mToDateTV.setText(Utils.longToDateString(this, dateLong));
                mRemoveToDateIV.setVisibility(View.VISIBLE);
                break;
        }

        // Control the case when ToDate is before FromDate -> Change values
        if(mFromDate > mToDate && mToDate != 0) {
            long aux1 = mFromDate;
            mFromDate = mToDate;
            mToDate = aux1;

            CharSequence aux2 = mFromDateTV.getText();
            mFromDateTV.setText(mToDateTV.getText());
            mToDateTV.setText(aux2);
        }
    }
    @Override
    public void onCancelled() { }

    /**
     * Warning dialog callback (title conflict)
     */
    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        Toast.makeText(this, "PENDING", Toast.LENGTH_SHORT).show();
    }
}
