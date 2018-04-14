package com.example.eakgun14.journeytracker.DataTypes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Journey implements Parcelable{
    private static final SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
    private String name;
    private Date createdOn;
    private String description;

    public Journey(String n, String d) {
        name = n;
        createdOn = new Date();
        description = d;
    }

    public Journey(Parcel in) {
        name = in.readString();
        createdOn = (Date) in.readValue(Date.class.getClassLoader());
        description = in.readString();
    }

    public String getName() {
        return name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getDescription() {
        return description;
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
        parcel.writeValue(createdOn);
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
}
