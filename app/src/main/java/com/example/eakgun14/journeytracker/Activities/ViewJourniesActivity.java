package com.example.eakgun14.journeytracker.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ViewJourniesActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final String TAG = "ViewJourniesActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;

    private GoogleMap mMap;
    private List<List<LatLng>> routeList;

    private static final int[] colors = {Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN};
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journies);

        Intent intent = getIntent();
        String[] routes = intent.getStringArrayExtra("routes");

        routeList = new LinkedList<List<LatLng>>();

        Gson gson = new Gson();
        for (int i = 0; i < routes.length; i++) {
            List<LatLng> latLngList = gson.fromJson(routes[i], new TypeToken<List<LatLng>>(){}.getType());
            routeList.add(latLngList);
        }

        Log.d("latList", routeList.toString());

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
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        int i = 0;
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
            Toast.makeText(this, "BULLSHIT", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private int getNextColor() {
        int nextColor = colors[i];
        i++; i = i % colors.length;
        return nextColor;
    }

    // Request permissions to use fine and coarse location services,
    // If both permissions are granted, set activity state as permissions granted
    // and initialize map, which will trigger onMapReady callback
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                requestPermissions(permissions);
            }
        }else{
            requestPermissions(permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    // request permissions for ACCESS_LOCATION, whose result will trigger
    // onPermissionsRequestRequest callback
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE);
    }
}
