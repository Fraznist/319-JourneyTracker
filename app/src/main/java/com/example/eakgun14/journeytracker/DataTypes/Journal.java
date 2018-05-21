package com.example.eakgun14.journeytracker.DataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Journal implements Journable{
    // Relations between Journeys and containing journals are handled at the database level
    // maintaining a list of journeys for every journal in memory is redundant.
    // all journals can be in memory at once, but at that state their journey contents can't be
    // accessed anyways.

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    public Journal(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "id: " + this.getId() + " name: " + this.getName();
    }

}
