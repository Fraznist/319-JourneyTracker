package com.example.eakgun14.journeytracker.DataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.text.SimpleDateFormat;

@Entity
public class Journey implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "journal_id")
    private int journal_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    public Journey(String name, String description, int journal_id) {
        this.name = name;
        this.description = description;
        this.journal_id = journal_id;
    }

    public Journey(Parcel in) {
        name = in.readString();
        description = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(int journal_id) {
        this.journal_id = journal_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return name;
    }

    // Dummy implementations for Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<Journey> CREATOR = new Parcelable.Creator<Journey>() {

        public Journey createFromParcel(Parcel in) {
            return new Journey(in);
        }

        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
