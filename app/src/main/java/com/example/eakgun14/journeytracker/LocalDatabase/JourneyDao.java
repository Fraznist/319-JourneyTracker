package com.example.eakgun14.journeytracker.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.eakgun14.journeytracker.DataTypes.Journey;

import java.util.List;

@Dao
public interface JourneyDao {

    @Query("SELECT * FROM journey")
    List<Journey> getAllJourneys();

    @Query("SELECT * FROM journey WHERE journal_id IS :j_id")
    List<Journey> getAllJourneysInJournal(int j_id);

    @Query("UPDATE journey SET journal_id = :j_id WHERE id IN(:ids)")
    void moveJourneys(List<Integer> ids, int j_id);

    @Insert
    void insertAll(Journey... journeys);

    @Insert
    void insertAll(List<Journey> journeys);

    @Delete
    void deleteAll(List<Journey> journeys);
}
