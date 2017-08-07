package es.us.etsii.sensorflow.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.etsii.sensorflow.App;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;
import es.us.etsii.sensorflow.utils.DialogUtils;

public class ExportActivity extends BaseActivity implements FolderChooserDialog.FolderCallback {

    // --------------------------- VALUES ----------------------------

    private static final String TAG = "ExportActivity";

    // ------------------------- ATTRIBUTES --------------------------

    @BindView(R.id.lv_folder_content) ListView mFolderContentLV;
    @BindView(R.id.et_file_name) EditText mFileNameET;
    @BindView(R.id.tv_folder_name) TextView mFolderNameTV;
    @BindView(R.id.tv_from_date) TextView mFromDateTV;
    @BindView(R.id.tv_to_date) TextView mToDateTV;
    @BindView(R.id.et_max_samples) EditText mMaxSamplesET;
    @BindView(R.id.sw_settings) Switch mSettingsSW;
    @BindView(R.id.iv_remove_to_date) ImageView mRemoveToDateIV;
    @BindView(R.id.iv_remove_from_date) ImageView mRemoveFromDateIV;
    @BindView(R.id.s_conflict_mode) Spinner mConflictModeSP;
    @BindView(R.id.saveFAB) FloatingActionButton mSaveFAB;
    private ArrayAdapter<String> mFolderContentAdapter;

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

        // Setup a simple list with the files at the default location
        setupFolderContent(false);
    }

    // --------------------------- STATES ----------------------------

    // -------------------------- USE CASES --------------------------

    @OnClick(R.id.tv_folder_name)
    public void chooseFolderPath(){
        DialogUtils.folderPickerDialog(this);
    }

    @OnClick(R.id.tv_from_date)
    public void fromDatePicker(){

    }

    @OnClick(R.id.tv_to_date)
    public void toDatePicker(){

    }

    // ------------------------- AUXILIARY ---------------------------

    // -------------------------- INTERFACE --------------------------

    private void setupFolderContent(boolean updateOnly) {
        // Get the folder path form preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPreferences.getString(Constants.CSV_FOLDER, Constants.CSV_FOLDER_ROUTE);

        // Populate the adapter and setup the ListView
        if(!updateOnly)
            mFolderContentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        else
            mFolderContentAdapter.clear();

        File[] files = new File(path).listFiles();
        if(files != null)
            for (File file : files)
                mFolderContentAdapter.add(file.getName());
        else
            mFolderContentAdapter.add(getString(R.string.no_files_in_directory));

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
        mFolderNameTV.setText(folder.getName());
        setupFolderContent(true);
    }
    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) { }
}
