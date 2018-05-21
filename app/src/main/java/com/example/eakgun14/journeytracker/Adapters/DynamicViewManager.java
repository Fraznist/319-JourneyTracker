package com.example.eakgun14.journeytracker.Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicViewManager<T> {

    private List<T> current; // Objects currently shown in the View
    private List<T> selected; // Objects that are selected
    private List<T> toAdd;    // Objects scheduled to be stored on db
    private List<T> toDelete; // Objects scheduled to be removed from db
    private Map<Integer, List<Integer>> moveMap; // {journal_id of destination, ids of journeys to move}

    DynamicViewManager(List<T> ogList) {
        if (ogList == null)
            current = new ArrayList<>();
        else current = ogList;
        selected = new ArrayList<>();
        toAdd = new ArrayList<>();
        toDelete = new ArrayList<>();
        moveMap = new HashMap<>();
    }

    public void add(int position, T item) {
        current.add(position, item);
        toAdd.add(item);
    }

    public int remove(T t) {
        // Remove a journable from list, schedule it for removal from db

        int i = current.indexOf(t);  // index required to notify RecyclerView
        current.remove(i);

        int index = toAdd.indexOf(t); // Is the journable to remove is actually in database?
        if (index != -1)
            // apparently not, remove it from the scheduled addends to the database
            toAdd.remove(index);
        else
            // its already in database, schedule for removal
            toDelete.add(t);

        return i;
    }

    public void addAsSelected(T t) {
        selected.add(t);
    }

    public void removeFromSelected(T t) {
        selected.remove(t);
    }

    public void clearSelected() {
        selected.clear();
    }

    public void clearModifications() {
        selected.clear();
        toAdd.clear();
        toDelete.clear();
        moveMap.clear();
    }

    public int size() {
        return current.size();
    }

    public T get(int index) {
        return current.get(index);
    }

    public List<T> getCurrent() {
        return current;
    }

    public List<T> getSelected() {
        return selected;
    }

    public List<T> getToAdd() {
        return toAdd;
    }

    public List<T> getToDelete() {
        return toDelete;
    }

    public Map<Integer, List<Integer>> getMoveMap() {
        return moveMap;
    }
}
