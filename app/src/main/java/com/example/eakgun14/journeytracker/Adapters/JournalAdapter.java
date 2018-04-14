package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
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
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {
    private AppDatabase db;

    private List<Journal> journals;
    private List<Journal> selectedJournals;
    private JournalsActivity jact;

    public JournalAdapter(List<Journal> jjs, JournalsActivity ja, AppDatabase database) {
        journals = jjs;
        selectedJournals = new ArrayList<Journal>();
        jact = ja;
        this.db = database;
    }

    public void add(int position, Journal item) {
        journals.add(position, item);
        if (item == null)
            Log.d("debug", "NULL");
        db.journalDao().insertAll(item);
        notifyItemInserted(position);
    }

    public void removeSelected() {
        for (Journal j : selectedJournals) {
            int i = journals.indexOf(j);
            remove(i);
        }
        selectedJournals.clear();
    }

    public void remove(int position) {
        Journal j = journals.remove(position);
        db.journalDao().deleteAll(j);
        notifyItemRemoved(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JournalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Journal j = journals.get(position);
        holder.name.setText(j.getName());
        holder.name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jact.startJourniesActivity(j);
            }
        });
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectedJournals.add(j);
                else
                    selectedJournals.remove(j);
                Log.d("debug", "Currently selected: " + selectedJournals);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return journals.size();
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
