package com.example.eakgun14.journeytracker.Adapters;

import android.support.annotation.NonNull;
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

import java.util.List;

// Custom class that can show both Journeys and Journals, trating them as Journable objects
public class JournableAdapter<T extends Journable>
        extends RecyclerView.Adapter<JournableAdapter.ViewHolder> {

    private DynamicViewManager<T> viewManager;
    private ViewAdapterListener jact;  // reference to the calling activity

    public JournableAdapter(List<T> jjs, ViewAdapterListener ja) {
        viewManager = new DynamicViewManager<>(jjs);
        jact = ja;
    }

    public void add(int position, T item) {
        viewManager.add(position, item);
        notifyItemInserted(position);
    }

    public void removeSelected() {
        for (T j : viewManager.getSelected())
            remove(j);
        viewManager.clearSelected();
    }

    private void remove(T j) {
        int i = viewManager.remove(j);
        notifyItemRemoved(i);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public JournableAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(
            @NonNull final JournableAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final T j = viewManager.getCurrent().get(position);
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
                    viewManager.addAsSelected(j);
                else
                    viewManager.removeFromSelected(j);
                Log.d("debug", viewManager.getSelected().toString());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return viewManager.getCurrent().size();
    }

    public List<T> getJournablesToAdd() {
        return viewManager.getToAdd();
    }

    public List<T> getJournablesToDelete() {
        return viewManager.getToDelete();
    }

    public List<T> getSelectedJournables() {
        return viewManager.getSelected();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        CheckBox selected;
        public View layout;

        ViewHolder(View v) {
            super(v);
            layout = v;
            name = v.findViewById(R.id.nameLine);
            selected = v.findViewById(R.id.includeButton);
        }
    }
}

