package com.example.eakgun14.journeytracker.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.eakgun14.journeytracker.DataTypes.Journable;
import com.example.eakgun14.journeytracker.DataTypes.Journal;

import java.util.Collection;
import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal")
    List<Journal> getAllJournals();

    @Insert
    void insertAll(Journal... journals);

    @Insert
    void insertAll(Collection<Journal> journals);

    @Delete
    void deleteAll(Journal... journals);

    @Delete
    void deleteAll(Collection<Journal> journals);
}
