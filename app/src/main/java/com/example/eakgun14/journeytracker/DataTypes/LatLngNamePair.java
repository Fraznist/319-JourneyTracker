package com.example.eakgun14.journeytracker.DataTypes;

import com.google.android.gms.maps.model.LatLng;

public class LatLngNamePair {

    private LatLng coords;
    private String name;

    public LatLngNamePair(LatLng latlng, String uri) {
        coords = latlng;
        name = uri;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name.toString();
    }
}
