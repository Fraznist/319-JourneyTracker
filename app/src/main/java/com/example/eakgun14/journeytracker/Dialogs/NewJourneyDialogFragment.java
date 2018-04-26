package com.example.eakgun14.journeytracker.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import org.w3c.dom.Text;

import java.util.List;

public class NewJourneyDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogClick(NewJourneyDialogFragment dialog);
        public AppDatabase getAppDatabase();
    }

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    private EditText nameText;
    private EditText descText;
    private Spinner journalSpinner;
    private AppDatabase db;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_save_journey, null);
        nameText = view.findViewById(R.id.dialog_journey_save_name);
        descText = view.findViewById(R.id.dialog_journey_save_description);
        journalSpinner = view.findViewById(R.id.dialog_save_journey_spinner);

        db = mListener.getAppDatabase();

        List<Journal> jjs = db.journalDao().getAllJournals();
        jjs.add(0, new Journal("Don't Assing to any Journal"));

        ArrayAdapter<Journal> adapter = new ArrayAdapter<Journal>(
                getActivity(), android.R.layout.simple_spinner_item, jjs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalSpinner.setAdapter(adapter);

        builder.setTitle(R.string.journey_dialog_title)
                .setView(view)
                .setNeutralButton(R.string.journey_dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogClick(NewJourneyDialogFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (NoticeDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }
    }

    public EditText getNameText() {
        return nameText;
    }

    public EditText getDescText() {
        return descText;
    }

    public Spinner getSpinner() {
        return journalSpinner;
    }
}
