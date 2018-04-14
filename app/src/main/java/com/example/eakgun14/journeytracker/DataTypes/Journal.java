package com.example.eakgun14.journeytracker.DataTypes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Journal implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    public Journal(String name) {
        this.name = name;
    }

    // Required constructor for Parcelable interface, never really used in source code.
    public Journal(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Dummy implementations for Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }

    public static final Parcelable.Creator<Journal> CREATOR = new Parcelable.Creator<Journal>() {

        public Journal createFromParcel(Parcel in) {
            return new Journal(in);
        }

        public Journal[] newArray(int size) {
            return new Journal[size];
        }
    };
}
