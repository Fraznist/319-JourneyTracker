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

import com.example.eakgun14.journeytracker.Activities.JournalsActivity;
import com.example.eakgun14.journeytracker.DataTypes.Journable;
import com.example.eakgun14.journeytracker.DataTypes.listActivity;
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JournableAdapter extends RecyclerView.Adapter<JournableAdapter.ViewHolder> {

    private List<Journable> Journables;
    private List<Journable> selectedJournables;
    private List<Journable> JournablesToAdd;
    private List<Journable> JournablesToDelete;
    private listActivity jact;

    public JournableAdapter(Journable[] jjs, listActivity ja) {
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
        for (Journable j : selectedJournables)
            remove(j);
        selectedJournables.clear();
    }

    public void remove(Journable j) {
        int i = Journables.indexOf(j);
        Journables.remove(i);

        int index = JournablesToAdd.indexOf(j);
        if (index != -1)
            JournablesToAdd.remove(index);
        else
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
                jact.startActivity(j);
            }
        });
        holder.selected.setChecked(false);
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

