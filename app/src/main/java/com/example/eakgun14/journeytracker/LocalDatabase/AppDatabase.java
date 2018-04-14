package com.example.eakgun14.journeytracker.LocalDatabase;

import android.arch.persistence.room.RoomDatabase;

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;

@android.arch.persistence.room.Database(entities = {Journal.class, Journey.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JournalDao journalDao();
    public abstract JourneyDao journeyDao();
}
