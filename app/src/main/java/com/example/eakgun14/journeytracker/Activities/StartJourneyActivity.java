package com.example.eakgun14.journeytracker.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.Dialogs.NewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StartJourneyActivity extends FragmentActivity implements OnMapReadyCallback,
        NoticeDialogListener {
    private static final String TAG = "StartJourneyActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // google map fields
    private LocationRequest locationRequest;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // other fields
    private List<LatLng> journeyLocationsList;
    private Boolean recordingJourney = false;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_journey);
        journeyLocationsList = new LinkedList<LatLng>();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()//.fallbackToDestructiveMigration()
                .build();

        final Button mapButton = findViewById(R.id.start_tracking_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordingJourney) {
                    finishRecordingJourney();
                    mapButton.setText(R.string.start_journey);
                }
                else {
                    startRecordingJourney();
                    mapButton.setText(R.string.finish_journey);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(StartJourneyActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5)
                .setFastestInterval(1)
                .setSmallestDisplacement(5)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                            if (recordingJourney) {
                                journeyLocationsList.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                Toast.makeText(StartJourneyActivity.this, "devLoc: " + new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(StartJourneyActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    // request permissions for ACCESS_LOCATION, whose result will trigger
    // onPermissionsRequestRequest callback
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE);
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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(StartJourneyActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                moveCamera(newLocation, DEFAULT_ZOOM);

                Toast.makeText(StartJourneyActivity.this, "" + new LatLng(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
                if (recordingJourney) {
                    journeyLocationsList.add(newLocation);

                    mMap.clear();
                    PolylineOptions polyLine = new PolylineOptions()
                            .addAll(journeyLocationsList)
                            .color(Color.CYAN)
                            .width(10.0f);
                    mMap.addPolyline(polyLine);
                }
            }
        }
    };

    @Override
    public void onDialogClick(DialogFragment dialog) {

        try {
            NewJourneyDialogFragment dial = (NewJourneyDialogFragment) dialog;

            String name = dial.getNameText().getText().toString();
            String desc = dial.getDescText().getText().toString();

            Integer j_id = dial.getSelectedJournalID();

            Gson gson = new Gson();

            String json = gson.toJson(journeyLocationsList);

            Toast.makeText(this, "" + json, Toast.LENGTH_LONG).show();

            Journey j = new Journey(name, desc, j_id, json);
            db.journeyDao().insertAll(j);
        }
        catch (ClassCastException e) {
            throw new ClassCastException(dialog.toString()
                    + " must extend NewJourneyDialogFragment");
        }
    }

    private void showFinishDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment frag =  new NewJourneyDialogFragment();

        Bundle args = new Bundle();
        List<Journal> journals = db.journalDao().getAllJournals();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (Journal j : journals) {
            names.add(j.getName());
            ids.add(j.getId());
        }

        args.putStringArrayList("journal names", names);
        args.putIntegerArrayList("journal ids", ids);

        frag.setArguments(args);
        frag.show(fm, "fragment_save_journey");
    }

    private void startRecordingJourney() {
        recordingJourney = true;
        getDeviceLocation();
    }

    private void finishRecordingJourney() {
        recordingJourney = false;
        showFinishDialog();
    }
}
