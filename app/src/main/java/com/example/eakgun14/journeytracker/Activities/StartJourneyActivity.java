package com.example.eakgun14.journeytracker.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener2;
import com.example.eakgun14.journeytracker.RouteService.AudioManager;
import com.example.eakgun14.journeytracker.RouteService.CameraManager;
import com.example.eakgun14.journeytracker.RouteService.RouteManager;
import com.example.eakgun14.journeytracker.DataTypes.WeatherInfo;
import com.example.eakgun14.journeytracker.Dialogs.NewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;
import com.example.eakgun14.journeytracker.RouteService.RouteService;
import com.example.eakgun14.journeytracker.RouteService.RouteServiceCallbacks;
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
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartJourneyActivity extends FragmentActivity implements OnMapReadyCallback,
        NoticeDialogListener2, RouteServiceCallbacks {

    // UI objects
    private TextView mTemperature;
    private TextView mCityName;
    private ImageView mWeatherImage;

    // google map fields
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;

    // other fields
    private RouteManager routeManager;
    private CameraManager cameraManager;
    private AudioManager audioManager;
    private RouteService routeService;
    private boolean routeServiceBound = false;
    private Boolean recordingJourney = false;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_journey);

        // Check if route service is running. If so, change activity state.
        if (RouteService.serviceRunning)
            recordingJourney = true;


        routeManager = RouteManager.getInstance();
        cameraManager = CameraManager.getInstance();
        cameraManager.setContext(this.getApplicationContext());

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //@TODO restructure to avoid UI thread queries.
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()//.fallbackToDestructiveMigration()
                .build();

        final Button mapButton = findViewById(R.id.start_tracking_button);
        if (recordingJourney) mapButton.setText(R.string.finish_journey);
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

        final FloatingActionButton camButton = findViewById(R.id.take_photo_button);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        mTemperature = findViewById(R.id.start_journey_temperature);
        mCityName = findViewById(R.id.start_journey_city);
        mWeatherImage = findViewById(R.id.start_journey_weather_icon);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(StartJourneyActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to Service
        Intent intent = new Intent(this, RouteService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (routeServiceBound) {
            routeService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            routeServiceBound = false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        // Specify the sensitivity of the GPS sensor
        // @TODO change GPS sensitivities to more reasonable values after testing.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5)
                .setFastestInterval(1)
                .setSmallestDisplacement(5)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Called every time a location change is registered by our location request
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    // Only concerned about coordinates
                    LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Center the camera on the user
                    moveCamera(newLocation);
                }
            }
        };

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                //Register our location request
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            //Register our location request
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    private void startRecordingJourney() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            recordingJourney = true;
            // Start RouteSerivce
            Intent intent = new Intent(this, RouteService.class);
            startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void finishRecordingJourney() {
        recordingJourney = false;
        showFinishDialog();
    }

    // Move the camera to the specified latitude and longitude with a default zoom value
    // @TODO keep users custom zoom value on camera movements if the user tampers with the zoom
    private void moveCamera(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    @Override
    public void updateMap() {
        // Redraw the entire polyline, I do not know if we can modify an existing
        // polyline, rather than removing and redrawing everything.
        mMap.clear();
        PolylineOptions polyLine = new PolylineOptions()
                .addAll(routeManager.getRoute())
                .color(Color.CYAN)
                .width(10.0f);
        mMap.addPolyline(polyLine);
    }

    @Override
    public void updateWeather(WeatherInfo weather) {
        mTemperature.setText(weather.getTemperature());
        mCityName.setText(weather.getCityName());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getDrawableName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }

    private void showFinishDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment frag =  new NewJourneyDialogFragment();

        // passing only name and id of journal, since Journals aren't parcelable,
        // maybe they should be
        // These are used to fill a dropdown spinner, from which the user picks desired folder
        Bundle args = new Bundle();
        List<Journal> journals = db.journalDao().getAllJournals();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        for (Journal j : journals) {
            names.add(j.getName());
            ids.add(j.getId());
        }

        args.putStringArrayList("journal names", names);
        args.putIntegerArrayList("journal ids", ids);

        // Show a dialog box to specify details of the route
        frag.setArguments(args);
        frag.show(fm, "fragment_save_journey");
    }

    // NoticeDialogListener Callback
    @Override
    public void onDialogClick(DialogFragment dialog) {

        // Store the recorded route into the database, with the name, description, and folder
        // Specified in the dialog box.
        try {
            NewJourneyDialogFragment dial = (NewJourneyDialogFragment) dialog;

            String name = dial.getNameText().getText().toString();
            String desc = dial.getDescText().getText().toString();

            Integer j_id = dial.getSelectedJournalID();

            // Serialize list of points into a JSON string.
            Gson gson = new Gson();
            String json = gson.toJson(routeManager.getRoute());
            String photosJson = gson.toJson(cameraManager.getImageUriList());
            Log.d("serialize", photosJson);

            // Insert into database.
            Journey j = new Journey(name, desc, j_id, json, photosJson);
            db.journeyDao().insertAll(j);

            routeManager.clear();
            cameraManager.clear();

            changeAudioFileName(name);

            stopRouteService();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(dialog.toString()
                    + " must extend NewJourneyDialogFragment");
        }
    }

    @Override
    public void onSecondaryDialogClick(DialogFragment dialog) {
        checkAudioPermission();

        if (audioManager == null)
            audioManager = new AudioManager(this.getApplicationContext());

        audioManager.onRecord("tempAudio");
    }

    private void changeAudioFileName(String newJourneyName) {
        String sourceName = "tempAudio";
        File parentDirectory = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        File sourceAudio = new File(parentDirectory, sourceName);

        if(!sourceAudio.exists())
            return;

        if (sourceAudio.renameTo(new File(parentDirectory, newJourneyName)))
            Log.d("rename", "YE BOIII");
        else
            Log.d("rename", "no boi :(");
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            RouteService.LocalBinder binder = (RouteService.LocalBinder) service;
            routeService = binder.getService();
            routeServiceBound = true;
            routeService.setCallbacks(StartJourneyActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            routeServiceBound = false;
        }
    };

    private void stopRouteService() {
        // Stop the service when the user is done
        if (routeServiceBound) {
            routeService.stopSelf();
            routeServiceBound = false;
        }
    }

    // Check whether the Location service permission is granted by the OS, and request it if not.
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // App doesn't have location services permission from OS

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
                        }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    // Check whether the audio recording permission is granted by the OS, and request it if not.
    public static final int MY_PERMISSIONS_RECORD_AUDIO = 88;
    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // App doesn't have location services permission from OS

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the record audio permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(StartJourneyActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        MY_PERMISSIONS_RECORD_AUDIO );
                            }
                        }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_RECORD_AUDIO );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
            }
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
                else finish();
            }
        }
    }

    private static final int TAKE_PICTURE = 5555;
    public void takePhoto() {
        if (recordingJourney && !routeManager.isRouteEmpty()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = cameraManager.setImageUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE :
                if (resultCode == Activity.RESULT_OK)
                    cameraManager.cacheImage(routeManager.getLast());
        }
    }
}
