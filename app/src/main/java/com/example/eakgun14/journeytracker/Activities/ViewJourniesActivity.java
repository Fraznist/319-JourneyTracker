package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ViewJourniesActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private List<List<LatLng>> routeList;

    private static final int[] colors =
            {Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN};
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journies);

        Intent intent = getIntent();
        String[] routes = intent.getStringArrayExtra("routes");

        // Randomize circular array starting index to add variables
        Random rand = new Random();
        i = rand.nextInt(6);

        routeList = new LinkedList<>();

        // deserialize every JSON file into a list of LatLng coordinates
        Gson gson = new Gson();
        for (int i = 0; i < routes.length; i++) {
            List<LatLng> latLngList = gson.fromJson(routes[i], new TypeToken<List<LatLng>>(){}.getType());
            Log.d("route", "size of route " + i + ": " + latLngList.size());
            routeList.add(latLngList);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        // Find out the bounding rectangle that surrounds all of the LatLng coordinates
        // that have to be displayed, then zoom the camera accordingly.
        int i = 0;  // Counter, newLatLngBounds raises an exception when there aren't any points
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (List<LatLng> route : routeList) {
            // Find the bounding latitude and longitude values, to determine where to center the camera
            for (LatLng latlng : route) {
                i++;
                builder.include(latlng);
            }

            PolylineOptions polyLine = new PolylineOptions()
                    .addAll(route)
                    .color(getNextColor())
                    .width(10.0f);
            mMap.addPolyline(polyLine);
        }
        if (i == 0) {
            // Don't try to
            Toast.makeText(this, "No points to display", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private int getNextColor() {
        // Use the color array in a circular manner.
        int nextColor = colors[i];
        i++; i = i % colors.length;
        return nextColor;
    }
}
