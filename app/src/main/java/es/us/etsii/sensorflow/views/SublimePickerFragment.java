package es.us.etsii.sensorflow.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker.RecurrenceOption;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;

public class SublimePickerFragment extends DialogFragment {

    // -------------------------- ATTRIBUTES -------------------------

    DateFormat mDateFormatter, mTimeFormatter;
    SublimePicker mSublimePicker;
    SublimeCallback mCallback;
    int mId;

    // ------------------------- CONSTRUCTOR -------------------------

    public SublimePickerFragment() {
        // You can also override 'formatDate(Date)' & 'formatTime(Date)' to supply custom formats.
        mDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        mTimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        mTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSublimePicker = (SublimePicker) getActivity().getLayoutInflater()
                .inflate(R.layout.sublime_picker, container);

        Bundle arguments = getArguments();

        // Retrieve SublimeOptions, if they are null, default
        SublimeOptions options = null;
        if (arguments != null)
            options = arguments.getParcelable(Constants.SUBLIME_OPTIONS);

        mSublimePicker.initializePicker(options, mListener);
        return mSublimePicker;
    }

    // --------------------------- LISTENER --------------------------

    // Set activity callback
    public SublimePickerFragment withCallbackAndId(int id, SublimeCallback callback) {
        mId = id;
        mCallback = callback;
        return this;
    }

    private SublimeListenerAdapter mListener = new SublimeListenerAdapter() {
        @Override
        public void onCancelled() {
            if (mCallback != null)
                mCallback.onCancelled();

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss();
        }

        @Override
        public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
                                            SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            if (mCallback != null)
                mCallback.onDateTimeRecurrenceSet(selectedDate, hourOfDay, minute, mId);

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss();
        }
    };

    // For communicating with the activity - Customizable
    interface SublimeCallback {
        void onCancelled();

        void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, int id);
    }
}
