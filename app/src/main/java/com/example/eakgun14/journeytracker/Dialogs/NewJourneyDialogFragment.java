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
import android.widget.Spinner;

import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;

public class NewJourneyDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;

    private EditText nameText;
    private EditText descText;
    private Spinner journalSpinner;
    private ArrayList<Integer> ids;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_save_journey, null);
        nameText = view.findViewById(R.id.dialog_create_journal_name);
        descText = view.findViewById(R.id.dialog_journey_save_description);
        journalSpinner = view.findViewById(R.id.dialog_save_journey_spinner);

        ArrayList<String> names = getArguments().getStringArrayList("journal names");
        ids = getArguments().getIntegerArrayList("journal ids");

        names.add(0, "Don't Assing to any Journal");
        ids.add(0, -1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, names);
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

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public Integer getSelectedJournalID() {
        int pos = journalSpinner.getSelectedItemPosition();
        return ids.get(pos);
    }
}
