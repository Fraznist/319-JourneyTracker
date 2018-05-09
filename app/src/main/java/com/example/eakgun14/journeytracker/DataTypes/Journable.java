package com.example.eakgun14.journeytracker.DataTypes;

// Interface for both journey and journal classes
public interface Journable {

    String getName();

    Integer getId();

    void setName(String name);

    void setId(Integer id);
}
