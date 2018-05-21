package com.example.eakgun14.journeytracker.DataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = @ForeignKey(entity = Journal.class,
                                    parentColumns = "id",
                                    childColumns = "journal_id",
                                    onDelete = SET_NULL),
        indices = {@Index(value = {"journal_id"})})
public class Journey implements Journable {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    // Reference to containing journal
    @ColumnInfo(name = "journal_id")
    private Integer journal_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "route")
    private String route;

    @ColumnInfo(name = "coordinate_photos")
    private String coordinate_photos;

    public Journey(String name, String description, Integer journal_id, String route, String coordinate_photos) {
        this.name = name;
        this.description = description;
        this.journal_id = journal_id;
        this.route = route;
        this.coordinate_photos = coordinate_photos;
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

    public String getRoute() {
        return route;
    }

    public String getCoordinate_photos() {
        return coordinate_photos;
    }
}
