package com.example.eakgun14.journeytracker.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.eakgun14.journeytracker.DataTypes.Journable;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Custom class that can show both Journeys and Journals, trating them as Journable objects
public class JournableAdapter extends RecyclerView.Adapter<JournableAdapter.ViewHolder> {

    private List<Journable> Journables; // Journables currently shown in the RecyclerView
    private List<Journable> selectedJournables; // Journables that have their checkbox checked
    private List<Journable> JournablesToAdd;    // Journables scheduled to be stored on db
    private List<Journable> JournablesToDelete; // Journables scheduled to be removed from db
    private JournableAdapterListener jact;  // reference to the calling activity

    public JournableAdapter(Journable[] jjs, JournableAdapterListener ja) {
        Journables = new ArrayList<Journable>(Arrays.asList(jjs));
        selectedJournables = new ArrayList<Journable>();
        JournablesToAdd = new ArrayList<Journable>();
        JournablesToDelete = new ArrayList<Journable>();
        jact = ja;
    }

    public void add(int position, Journable item) {
        Journables.add(position, item);
        JournablesToAdd.add(item);
        notifyItemInserted(position);
    }

    public void removeSelected() {
        // Remove all selected journables from the list, and schedule them for
        // removal from the database
        for (Journable j : selectedJournables)
            remove(j);
        selectedJournables.clear();
    }

    public void remove(Journable j) {
        // Remove a journable from list, schedule it for removal from db

        int i = Journables.indexOf(j);  // index required to notify RecyclerView
        Journables.remove(i);

        int index = JournablesToAdd.indexOf(j); // Is the journable to remove is actually in database?
        if (index != -1)
            // apparently not, remove it from the scheduled addends to the database
            JournablesToAdd.remove(index);
        else
            // its already in database, schedule for removal
            JournablesToDelete.add(j);
        notifyItemRemoved(i);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JournableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Journable j = Journables.get(position);
        holder.name.setText(j.getName());
        holder.name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // activity specific behavior
                jact.onViewItemClicked(j);
            }
        });
        holder.selected.setChecked(false);
        // Update selectedJournables list
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectedJournables.add(j);
                else
                    selectedJournables.remove(j);
                Log.d("debug", selectedJournables.toString());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Journables.size();
    }

    public List<Journable> getJournablesToAdd() {
        return JournablesToAdd;
    }

    public List<Journable> getJournablesToDelete() {
        return JournablesToDelete;
    }

    public List<Journable> getSelectedJournables() {
        return selectedJournables;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public CheckBox selected;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            name = v.findViewById(R.id.nameLine);
            selected = v.findViewById(R.id.includeButton);
        }
    }
}

