package com.example.eakgun14.journeytracker.RouteService;

import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RouteManager /* implements Parcelable */{
    private static final double MAX_ANGLE_VARIATION = 2;

    private static RouteManager instance = new RouteManager();

    private ArrayList<LatLng> route;
    private double angleToMatch;

    private RouteManager() {
        route = new ArrayList<>();
    }

    public static RouteManager getInstance() {
        if (instance == null)
            instance = new RouteManager();
        return instance;
    }

    // adds the new coordinates to the end of the list.
    // If adding newLoc represents a change of direction (different angle with angleToMatch)
    // It is appended to the end of the list, and angleToMatch is updated as the
    // angle of the line segment between new addition and the element preceding it.
    // If it is mostly in line with angleToMatch, it replaces the last element of the list,
    // and angleToMatch remains unchanged.
    public void add(LocationResult locationResult) {

        List<Location> locationList = locationResult.getLocations();
        if (locationList.size() > 0) {
            //The last location in the list is the newest
            Location location = locationList.get(locationList.size() - 1);
            // Only concerned about coordinates
            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());

            if (route.isEmpty())
                route.add(newLoc);
            else if (route.size() == 1) {
                route.add(newLoc);
                angleToMatch = angleBetweenLatLng(route.get(0), newLoc);
            }
            else {
                LatLng lineStart = route.get(route.size() - 2);
                double angle = angleBetweenLatLng(lineStart, newLoc);

                if (Math.abs(angleToMatch - angle) < MAX_ANGLE_VARIATION)
                    route.set(route.size() - 1, newLoc);
                else {
                    route.add(newLoc);
                    angleToMatch = angleBetweenLatLng(route.get(route.size() - 2), newLoc);
                }
            }
        }
    }

    private static double angleBetweenLatLng(LatLng start, LatLng fin) {
        double rad = Math.atan2(fin.longitude - start.longitude, fin.latitude - start.latitude);
        return Math.toDegrees(rad);
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void clear() {
        route.clear();
        angleToMatch = 0;
    }
}
