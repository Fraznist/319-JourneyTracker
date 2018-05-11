package com.example.eakgun14.journeytracker.RouteService;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RouteManager {
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
    public void add(LatLng coordinates) {
        if (route.isEmpty())
            route.add(coordinates);
        else if (route.size() == 1) {
            route.add(coordinates);
            angleToMatch = angleBetweenLatLng(route.get(0), coordinates);
        }
        else {
            LatLng lineStart = route.get(route.size() - 2);
            double angle = angleBetweenLatLng(lineStart, coordinates);

            if (deltaAngle(angleToMatch, angle) < MAX_ANGLE_VARIATION)
                route.set(route.size() - 1, coordinates);
            else {
                route.add(coordinates);
                angleToMatch = angleBetweenLatLng(route.get(route.size() - 2), coordinates);
            }
        }
    }

    private static double angleBetweenLatLng(LatLng start, LatLng fin) {
        double rad = Math.atan2(fin.longitude - start.longitude, fin.latitude - start.latitude);
        return Math.toDegrees(rad);
    }

    private static double deltaAngle(double a1, double a2) {
        double phi = Math.abs(a1 - a2);
        return phi > 180 ? 360 - phi : phi;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void clear() {
        route.clear();
        angleToMatch = 0;
    }

    public LatLng getLast() {
        return route.get(route.size() - 1);
    }

    public boolean isRouteEmpty() {
        return route.isEmpty();
    }
}
