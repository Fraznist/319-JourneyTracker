package com.example.eakgun14.journeytracker.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.eakgun14.journeytracker.DataTypes.Journal;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal")
    List<Journal> getAllJournals();

    @Insert
    void insertAll(List<Journal> journals);

    @Delete
    void deleteAll(List<Journal> journals);
}
