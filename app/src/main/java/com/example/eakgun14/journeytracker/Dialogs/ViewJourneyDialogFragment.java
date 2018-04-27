package com.example.eakgun14.journeytracker.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eakgun14.journeytracker.R;

public class ViewJourneyDialogFragment extends DialogFragment {

    EditText description;
    Button viewButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String name = getArguments().getString("name");
        String desc = getArguments().getString("description");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_journey_info, null);

        description = view.findViewById(R.id.dialog_view_journey_description);
        description.setText(desc.toString());
        description.setFocusable(false);

        viewButton = view.findViewById(R.id.dialog_view_journey_view);

        builder.setTitle(name)
                .setView(view);

        return builder.create();
    }
}
