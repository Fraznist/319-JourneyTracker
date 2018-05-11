package com.example.eakgun14.journeytracker.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eakgun14.journeytracker.R;

public class ViewJourneyDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener2 mListener;

    private String name;
    private String route;
    private String photos;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        name = getArguments().getString("name");
        String desc = getArguments().getString("description");
        route = getArguments().getString("route");
        photos = getArguments().getString("photos");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_journey_info, null);

        EditText description = view.findViewById(R.id.dialog_view_journey_description);
        description.setText(desc.toString());
        description.setFocusable(false);

        Button viewButton = view.findViewById(R.id.dialog_view_journey_view);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogClick(ViewJourneyDialogFragment.this);
            }
        });

        Button photoButton = view.findViewById(R.id.dialog_view_journey_photos);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSecondaryDialogClick(ViewJourneyDialogFragment.this);
            }
        });

        builder.setTitle(name)
                .setView(view);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener2) context;
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
                mListener = (NoticeDialogListener2) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }
    }

    public String getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public String getPhotos() {
        return photos;
    }
}
