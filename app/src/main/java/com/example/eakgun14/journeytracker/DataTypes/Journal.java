package com.example.eakgun14.journeytracker.DataTypes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Journal implements Parcelable{
    private String name;
    private List<Journey> journeyList;

    public Journal(String n) {
        name = n;
        journeyList = new ArrayList<Journey>();
    }

    // Required constructor for Parcelable interface, never really used in source code.
    public Journal(Parcel in) {
        name = in.readString();
        journeyList = in.readArrayList(Journey.class.getClassLoader());
    }

    public void addJourney(Journey j) {
        journeyList.add(j);
    }

    public void addAllJourneys(Collection<Journey> jCol) {
        for (Journey j : jCol)
            journeyList.add(j);
    }

    public String getName() {
        return name;
    }

    public List<Journey> getJourneyList() {
        return journeyList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return journeyList.toString();
    }

    // Dummy implementations for Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeList(journeyList);
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
