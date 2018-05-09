package com.example.eakgun14.journeytracker.Activities;

import android.Manifest;
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
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.RouteService.RouteManager;
import com.example.eakgun14.journeytracker.DataTypes.WeatherInfo;
import com.example.eakgun14.journeytracker.Dialogs.NewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class StartJourneyActivity extends FragmentActivity implements OnMapReadyCallback,
        NoticeDialogListener, RouteServiceCallbacks {
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String APP_ID = "e72ca729af228beabd5d20e3b7749713";
    private static float DEF_ZOOM = 15f;

    // UI objects
    private TextView mTemperature;
    private TextView mCityName;
    private ImageView mWeatherImage;

    // google map fields
    private LocationRequest locationRequest;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // other fields
    private RouteManager routeManager;
    private RouteService routeService;
    private boolean routeServiceBound = false;
    private LatLng oldLoc = null;
    private Boolean recordingJourney = false;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_journey);

        // Restore saved state so that a route being recorded isn't lost on orientation change
        if (savedInstanceState != null)
            recordingJourney = savedInstanceState.getBoolean("KEY_BUTTON_STATE");

        routeManager = RouteManager.getInstance();

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

    // Move the camera to the specified latitude and longitude with a default zoom value
    // @TODO keep users custom zoom value on camera movements if the user tampers with the zoom
    private void moveCamera(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEF_ZOOM));
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
            }

        }
    }

    // Called every time a location change is registered by our location request
    LocationCallback mLocationCallback = new LocationCallback() {
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
                // Request local weather data from API and update the UI
                // Only if its the first onLocationResult callback or the device has
                // travelled at least 10km.
                if (oldLoc == null) {
                    oldLoc = newLocation;
                    getLatLngWeather(newLocation);
                }
                else if (distance(oldLoc, newLocation) > 10) {
                    oldLoc = newLocation;
                    getLatLngWeather(newLocation);
                }
            }
        }
    };

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

    public void getLatLngWeather(LatLng coordinates) {
        RequestParams params = new RequestParams();
        params.put("lat", coordinates.latitude);
        params.put("lon", coordinates.longitude);
        params.put("appid", APP_ID);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                WeatherInfo weatherData = WeatherInfo.fromJSONObject(response);
                updateWeatherUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Toast.makeText(StartJourneyActivity.this,
                        "HTTP Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWeatherUI(WeatherInfo weather) {
        mTemperature.setText(weather.getTemperature());
        mCityName.setText(weather.getCityName());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getDrawableName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
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

            // Insert into database.
            Journey j = new Journey(name, desc, j_id, json);
            db.journeyDao().insertAll(j);

            routeManager.clear();

            stopRouteService();
        }
        catch (ClassCastException e) {
            throw new ClassCastException(dialog.toString()
                    + " must extend NewJourneyDialogFragment");
        }
    }

    private void showFinishDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment frag =  new NewJourneyDialogFragment();

        // passing only name and id of journal, since Journals aren't parcelable,
        // maybe they should be
        // These are used to fill a dropdown spinner, from which the user picks desired folder
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

        // Show a dialog box to specify details of the route
        frag.setArguments(args);
        frag.show(fm, "fragment_save_journey");
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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("KEY_BUTTON_STATE", recordingJourney);
    }

    // calculate the distance between 2 LatLng points, using the Haversine Method without elevation
    public static double distance(LatLng coord1, LatLng coord2) {

        double lat1 = coord1.latitude, lon1 = coord1.longitude;
        double lat2 = coord2.latitude, lon2 = coord2.longitude;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }
}
