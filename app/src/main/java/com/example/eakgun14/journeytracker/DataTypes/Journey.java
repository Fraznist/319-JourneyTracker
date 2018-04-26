package com.example.eakgun14.journeytracker.DataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.SET_DEFAULT;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = @ForeignKey(entity = Journal.class,
                                    parentColumns = "id",
                                    childColumns = "journal_id",
                                    onDelete = SET_NULL))
public class Journey implements Journable {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "journal_id")
    private Integer journal_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    public Journey(String name, String description, Integer journal_id) {
        this.name = name;
        this.description = description;
        this.journal_id = journal_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(Integer journal_id) {
        this.journal_id = journal_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "id: " + id + ", journal_id: " + journal_id + ", name: " + name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
