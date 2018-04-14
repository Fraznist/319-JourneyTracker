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

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.R;

import java.util.ArrayList;
import java.util.List;

public class JourniesAdapter extends RecyclerView.Adapter<JourniesAdapter.ViewHolder> {
    private List<Journey> journies;
    private List<Journey> selectedJournies;
    private Context mContext;

    public JourniesAdapter(List<Journey> jjs, Context ct) {
        journies = jjs;
        selectedJournies = new ArrayList<Journey>();
        mContext = ct;
    }

    public void add(int position, Journey item) {
        journies.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        journies.remove(position);
        notifyItemRemoved(position);
    }

    public void removeSelected() {
        for (Journey j : selectedJournies){
            int i = journies.indexOf(j);
            remove(i);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JourniesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        final Journey j = journies.get(position);
        holder.name.setText(j.getName());
        holder.name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectedJournies.add(j);
                else
                    selectedJournies.remove(j);
                Log.d("debug", "Currently selected: " + selectedJournies);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return journies.size();
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

