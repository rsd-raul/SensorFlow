package es.us.etsii.sensorflow.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.domain.Config;
import es.us.etsii.sensorflow.managers.RealmManager;
import es.us.etsii.sensorflow.utils.CSVUtils;
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
    @Inject Lazy<RealmManager> mRealmManagerLazy;
    private String mFileName = null;
    private List<String> mFilesInFolder;
    private Config exportConfig = new Config();

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

        // Setup a simple list with the files at the default location and a click listener
        setupFolderContent();
        mFolderContentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    mFileName = mFilesInFolder.get(pos).replace(".csv", "");
                    mFileNameET.setText(mFileName);
                }
            });
    }

    // --------------------------- STATES ----------------------------

    // -------------------------- USE CASES --------------------------

    @SuppressWarnings("ResourceType")
    @OnClick(R.id.saveFAB)
    public void checkAndExportCSV(){
        // Check if we have permissions to access external storage
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_missing_sd_write, Toast.LENGTH_SHORT).show();
            return;
        }

        mFileName = mFileNameET.getText().toString();
        exportConfig.setConflictIndex(mConflictModeSP.getSelectedItemPosition());   // ResType ignore
        if(mFilesInFolder.contains(mFileName+".csv") && exportConfig.getConflictIndex() == Constants.WARN)
            DialogUtils.waringDialog(this);
        else
            exportToCSV();
    }

    /**
     * Complete the info required to by the CSV export task and call it
     */
    public void exportToCSV() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String folderPath = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);
        String fullFilePath = folderPath + File.separator + mFileName + ".csv";
        exportConfig.setFullFilePath(fullFilePath);

        String maxSamplesStr = mMaxSamplesET.getText().toString();
        try {
            int maxSamples = maxSamplesStr.length() > 0 ? Integer.parseInt(maxSamplesStr) : 0;
            exportConfig.setMaxSamples(maxSamples);
            new CSVExportTask().execute();
        }catch (Exception ex){
            Toast.makeText(this, Integer.MAX_VALUE + " " + getString(R.string.max_samples),
                                                                        Toast.LENGTH_SHORT).show();
        }
        // Hide the keyboard in case it's showing
        hideKeyboard();
    }

    @OnClick(R.id.tv_folder_name)
    public void chooseFolderPath(){
        DialogUtils.folderPickerDialog(this);
    }

    @OnClick(R.id.tv_from_date)
    public void fromDatePicker(){
        DialogUtils.buildDateTimePicker(Constants.FROM_PICKER, exportConfig.getFromDate(), this);
    }

    @OnClick(R.id.tv_to_date)
    public void toDatePicker(){
        DialogUtils.buildDateTimePicker(Constants.TO_PICKER, exportConfig.getToDate(), this);
    }

    // ------------------------- AUXILIARY ---------------------------

    // -------------------------- INTERFACE --------------------------

    @OnClick(R.id.iv_remove_from_date)
    public void fromDateRemove(){
        exportConfig.setFromDate(0);
        mFromDateTV.setText(R.string.no_date_set);
        mRemoveFromDateIV.setVisibility(View.GONE);
        mMaxSamplesET.setEnabled(true);
    }
    @OnClick(R.id.iv_remove_to_date)
    public void toDateRemove(){
        exportConfig.setToDate(0);
        mToDateTV.setText(R.string.no_date_set);
        mRemoveToDateIV.setVisibility(View.GONE);
        mMaxSamplesET.setEnabled(true);
    }

    @SuppressLint("SetTextI18n")    // Internationalization not needed here
    @OnClick(R.id.iv_refresh_name)
    public void refreshName(){
        mFileNameET.setText(Constants.CSV_FILE_PREFIX + System.currentTimeMillis());
    }

    @OnClick(R.id.iv_refresh_folder)
    public void setupFolderContent() {
        // Get the folder path form preferences and update the UI field
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);
        mFolderNameTV.setText(path);

        // Get all the files and save them on the adapter, make sure we have +1 or notify the user
        File[] files = new File(path).listFiles();
        mFilesInFolder = new ArrayList<>();
        if(files != null)
            for (int i = files.length-1; i >= 0; i--)
                mFilesInFolder.add(files[i].getName());
        else
            mFilesInFolder.add(getString(R.string.no_files_in_directory));

        mFolderContentLV.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFilesInFolder));
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view == null)
            return;

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        setupFolderContent();
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
                exportConfig.setFromDate(dateLong);
                mFromDateTV.setText(Utils.longToDateString(this, dateLong));
                mRemoveFromDateIV.setVisibility(View.VISIBLE);
                break;
            case Constants.TO_PICKER:
                exportConfig.setToDate(dateLong);
                mToDateTV.setText(Utils.longToDateString(this, dateLong));
                mRemoveToDateIV.setVisibility(View.VISIBLE);
                break;
        }

        // Control the case when ToDate is before FromDate -> Change values
        if(exportConfig.getFromDate() > exportConfig.getToDate() && exportConfig.getToDate() != 0) {
            long aux1 = exportConfig.getFromDate();
            exportConfig.setFromDate(exportConfig.getToDate());
            exportConfig.setToDate(aux1);

            CharSequence aux2 = mFromDateTV.getText();
            mFromDateTV.setText(mToDateTV.getText());
            mToDateTV.setText(aux2);
        }

        // Disable the max samples if we are using a range
        if(exportConfig.getFromDate() > 0 && exportConfig.getToDate() > 0)
            mMaxSamplesET.setEnabled(false);
    }
    @Override
    public void onCancelled() { }

    /**
     * Warning dialog callback (title conflict)
     */
    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        switch (which){
            case NEUTRAL:
                exportConfig.setConflictIndex(Constants.APPEND);
                exportToCSV();
                break;
            case POSITIVE:
                exportConfig.setConflictIndex(Constants.OVERRIDE);
                exportToCSV();
                break;
            case NEGATIVE:
                break;
        }
    }

    private class CSVExportTask extends AsyncTask<Void, Void, Integer> {

        // ------------------------- CONSTRUCTOR -------------------------

        CSVExportTask() { }

        // -------------------------- USE CASES --------------------------

        @Override
        protected Integer doInBackground(Void... voids) {
            return CSVUtils.exportToCSV(exportConfig, mRealmManagerLazy.get());
        }

        @Override
        protected void onPostExecute(Integer logRes) {
            super.onPostExecute(logRes);

            if(logRes == R.string.samples_stored){
                ExportActivity.this.setupFolderContent();
                ExportActivity.this.refreshName();
            }

            Snackbar.make(mSaveFAB, logRes, Snackbar.LENGTH_SHORT).show();
//            Toast.makeText(ExportActivity.this, logRes, Toast.LENGTH_SHORT).show();
        }

        // ------------------------- AUXILIARY ---------------------------


    }
}
