package com.example.eakgun14.journeytracker.DataTypes;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.net.URI;

public class LatLngURIPair {

    private LatLng coords;
    private String imageUri;

    public LatLngURIPair(LatLng latlng, String uri) {
        coords = latlng;
        imageUri = uri;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String toString() {
        return imageUri.toString();
    }
}
